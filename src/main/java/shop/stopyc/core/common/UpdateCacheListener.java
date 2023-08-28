package shop.stopyc.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.stopyc.entry.EventContent;
import shop.stopyc.entry.event.ActiveUpdateCacheEvent;
import shop.stopyc.entry.event.ObjAccessEvent;
import shop.stopyc.service.CachePreHeat;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author: stop.yc
 **/
@Component
@Slf4j
public class UpdateCacheListener {

    @Resource
    private UpdateCache updateCache;
    @Resource
    private CachePreHeat cachePreHeat;

    @EventListener(classes = ObjAccessEvent.class)
    public void objAccess(ObjAccessEvent objAccessEvent) {
        EventContent eventContent = objAccessEvent.getEventContent();
        Object data = eventContent.getData();
        String key = eventContent.getKey();
        log.info("监听用户访问key为：{}， data为：{} 的对象，惰性更新缓存状态", key, data);
        tryUpdateCache();
    }

    @EventListener(classes = ActiveUpdateCacheEvent.class)
    public void activeUpdateCache(ActiveUpdateCacheEvent activeUpdateCacheEvent) {
        tryUpdateCache();
    }

    @Async
    public void tryUpdateCache() {
        Set<Object> needToPreHeat = updateCache.updateCache();
        cachePreHeat.preHeat(needToPreHeat);
    }
}
