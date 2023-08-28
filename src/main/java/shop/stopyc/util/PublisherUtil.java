package shop.stopyc.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.stopyc.entry.EventContent;
import shop.stopyc.entry.event.ObjAccessEvent;

import javax.annotation.Resource;

/**
 * @description: 事件发布工具类
 * @author: stop.yc
 * @create: 2023-08-28 15:41
 **/
@Component
@Slf4j
public class PublisherUtil {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    public void objAccess(Object source, EventContent eventContent) {
        eventPublisher.publishEvent(new ObjAccessEvent(source, eventContent));
    }
}
