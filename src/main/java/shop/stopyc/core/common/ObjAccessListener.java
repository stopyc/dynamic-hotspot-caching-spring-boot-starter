package shop.stopyc.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.stopyc.entry.EventContent;
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
        EventContent eventContent = objAccessEvent.getEventContent();
        Object data = eventContent.getData();
        String key = eventContent.getKey();
        log.info("监听用户访问key为：{}， data为：{} 的对象，到惰性更新缓存状态", key, data);
        updateCache.updateCache();
    }
}
