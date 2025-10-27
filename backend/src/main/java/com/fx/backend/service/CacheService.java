package com.fx.backend.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     */
    public void setCache(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     */
    public Object getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存（按模式）
     */
    public void deleteCacheByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 清除学生成绩相关缓存
     */
    public void clearStudentGradesCache(Long studentId) {
        // 清除所有相关的缓存key
        String[] patterns = {
            "student_grades_by_id:*" + studentId + "*",
            "student_grades_by_number:*",
            "teacher_student_grades:*"
        };
        
        for (String pattern : patterns) {
            deleteCacheByPattern(pattern);
        }
    }

    /**
     * 清除所有Redis缓存
     */
    public void clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            // 如果清除失败，记录日志但不抛出异常
            System.err.println("清除缓存时发生错误: " + e.getMessage());
        }
    }

    /**
     * 安全地获取缓存值，处理字符编码问题
     */
    public <T> T getCacheSafely(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return clazz.cast(value);
            }
        } catch (Exception e) {
            // 如果获取缓存失败，删除该缓存键
            redisTemplate.delete(key);
            System.err.println("获取缓存失败，已删除缓存键 " + key + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 安全地设置缓存值，处理字符编码问题
     */
    public void setCacheSafely(String key, Object value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("设置缓存失败: " + e.getMessage());
        }
    }
}