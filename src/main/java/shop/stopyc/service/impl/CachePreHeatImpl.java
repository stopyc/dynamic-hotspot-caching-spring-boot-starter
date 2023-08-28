package shop.stopyc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import shop.stopyc.service.CachePreHeat;

import java.util.Set;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: 缓存预热实现类
 * @author: stop.yc
 * @create: 2023-08-28 23:41
 **/
@Service
@Slf4j
public class CachePreHeatImpl implements CachePreHeat {

    @Override
    public void preHeat(Set<Object> needToPreHeatSet) {
        if (CollectionUtils.isEmpty(needToPreHeatSet)) {
            return;
        }
        log.info("开始预热缓存");
        log.info("需要预热的缓存的data列表 为: {}", needToPreHeatSet);
    }
}
