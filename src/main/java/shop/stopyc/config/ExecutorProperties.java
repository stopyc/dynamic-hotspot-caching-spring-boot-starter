package shop.stopyc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 线程池配置类
 * @author: stop.yc
 * @create: 2023-09-02 22:32
 **/
@ConfigurationProperties(prefix = "task.pool")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutorProperties {
    private Integer coreSize = 8;

    private Integer maxSize = 16;

    private Integer queueCapacity = 30;
}
