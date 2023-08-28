package shop.stopyc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import shop.stopyc.core.type.SortAlgorithmType;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 动态热点缓存对外配置类
 * @author: stop.yc
 * @create: 2023-08-27 21:25
 **/
@ConfigurationProperties(prefix = "dynamic.hot.cache")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicHotCacheProperties {
    /**
     * 经过排序算法得出来的热点缓存的对象
     */
    private String cachePoolPrefix = "cachepool";

    /**
     * 所有需要进行排序算法统计的key池，每次会随机抽样，探测热点对象
     */
    private String allKeyPoolPrefix = "allkeypool";

    /**
     * Slow抽样个数
     */
    private Integer slowSamplesNums = 20;

    /**
     * Fast抽样个数
     */
    private Integer fastSamplesNums = 5;

    /**
     * Fast抽样最大耗时
     */
    private Long fastSamplesTime = 1000L;

    /**
     * Slow抽样最大耗时
     */
    private Long slowSamplesTime = 3000L;

    /**
     * 热点对象的个数
     */
    private Integer hotCacheNums = 20;

    /**
     * 策略类型
     */
    private String strategyType = SortAlgorithmType.LRU.getType();

    private boolean enable;
}
