package com.fx.backend.common.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 用于标记需要限流的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 限流key前缀
     */
    String keyPrefix() default "rate_limit";
    
    /**
     * 时间窗口（秒）
     */
    int timeWindow() default 60;
    
    /**
     * 最大请求次数
     */
    int maxRequests() default 100;
    
    /**
     * 限流类型
     */
    RateLimitType type() default RateLimitType.IP;
    
    enum RateLimitType {
        IP,        // 按IP限流
        USER,      // 按用户限流
        GLOBAL     // 全局限流
    }
}
