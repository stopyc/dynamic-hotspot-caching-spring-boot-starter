package shop.stopyc.core.common;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import shop.stopyc.config.DynamicHotCacheProperties;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 动态热点缓存排序工厂
 * @author: stop.yc
 * @create: 2023-08-27 16:45
 **/

@Setter
@Component
@NoArgsConstructor
@DependsOn({"mylfu", "lru"})
public class DynamicHotCacheFactory {

    /**
     * 策略map
     */
    private static final Map<String, AbstractSortAlgorithm> SORT_STRATEGY_MAP = new ConcurrentHashMap<>(5);
    private DynamicHotCacheProperties properties;

    @Autowired
    public DynamicHotCacheFactory(DynamicHotCacheProperties properties) {
        this.properties = properties;
    }

    static void putStrategy(String strategy, AbstractSortAlgorithm sortAlgorithm) {
        SORT_STRATEGY_MAP.put(strategy, sortAlgorithm);
    }

    protected static AbstractSortAlgorithm getStrategy(String strategyType) {
        return Optional.ofNullable(SORT_STRATEGY_MAP.get(strategyType))
                .orElseThrow(() -> new RuntimeException("没有找到对应的策略"));
    }

    public DynamicHotCache getDynamicHotCache() {
        return new DynamicHotCache(getStrategy(properties.getStrategyType()));
    }
}
