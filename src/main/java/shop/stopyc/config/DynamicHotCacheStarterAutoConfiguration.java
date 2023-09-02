package shop.stopyc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import shop.stopyc.core.common.DynamicHotCache;
import shop.stopyc.core.common.DynamicHotCacheFactory;

import javax.annotation.Resource;

/**
 * @author YC104
 */
@Configuration
//@ConditionalOnProperty(prefix = "dynamic.hot.cache", name = "enable", havingValue = "true")
@DependsOn({"mylfu", "lru"})
@EnableConfigurationProperties(DynamicHotCacheProperties.class)
@ConditionalOnClass(DynamicHotCache.class)
public class DynamicHotCacheStarterAutoConfiguration {

    @Autowired
    private DynamicHotCacheProperties dynamicHotCacheProperties;

    @Resource
    private DynamicHotCacheFactory dynamicHotCacheFactory;

    @Bean
    @ConditionalOnMissingBean
    public DynamicHotCache dynamicHotCache() {
        dynamicHotCacheFactory.setProperties(dynamicHotCacheProperties);
        return dynamicHotCacheFactory.getDynamicHotCache();
    }
}