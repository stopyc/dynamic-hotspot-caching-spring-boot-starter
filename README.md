# dynamic-hotspot-caching-spring-boot-starter

## 项目介绍

基于Redis内存策略的动态热点缓存框架，通过策略工厂模式整合LRU、LFU、Random等算法，通过配置文件，实现动态更新key的热力值，并将热点Key池对象写入缓存。
(![Dynamic-Hot-Cache](https://github.com/stopyc/picb/blob/7b64a173da77febd5729567457f0ec7fb984ed1c/Dynamic-Hot-Cache.png?raw=true))
<img src="https://github.com/stopyc/picb/blob/main/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F.png?raw=true" alt="设计模式" style="zoom:50%;" />

## 快速开始

1. 引入依赖

```xml 

<dependency>
    <groupId>shop.stopyc</groupId>
    <artifactId>dynamic-hotspot-caching-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

2. 配置文件

```yaml
# 配置动态缓存池（以下实例值均为默认配置）
dynamic:
  hot:
    cache:
      # 热点缓存池key前缀
      cachePoolPrefix: cachepool
      # 所有对象池key前缀
      allKeyPoolPrefix: allkeypool
      # 快速检查模式-抽样5个
      fastSamplesNums: 5
      # 快速检查模式-抽样1000ms
      fastSamplesTime: 1000
      # 慢速检查模式-抽样10个
      slowSamplesNums: 10
      # 慢速检查模式-抽样3000ms
      slowSamplesTime: 3000
      # 热点缓存池大小
      hotCacheNums: 10
      # 热力值计算策略
      strategy-type: lru
      # 定时检查间隔
      regularlyUpdateCacheInterval: 500
      # 是否启用
      enable: true

task:
  pool:
    # 核心线程数
    coreSize: 8
    # 最大线程数
    maxSize: 16
    # 队列容量
    queueCapacity: 30
```

3. 使用

首先添加注解@EnableScheduling

```java

@SpringBootApplication
@EnableScheduling
public class TestingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestingApplication.class, args);
    }
}
```

然后在外部对象被访问时，调用`dynamicHotCache.access(String hashKey, Object data)`方法，标记该对象被访问。

```java
@Resource
private DynamicHotCache dynamicHotCache;

        void test(Obj needToCache){
        Long objId=needToCache.getObjId();
        // hashKey为对象在Redis中缓存的hashKey前缀，推荐使用对象的id
        // data为对象的id，最后对外提供的接口返回值就是data的集合，
        // 表示是改动的热点缓存池，用户需要以这些data去数据库查询并写入缓存。
        dynamicHotCache.access(objId.toString(),objId);
        }
```