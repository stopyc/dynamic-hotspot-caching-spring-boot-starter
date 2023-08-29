package shop.stopyc.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import shop.stopyc.entry.DynamicHotCacheObj;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: redis操作工具类
 * @author: stop.yc
 * @create: 2023-08-27 17:23
 **/
@Component
@SuppressWarnings("all")
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String... value) {
        if (Objects.isNull(value) || value.length == 0) {
            return;
        }
        stringRedisTemplate.opsForSet().add(key, value);
    }

    public Map<String, DynamicHotCacheObj> mGetRandom(String key, long count) {
        long size = stringRedisTemplate.opsForHash().size(key);
        if (size == 0) {
            return Collections.emptyMap();
        }
        Map<Object, Object> map;
        if (size < 2) {
            map = stringRedisTemplate.opsForHash().entries(key);
        } else {
            map = stringRedisTemplate.opsForHash().randomEntries(key, count);
        }

        if (Objects.isNull(map)) {
            return Collections.emptyMap();
        }
        Map<String, DynamicHotCacheObj> res = new HashMap<>(map.size());
        Set<Map.Entry<Object, Object>> entries = map.entrySet();
        entries.forEach((entry) -> res.put(entry.getKey().toString(), jsonStrToBean(entry.getValue().toString(), DynamicHotCacheObj.class)));
        return res;
    }

    public DynamicHotCacheObj mGet(String key, String hashKey) {
        Object o = stringRedisTemplate.opsForHash().get(key, hashKey);
        if (Objects.isNull(o)) {
            return null;
        }
        return jsonStrToBean(o.toString(), DynamicHotCacheObj.class);
    }

    public void mSet(String key, String hashKey, DynamicHotCacheObj data) {
        Map<String, String> res = new HashMap<>(1);
        res.put(hashKey, beanToJsonStr(data));
        stringRedisTemplate.opsForHash().putAll(key, res);
    }

    public void mSet(String key, Map<String, DynamicHotCacheObj> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        Map<String, String> res = new HashMap<>(data.size());
        data.forEach((key1, value) -> res.put(key1, beanToJsonStr(value)));
        stringRedisTemplate.opsForHash().putAll(key, res);
    }

    public Set<ZSetOperations.TypedTuple<String>> zGet(String key) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, 0, Long.MAX_VALUE);
    }

    public void zSet(String key, DynamicHotCacheObj data, double score) {
        stringRedisTemplate.opsForZSet().add(key, beanToJsonStr(data), score);
    }

    public void zSet(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        if (CollectionUtils.isEmpty(tuples)) {
            return;
        }
        stringRedisTemplate.opsForZSet().add(key, tuples);
    }

    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    private <T> T jsonStrToBean(String jsonString, Class<T> clazz) {
        return JSONObject.parseObject(jsonString, clazz);
    }


    private <T> String beanToJsonStr(T bean) {
        return JSONObject.toJSONString(bean);
    }
}
