package com.fx.backend.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 * 用于标记需要缓存的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    
    /**
     * 缓存key前缀
     */
    String keyPrefix() default "";
    
    /**
     * 缓存过期时间（秒）
     */
    int expireSeconds() default 300;
    
    /**
     * 是否使用参数作为key的一部分
     */
    boolean useArgs() default true;
}
