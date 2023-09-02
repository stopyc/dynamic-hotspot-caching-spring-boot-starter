package shop.stopyc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author YC104
 */
@Configuration
@ConditionalOnProperty(prefix = "task.pool", name = "enable", havingValue = "true")
@EnableConfigurationProperties(ExecutorProperties.class)
public class ExecutorStarterAutoConfiguration {

    @Resource
    private ExecutorProperties properties;


    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(properties.getCoreSize());
        //配置最大线程数
        executor.setMaxPoolSize(properties.getMaxSize());
        //配置队列大小
        executor.setQueueCapacity(properties.getQueueCapacity());

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}