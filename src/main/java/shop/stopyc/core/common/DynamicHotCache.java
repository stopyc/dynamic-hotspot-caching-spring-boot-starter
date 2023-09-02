package shop.stopyc.core.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 动态热点缓存排序算法
 * @author: stop.yc
 * @create: 2023-08-27 20:43
 **/
//@Component
@AllArgsConstructor
@NoArgsConstructor
public class DynamicHotCache {

    private AbstractSortAlgorithm abstractSortAlgorithm;

    /**
     * 缓存对象访问入口统计
     *
     * @param hashKey：对象id
     * @param data：        对象标识，用来数据库查找
     */
    @Async
    public void access(String hashKey, Object data) {
        if (!StringUtils.hasText(hashKey)) {
            return;
        }
        if (data == null) {
            return;
        }
        data = data.toString();
        abstractSortAlgorithm.objAccess(hashKey, data);
    }

    protected Set<Object> tryUpdateHotCachePool(long sampleNums) {
        return abstractSortAlgorithm.tryUpdateHotCachePool(sampleNums);
    }
}
