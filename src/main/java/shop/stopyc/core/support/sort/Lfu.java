package shop.stopyc.core.support.sort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.stopyc.core.common.AbstractSortAlgorithm;
import shop.stopyc.core.type.SortAlgorithmType;
import shop.stopyc.entry.DynamicHotCacheObj;

import java.util.Objects;
import java.util.Random;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: lfu算法
 * @author: stop.yc
 * @create: 2023-08-27 16:51
 **/
@Component("mylfu")
@Slf4j
public class Lfu extends AbstractSortAlgorithm {
    private static final Random r = new Random();


    private final long lfuLogFactor = 10;
    private final long lfuDecayTime = 60000L;

    private long getNewLogicAccessCount(long oldLogicAccessCount) {
        double R = r.nextDouble();
        double P = 1 / ((double) oldLogicAccessCount * lfuLogFactor + 1);
        return R < P ? oldLogicAccessCount + 1 : oldLogicAccessCount;
    }

    private long timeDecayLogicAccessCount(long oldLogicAccessCount, long lastAccessTime) {
        long now = System.currentTimeMillis();
        return now - lastAccessTime > lfuDecayTime ? oldLogicAccessCount - 1 : oldLogicAccessCount;
    }

    private long getNewLogicAccessCount(long oldLogicAccessCount, long lastAccessTime) {
        oldLogicAccessCount = timeDecayLogicAccessCount(oldLogicAccessCount, lastAccessTime);
        return getNewLogicAccessCount(oldLogicAccessCount);
    }


    @Override
    public void access(String hashKey, Object data) {
        DynamicHotCacheObj dynamicHotCacheObj = redisUtil.mGet(properties.getAllKeyPoolPrefix(), hashKey);
        if (Objects.isNull(dynamicHotCacheObj)) {
            dynamicHotCacheObj = DynamicHotCacheObj.builder()
                    .lastAccessTime(System.currentTimeMillis())
                    .accessCount(1)
                    .data(data)
                    .sort(1)
                    .build();
        } else {
            long lastAccessTime = dynamicHotCacheObj.getLastAccessTime();
            long accessCount = dynamicHotCacheObj.getAccessCount();
            long newLogicAccessCount = getNewLogicAccessCount(accessCount, lastAccessTime);
            dynamicHotCacheObj.setLastAccessTime(System.currentTimeMillis());
            dynamicHotCacheObj.setAccessCount(newLogicAccessCount);
            dynamicHotCacheObj.setSort(newLogicAccessCount);
        }
        log.info("执行lru算法，更新对象最后状态为：{}", dynamicHotCacheObj);
        redisUtil.mSet(properties.getAllKeyPoolPrefix(), hashKey, dynamicHotCacheObj);
    }

    @Override
    protected String strategyName() {
        return SortAlgorithmType.LFU.getType();
    }

    @Override
    protected DynamicHotCacheObj updateHotCacheObj(DynamicHotCacheObj oldHotCache) {
        long newLogicAccessCount = timeDecayLogicAccessCount(oldHotCache.getAccessCount(), oldHotCache.getLastAccessTime());
        oldHotCache.setAccessCount(newLogicAccessCount);
        oldHotCache.setSort(newLogicAccessCount);
        return oldHotCache;
    }
}
