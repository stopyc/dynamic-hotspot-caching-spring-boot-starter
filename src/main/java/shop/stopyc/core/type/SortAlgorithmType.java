package shop.stopyc.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YC104
 */

@Getter
@AllArgsConstructor
public enum SortAlgorithmType {

    LRU("lru"),

    LFU("lfu");
    private final String type;
}
