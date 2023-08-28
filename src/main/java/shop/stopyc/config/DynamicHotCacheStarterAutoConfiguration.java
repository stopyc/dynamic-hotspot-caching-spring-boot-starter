package shop.stopyc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author YC104
 */
@Configuration
@ConditionalOnProperty(prefix = "dynamic.hot.cache", name = "enable", havingValue = "true")
@EnableConfigurationProperties(DynamicHotCacheProperties.class)
public class DynamicHotCacheStarterAutoConfiguration {
}