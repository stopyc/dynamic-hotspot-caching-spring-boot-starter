package shop.stopyc.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import shop.stopyc.config.DynamicHotCacheProperties;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 更新缓存实际类
 * @author: stop.yc
 * @create: 2023-08-28 21:27
 **/
@Component
@Slf4j
public class UpdateCache {
    private static long lastSlowUpdateTime = 0;
    private static long lastFastUpdateTime = 0;
    @Resource
    private DynamicHotCache dynamicHotCache;
    @Resource
    private DynamicHotCacheProperties properties;

    public Set<Object> updateCache() {
        if (!properties.isEnable()) {
            return Collections.emptySet();
        }
        Set<Object> needToPreheat = fastUpdate();
        if (CollectionUtils.isEmpty(needToPreheat)) {
            needToPreheat = new ConcurrentSkipListSet<>();
        }
        Set<Object> needToPreheat2 = slowUpdate();
        if (!CollectionUtils.isEmpty(needToPreheat2)) {
            needToPreheat.addAll(needToPreheat2);
        }
        return needToPreheat;
    }

    private Set<Object> slowUpdate() {
        log.info("尝试执行慢速更新热点缓存池");
        long now = System.currentTimeMillis();
        if (now - lastSlowUpdateTime < properties.getSlowSamplesTime()) {
            log.info("距离上次慢速更新时间不足 {} ms，不进行更新", properties.getSlowSamplesTime());
            return Collections.emptySet();
        }
        long after;
        Set<Object> needToPreheat;
        int cnt = 1;
        do {
            log.info("开始第 {} 次慢速更新热点缓存池", cnt);
            needToPreheat = dynamicHotCache.tryUpdateHotCachePool(properties.getSlowSamplesNums());
            after = System.currentTimeMillis();
            log.info("第 {} 次慢速更新热点缓存池结束，累积总耗时 {} ms，更新 {} 个缓存对象", cnt++, after - now, needToPreheat.size());
        } while (after - now <= properties.getSlowSamplesTime() && needToPreheat.size() >= properties.getSlowSamplesNums() * 0.25);
        lastSlowUpdateTime = after;
        return needToPreheat;
    }

    private Set<Object> fastUpdate() {
        log.info("尝试执行快速更新热点缓存池");
        long now = System.currentTimeMillis();
        if (now - lastFastUpdateTime < properties.getFastSamplesTime()) {
            log.info("距离上次快速更新时间不足 {} ms，不进行更新", properties.getFastSamplesTime());
            return Collections.emptySet();
        }
        long after;
        Set<Object> needToPreheat;
        int cnt = 1;
        do {
            log.info("开始第 {} 次快速更新热点缓存池", cnt);
            needToPreheat = dynamicHotCache.tryUpdateHotCachePool(properties.getFastSamplesNums());
            after = System.currentTimeMillis();
            log.info("第 {} 次快速更新热点缓存池结束，累积总耗时 {} ms，更新 {} 个缓存对象", cnt++, after - now, needToPreheat.size());
        } while (after - now <= properties.getFastSamplesTime() && needToPreheat.size() >= properties.getFastSamplesNums() * 0.25);
        lastFastUpdateTime = after;
        return needToPreheat;
    }
}
