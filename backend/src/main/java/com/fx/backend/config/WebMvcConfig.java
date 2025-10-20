package com.fx.backend.config;

import com.fx.backend.common.jwt.JwtAuthInterceptor;
import com.fx.backend.common.jwt.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final RoleInterceptor roleInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor, RoleInterceptor roleInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.roleInterceptor = roleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(roleInterceptor).addPathPatterns("/api/**");
    }
}


