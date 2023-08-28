package shop.stopyc.entry.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import shop.stopyc.entry.EventContent;

/**
 * @author YC104
 */
@Getter
public class ObjAccessEvent extends ApplicationEvent {

    private final EventContent eventContent;

    public ObjAccessEvent(Object source, EventContent eventContent) {
        super(source);
        this.eventContent = eventContent;
    }
}
