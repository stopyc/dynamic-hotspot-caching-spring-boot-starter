package shop.stopyc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import shop.stopyc.core.common.DynamicHotCache;
import shop.stopyc.core.common.UpdateCache;
import shop.stopyc.entry.DynamicHotCacheObj;
import shop.stopyc.util.RedisUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: dynamic-hotspot-caching-spring-boot-starter
 * @description: test
 * @author: stop.yc
 * @create: 2023-08-27 17:00
 **/
//@SpringBootTest(classes = DynamicHotCacheApplication.class)
@Slf4j
public class TestCache {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private DynamicHotCache dynamicHotCache;

    @org.junit.jupiter.api.Test
    void test1() {
        //List<Object> sampling = dynamicHotCache.sampling("111")
        //System.out.println("sampling = " + sampling);
    }

    @Test
    void test2() {
        dynamicHotCache.access("111", "111");
    }

    @Test
    void test3() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int id = r.nextInt(100);
            dynamicHotCache.access(String.valueOf(id), id);
        }
    }

    @Test
    void test4() {
        String[] strings = new String[1000];
        for (int i = 0; i < 1000; i++) {
            strings[i] = String.valueOf(i + 1);
        }
        redisUtil.set("cachepool", strings);
    }


    @Test
    void test6() {
        Map<String, DynamicHotCacheObj> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            DynamicHotCacheObj dynamicHotCacheObj = new DynamicHotCacheObj();
            dynamicHotCacheObj.setSort(i + 1);
            map.put(String.valueOf(i + 1), dynamicHotCacheObj);
        }
        redisUtil.mSet("allkeypool", map);
    }

    @Test
    void test5() {
        Map<String, DynamicHotCacheObj> map = redisUtil.mGetRandom("allkeypool", 5);
        map.forEach((key, value) -> System.out.println("key = " + key + ", value = " + value));
    }

    @Test
    void test7() {
        Map<String, DynamicHotCacheObj> map = redisUtil.mGetRandom("allkeypool", 5);
        //TreeMap<DynamicHotCacheObj, String> sortedHotObj = dynamicHotCache.getSortedHotObj(map);
        //sortedHotObj.forEach((key, value) -> System.out.println("key = " + key + ", value = " + value));
    }

    @Test
    void test8() {
        Map<String, DynamicHotCacheObj> map = redisUtil.mGetRandom("allkeypool", 5);
        //TreeMap<DynamicHotCacheObj, String> sortedHotObj = dynamicHotCache.getSortedHotObj(map);
        //sortedHotObj.forEach((key, value) -> System.out.println("key = " + key + ", value = " + value));
        //Map.Entry<DynamicHotCacheObj, String> max = sortedHotObj.firstEntry();
        //System.out.println("max.getKey() = " + max.getKey());
    }

    @Test
    void test9() {
        //dynamicHotCache.get();
    }

    @Test
    void test10() {
        DynamicHotCacheObj dynamicHotCacheObj = new DynamicHotCacheObj();
        for (int i = 0; i < 10; i++) {
            dynamicHotCacheObj.setAccessCount(i + 1);
            dynamicHotCacheObj.setLastAccessTime(System.currentTimeMillis());
            redisUtil.zSet("cachepool", dynamicHotCacheObj, System.currentTimeMillis());
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {

            }
        }
    }

    @Resource
    private UpdateCache updateCache;

    @Test
    void test12() throws InterruptedException {
        Map<String, DynamicHotCacheObj> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            DynamicHotCacheObj dynamicHotCacheObj = new DynamicHotCacheObj();
            dynamicHotCacheObj.setSort(System.currentTimeMillis());
            map.put(String.valueOf(i + 1), dynamicHotCacheObj);
            Thread.sleep(100L);
        }
        redisUtil.mSet("allkeypool", map);
    }


    @Test
    void test13() throws InterruptedException {
        Map<String, DynamicHotCacheObj> map = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            DynamicHotCacheObj dynamicHotCacheObj = new DynamicHotCacheObj();
            dynamicHotCacheObj.setSort(i + 1);
            dynamicHotCacheObj.setData(i);
            map.put(String.valueOf(i + 1), dynamicHotCacheObj);
        }
        redisUtil.mSet("allkeypool", map);
    }

    @Test
    void test14() throws InterruptedException {
        DynamicHotCacheObj obj = new DynamicHotCacheObj();
        obj.setData(25);
        obj.setSort(999);
        redisUtil.zSet("cachepool", obj, 1);
    }

    @Test
    void test15() throws InterruptedException {
        DynamicHotCacheObj obj = new DynamicHotCacheObj();
        obj.setData(25);
        obj.setSort(3);
        redisUtil.mSet("allkeypool", "25", obj);
    }


    @Test
    void test16() throws InterruptedException {
        Map<String, DynamicHotCacheObj> map = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            DynamicHotCacheObj dynamicHotCacheObj = new DynamicHotCacheObj();
            dynamicHotCacheObj.setSort(i + 2);
            dynamicHotCacheObj.setData(i);
            map.put(String.valueOf(i + 1), dynamicHotCacheObj);
        }
        redisUtil.mSet("allkeypool", map);
    }

    @Test
    void test11() {
        //dynamicHotCache.tryUpdateHotCachePool(5);
    }

    @Test
    void test18() {
        dynamicHotCache.access("20", 21);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {

        }
    }

    @Test
    void test17() {
        for (int i = 0; i < 10; i++) {
            //dynamicHotCache.tryUpdateHotCachePool(5);
        }
    }

    @Test
    void test19() throws InterruptedException {
        Random r = new Random();
        for (int i = 0; i < 5000; i++) {
            if (i % 200 == 0) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {
                }
            }
            int i1 = r.nextInt(20);
            dynamicHotCache.access(String.valueOf(i1 + 1), i1 + 1);
        }

        Thread.sleep(10000L);
    }

    @Test
    void test20() {
        updateCache.updateCache();
    }

    @Test
    void test21() {
        for (int i = 0; i < 5; i++) {
            updateCache.updateCache();
        }
    }


    @Test
    void test22() {
        Set<Object> set1 = new HashSet<>();
        log.info("set1 为: {}", set1);
        Set<Object> set2 = new HashSet<>();
        set2.add(11);
        log.info("set2 为: {}", set2);
        set1.addAll(set2);
    }


    @Test
    void test23() {
        Set<ZSetOperations.TypedTuple<String>> members = new HashSet<>();
        members.add(new DefaultTypedTuple<>("member1", 1.0));
        members.add(new DefaultTypedTuple<>("member2", 2.0));
        redisUtil.delAndSet("cachepool", members);
    }


    @Test
    void test24() throws InterruptedException {
        Random r = new Random();
        for (int i = 0; i < 5000; i++) {
            int i1 = r.nextInt(20);
            dynamicHotCache.access(String.valueOf(i1 + 1), i1 + 1);
            Thread.sleep(500L);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException ignored) {
            }
        }
        Thread.sleep(10000L);
    }

    @Test
    void test25() throws InterruptedException {
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            if (i % 200 == 0) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {
                }
            }
            int i1 = r.nextInt(20);
            dynamicHotCache.access(String.valueOf(i1 + 1), i1 + 1);
            dynamicHotCache.access(String.valueOf(80), 80);
        }

        Thread.sleep(10000L);
    }


    @Test
    void test26() throws InterruptedException {
        dynamicHotCache.access("8888", "8888");
        Thread.sleep(5000L);
    }
}


