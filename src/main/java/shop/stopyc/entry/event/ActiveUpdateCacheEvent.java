package shop.stopyc.entry.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author YC104
 */
@Getter
public class ActiveUpdateCacheEvent extends ApplicationEvent {
    public ActiveUpdateCacheEvent(Object source) {
        super(source);
    }
}
