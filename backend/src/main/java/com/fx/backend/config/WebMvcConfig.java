package com.fx.backend.config;

import com.fx.backend.common.jwt.JwtAuthInterceptor;
import com.fx.backend.common.jwt.RoleInterceptor;
import com.fx.backend.common.ratelimit.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final RoleInterceptor roleInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor, 
                       RoleInterceptor roleInterceptor,
                       RateLimitInterceptor rateLimitInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.roleInterceptor = roleInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
        
        // 角色权限拦截器
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/api/**");
        
        // 限流拦截器
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}


