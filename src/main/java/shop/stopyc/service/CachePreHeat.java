package shop.stopyc.service;


import java.util.Set;

/**
 * @author YC104
 */
public interface CachePreHeat {

    void preHeat(Set<Object> needToPreHeatSet);
}
