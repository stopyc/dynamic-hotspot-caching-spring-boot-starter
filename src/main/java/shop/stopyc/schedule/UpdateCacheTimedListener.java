package shop.stopyc.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import shop.stopyc.config.DynamicHotCacheProperties;
import shop.stopyc.core.common.UpdateCache;

import javax.annotation.Resource;

/**
 * @description: 定时监听缓存变化情况
 * @author: stop.yc
 **/
@Component
@Slf4j
public class UpdateCacheTimedListener implements SchedulingConfigurer {
    @Resource
    private DynamicHotCacheProperties properties;

    @Resource
    private UpdateCache updateCache;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
            updateCache.updateCache();
        }, triggerContext -> {
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(properties.getRegularlyUpdateCacheInterval());
            return periodicTrigger.nextExecutionTime(triggerContext);
        });
    }
}
