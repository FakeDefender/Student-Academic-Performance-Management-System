package com.fx.backend.common.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
public class CacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(cacheable)")
    public Object around(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        // 生成缓存key
        String cacheKey = generateCacheKey(joinPoint, cacheable);
        
        // 尝试从缓存获取
        Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 执行原方法
        Object result = joinPoint.proceed();
        
        // 将结果存入缓存
        if (result != null) {
            redisTemplate.opsForValue().set(cacheKey, result, cacheable.expireSeconds());
        }
        
        return result;
    }

    private String generateCacheKey(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        StringBuilder keyBuilder = new StringBuilder();
        
        // 添加key前缀
        if (StringUtils.hasText(cacheable.keyPrefix())) {
            keyBuilder.append(cacheable.keyPrefix()).append(":");
        } else {
            // 使用类名和方法名作为默认前缀
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = signature.getDeclaringType().getSimpleName();
            String methodName = signature.getMethod().getName();
            keyBuilder.append(className).append(":").append(methodName).append(":");
        }
        
        // 添加参数信息
        if (cacheable.useArgs()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 使用参数的实际值来生成唯一的key
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) keyBuilder.append("_");
                    if (args[i] != null) {
                        keyBuilder.append(args[i].toString());
                    } else {
                        keyBuilder.append("null");
                    }
                }
            }
        }
        
        return keyBuilder.toString();
    }
}
