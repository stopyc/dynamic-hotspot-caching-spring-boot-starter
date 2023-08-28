package shop.stopyc.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 动态热点缓存obj
 * @author: stop.yc
 * @create: 2023-08-27 20:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class DynamicHotCacheObj {
    /**
     * 上次访问时间
     */
    private long lastAccessTime;

    /**
     * 访问次数
     */
    private long accessCount;

    /**
     * 数据，一般是对象的id
     */
    private Object data;

    private long sort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicHotCacheObj obj = (DynamicHotCacheObj) o;
        return lastAccessTime == obj.lastAccessTime && accessCount == obj.accessCount && sort == obj.sort && Objects.equals(data, obj.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAccessTime, accessCount, data, sort);
    }
}
