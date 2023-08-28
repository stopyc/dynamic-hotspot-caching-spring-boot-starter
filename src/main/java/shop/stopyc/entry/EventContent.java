package shop.stopyc.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YC104
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventContent {
    private String key;
    private Object data;
}