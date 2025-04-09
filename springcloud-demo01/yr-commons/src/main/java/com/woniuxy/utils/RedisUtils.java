package com.woniuxy.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 马宇航
 * @Todo: redis工具类
 * @DateTime: 25/04/09/星期三 15:35
 * @Component: 成都蜗牛学苑
 **/
@Component
public class RedisUtils {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    /**
     * 设置键值对
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置带过期时间的键值对
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 根据键获取值
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 为键设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取键的剩余过期时间
     * @param key 键
     * @param unit 时间单位
     * @return 剩余过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return stringRedisTemplate.getExpire(key, unit);
    }

    /**
     * 设置 Hash 类型的字段值
     * @param key 键
     * @param hashKey Hash 字段
     * @param value 值
     */
    public void hset(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置 Hash 类型的字段值
     * @param key 键
     * @param map 包含多个 Hash 字段和值的 Map
     */
    public void hsetAll(String key, Map<String, Object> map) {
        // 创建新的Map并进行类型转换
        Map<Object, Object> objectMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            objectMap.put(entry.getKey(), entry.getValue());
        }
        stringRedisTemplate.opsForHash().putAll(key, objectMap);
    }

    /**
     * 获取 Hash 类型的字段值
     * @param key 键
     * @param hashKey Hash 字段
     * @return 值
     */
    public String hget(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取整个 Hash 类型的数据
     * @param key 键
     * @return 包含所有 Hash 字段和值的 Map
     */
    public Map<String, Object> hgetAll(String key) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        // 创建新的Map并进行类型转换
        Map<String, Object> result = new java.util.HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }
        return result;
    }

    /**
     * 删除 Hash 类型的字段
     * @param key 键
     * @param hashKeys 要删除的 Hash 字段数组
     * @return 删除的字段数量
     */
    public Long hdel(String key, Object... hashKeys) {
        return stringRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断 Hash 类型中是否存在某个字段
     * @param key 键
     * @param hashKey Hash 字段
     * @return 是否存在
     */
    public boolean hHasKey(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }
    //单独给某个键设置过期时间
    public void setExpire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }
} 