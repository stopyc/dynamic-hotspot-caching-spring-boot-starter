package shop.stopyc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.stopyc.core.common.DynamicHotCache;
import shop.stopyc.core.common.DynamicHotCacheFactory;

import javax.annotation.Resource;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 动态热点缓存配置类
 * @author: stop.yc
 * @create: 2023-08-27 20:44
 **/
@Configuration
public class DynamicHotCacheConfig {

    @Resource
    private DynamicHotCacheFactory dynamicHotCacheFactory;

    @Bean
    public DynamicHotCache dynamicHotCache() {
        return dynamicHotCacheFactory.getDynamicHotCache();
    }
}
