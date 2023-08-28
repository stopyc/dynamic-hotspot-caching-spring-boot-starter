package shop.stopyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author YC104
 */
@SpringBootApplication
@EnableScheduling
public class DynamicHotCacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicHotCacheApplication.class, args);
    }
}