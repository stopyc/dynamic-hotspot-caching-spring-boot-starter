package shop.stopyc.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.stopyc.entry.event.ObjAccessEvent;

import javax.annotation.Resource;

/**
 * @author: stop.yc
 **/
@Component
@Slf4j
public class ObjAccessListener {

    @Resource
    private UpdateCache updateCache;

    @Async
    @EventListener(classes = ObjAccessEvent.class)
    public void updateCacheStatus(ObjAccessEvent objAccessEvent) {
        updateCache.updateCache();
    }
}
