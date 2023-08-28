package shop.stopyc.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.stopyc.config.DynamicHotCacheProperties;

import javax.annotation.Resource;

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

    public void updateCache() {
        fastUpdate();
        slowUpdate();
    }

    private void slowUpdate() {
        long now = System.currentTimeMillis();
        if (now - lastSlowUpdateTime < properties.getSlowSamplesTime()) {
            log.info("距离上次慢速更新时间不足 {} ms，不进行更新", properties.getSlowSamplesTime());
            return;
        }
        long after;
        int diffCnt;
        int cnt = 1;
        do {
            log.info("开始第 {} 次慢速更新热点缓存池", cnt);
            diffCnt = dynamicHotCache.tryUpdateHotCachePool(properties.getSlowSamplesNums());
            after = System.currentTimeMillis();
            log.info("第 {} 次慢速更新热点缓存池结束，累积总耗时 {} ms，更新 {} 个缓存对象", cnt++, after - now, diffCnt);
        } while (after - now <= properties.getSlowSamplesTime() && diffCnt >= properties.getSlowSamplesNums() * 0.25);
        lastSlowUpdateTime = after;
    }

    private void fastUpdate() {
        long now = System.currentTimeMillis();
        if (now - lastFastUpdateTime < properties.getFastSamplesTime()) {
            log.info("距离上次快速更新时间不足 {} ms，不进行更新", properties.getFastSamplesTime());
            return;
        }
        long after;
        int diffCnt;
        int cnt = 1;
        do {
            log.info("开始第 {} 次快速更新热点缓存池", cnt);
            diffCnt = dynamicHotCache.tryUpdateHotCachePool(properties.getFastSamplesNums());
            after = System.currentTimeMillis();
            log.info("第 {} 次快速更新热点缓存池结束，累积总耗时 {} ms，更新 {} 个缓存对象", cnt++, after - now, diffCnt);
        } while (after - now <= properties.getFastSamplesTime() && diffCnt >= properties.getFastSamplesNums() * 0.25);
        lastFastUpdateTime = after;
    }
}
