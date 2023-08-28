package shop.stopyc.core.support.sort;

import org.springframework.stereotype.Component;
import shop.stopyc.core.common.AbstractSortAlgorithm;
import shop.stopyc.core.type.SortAlgorithmType;
import shop.stopyc.entry.DynamicHotCacheObj;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: lfu算法
 * @author: stop.yc
 * @create: 2023-08-27 16:51
 **/
@Component("mylfu")
public class Lfu extends AbstractSortAlgorithm {


    @Override
    public void access(String key, Object data) {
        System.out.println(111);
    }

    @Override
    protected String strategyName() {
        return SortAlgorithmType.LFU.getType();
    }

    @Override
    protected DynamicHotCacheObj updateHotCacheObj(DynamicHotCacheObj oldHotCache) {
        return oldHotCache;
    }
}
