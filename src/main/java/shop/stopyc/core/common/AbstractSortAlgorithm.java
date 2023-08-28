package shop.stopyc.core.common;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import shop.stopyc.config.DynamicHotCacheProperties;
import shop.stopyc.entry.DynamicHotCacheObj;
import shop.stopyc.entry.EventContent;
import shop.stopyc.util.PublisherUtil;
import shop.stopyc.util.RedisUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 抽象热点缓存排序算法类
 * @author: stop.yc
 * @create: 2023-08-27 16:47
 **/
@Component
@Setter
@Slf4j
public abstract class AbstractSortAlgorithm {

    @Resource
    protected RedisUtil redisUtil;

    @Resource
    protected DynamicHotCacheProperties properties;

    private static Set<Object> distinctHotCache;
    private static int diffCnt;
    @Resource
    private PublisherUtil publisherUtil;

    @PostConstruct
    public void init() {
        DynamicHotCacheFactory.putStrategy(strategyName(), this);
    }

    /**
     * 缓存对象访问入口
     *
     * @param key：配置的缓存对象池
     * @param data:        缓存对象的标识
     */
    public abstract void access(String key, Object data);

    public void objAccess(String key, Object data) {
        access(key, data);
        publisherUtil.objAccess(this, new EventContent(key, data));
    }

    /**
     * 热点缓存排序算法名称
     *
     * @return： 热点缓存排序算法名称
     */
    protected abstract String strategyName();

    /**
     * 获取有序的抽样数据
     */
    protected List<DynamicHotCacheObj> sampling(long sampleNums) {
        Map<String, DynamicHotCacheObj> map = redisUtil.mGetRandom(properties.getAllKeyPoolPrefix(), sampleNums);
        return getSortedHotObj(map);
    }


    protected List<DynamicHotCacheObj> getSortedHotObj(Map<String, DynamicHotCacheObj> sampleMaps) {
        return sampleMaps.values().stream().sorted(((o1, o2) -> Long.compare(o2.getSort(), o1.getSort()))).collect(Collectors.toList());
    }

    /**
     * 获取热点缓存池
     *
     * @return: 热点缓存池
     */
    protected Set<ZSetOperations.TypedTuple<String>> getHotCachePool() {
        return redisUtil.zGet(properties.getCachePoolPrefix());
    }

    public int tryUpdateHotCachePool(long sampleNums) {
        // 1.对所有数据进行抽样
        List<DynamicHotCacheObj> sampleList = sampling(sampleNums);
        // 2.获取旧的热点缓存池
        Set<ZSetOperations.TypedTuple<String>> hotCachePool = getHotCachePool();

        // 3.新的热点缓存池
        Set<ZSetOperations.TypedTuple<String>> newHotCachePool = new LinkedHashSet<>(properties.getHotCacheNums());

        // 4.判断热点缓存池是否已满
        if (sampleList.size() + hotCachePool.size() <= properties.getHotCacheNums()) {
            // 4.1 未满直接添加
            poolCapacityNotFull(sampleList, hotCachePool, newHotCachePool);
        } else {
            // 4.3 已满，需要替换旧的热点缓存
            poolCapacityFull(sampleList, hotCachePool, newHotCachePool);
        }
        redisUtil.del(properties.getCachePoolPrefix());
        redisUtil.zSet(properties.getCachePoolPrefix(), newHotCachePool);
        return diffCnt;
    }

    private void poolCapacityFull(List<DynamicHotCacheObj> sampleList, Set<ZSetOperations.TypedTuple<String>> hotCachePool, Set<ZSetOperations.TypedTuple<String>> newHotCachePool) {
        log.info("热点缓存池已满，需要替换旧的热点缓存");
        // 1. 获取id去重列表
        Map<Object, DynamicHotCacheObj> distinctCacheMap = getDistinctCacheMap(sampleList, hotCachePool);

        List<DynamicHotCacheObj> list = new ArrayList<>(distinctCacheMap.values());

        list.sort(Comparator.comparingLong(DynamicHotCacheObj::getSort));

        diffCnt = 0;
        // 3. 插入完成之后，只保存配置中的最大热点缓存数量
        for (int i = list.size() - 1; i >= Math.abs(list.size() - properties.getHotCacheNums()); --i) {
            if (!distinctHotCache.contains(list.get(i).getData())) {
                // 统计缓存热点池修改的个数
                ++diffCnt;
            }
            newHotCachePool.add(getTuple(list.get(i)));
        }
    }

    private void poolCapacityNotFull(List<DynamicHotCacheObj> sampleList, Set<ZSetOperations.TypedTuple<String>> hotCachePool, Set<ZSetOperations.TypedTuple<String>> newHotCachePool) {
        log.info("热点缓存池未满，直接添加");
        // 1. 获取id去重列表
        Map<Object, DynamicHotCacheObj> distinctMap = getDistinctCacheMap(sampleList, hotCachePool);
        diffCnt = 0;
        // 2. 添加到缓存池中
        distinctMap.values().forEach((e) -> {
            if (!distinctHotCache.contains(e.getData())) {
                // 统计缓存热点池修改的个数
                ++diffCnt;
            }
            newHotCachePool.add(getTuple(e));
        });
    }

    private Map<Object, DynamicHotCacheObj> getDistinctCacheMap(List<DynamicHotCacheObj> sampleList, Set<ZSetOperations.TypedTuple<String>> hotCachePool) {
        Map<Object, DynamicHotCacheObj> distinctMap = new HashMap<>(sampleList.size() + hotCachePool.size());

        distinctHotCache = new HashSet<>(hotCachePool.size());
        // 1. 旧热点缓存池中的数据，更新后直接添加到新热点缓存池中
        hotCachePool.forEach((e) -> {
            DynamicHotCacheObj oldHotCacheObj = JSONObject.parseObject(e.getValue(), DynamicHotCacheObj.class);
            if (oldHotCacheObj == null) {
                return;
            }
            // TODO: 1.1更新热点值
            DynamicHotCacheObj newHotCacheObj = updateHotCacheObj(oldHotCacheObj);
            // 1.2 放入去重map
            distinctMap.put(newHotCacheObj.getData(), newHotCacheObj);
            // 1.3 保存前热点对象信息
            distinctHotCache.add(oldHotCacheObj.getData());
        });
        // 2.对样本值进行筛选去重，看看是否需要更新。
        for (DynamicHotCacheObj sample : sampleList) {
            if (distinctMap.containsKey(sample.getData())) {
                DynamicHotCacheObj old = distinctMap.get(sample.getData());
                if (sample.getSort() > old.getSort()) {
                    distinctMap.put(sample.getData(), sample);
                }
            } else {
                distinctMap.put(sample.getData(), sample);
            }
        }
        return distinctMap;
    }

    private ZSetOperations.TypedTuple<String> getTuple(DynamicHotCacheObj obj) {
        return new DefaultTypedTuple<>(JSONObject.toJSONString(obj), (double) obj.getSort());
    }


    /**
     * 更新热点缓存对象的热点值
     *
     * @param oldHotCache：热点缓存对象
     */
    protected abstract DynamicHotCacheObj updateHotCacheObj(DynamicHotCacheObj oldHotCache);
}
