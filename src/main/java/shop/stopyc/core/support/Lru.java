package shop.stopyc.core.support;

import org.springframework.stereotype.Component;
import shop.stopyc.core.common.AbstractSortAlgorithm;
import shop.stopyc.core.type.SortAlgorithmType;
import shop.stopyc.entry.DynamicHotCacheObj;

import java.util.Objects;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: Lru算法
 * @author: stop.yc
 * @create: 2023-08-27 16:51
 **/
@Component
public class Lru extends AbstractSortAlgorithm {

    @Override
    public void access(String hashKey, Object data) {
        DynamicHotCacheObj dynamicHotCacheObj = redisUtil.mGet(properties.getAllKeyPoolPrefix(), hashKey);
        if (Objects.isNull(dynamicHotCacheObj)) {
            dynamicHotCacheObj = DynamicHotCacheObj.builder()
                    .lastAccessTime(System.currentTimeMillis())
                    .data(data)
                    .sort(System.currentTimeMillis())
                    .build();
        } else {
            dynamicHotCacheObj.setLastAccessTime(System.currentTimeMillis());
            dynamicHotCacheObj.setSort(System.currentTimeMillis());
        }
        redisUtil.mSet(properties.getAllKeyPoolPrefix(), hashKey, dynamicHotCacheObj);
    }

    @Override
    public String strategyName() {
        return SortAlgorithmType.LRU.getType();
    }

    @Override
    protected DynamicHotCacheObj updateHotCacheObj(DynamicHotCacheObj oldHotCache) {
        //lru不需要更新时间
        return oldHotCache;
    }
}
