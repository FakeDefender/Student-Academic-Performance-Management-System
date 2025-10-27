package com.fx.backend.common.ratelimit;
import com.fx.backend.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lua脚本实现原子性的限流操作
    private static final String RATE_LIMIT_SCRIPT = 
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local window = tonumber(ARGV[2])\n" +
        "local current = redis.call('GET', key)\n" +
        "if current == false then\n" +
        "    redis.call('SET', key, 1)\n" +
        "    redis.call('EXPIRE', key, window)\n" +
        "    return 1\n" +
        "elseif tonumber(current) < limit then\n" +
        "    redis.call('INCR', key)\n" +
        "    return tonumber(current) + 1\n" +
        "else\n" +
        "    return -1\n" +
        "end";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否有RateLimit注解
        RateLimit rateLimit = getRateLimitAnnotation(handler);
        if (rateLimit == null) {
            return true;
        }

        // 生成限流key
        String rateLimitKey = generateRateLimitKey(request, rateLimit);
        
        // 执行限流检查
        RedisScript<Long> script = RedisScript.of(RATE_LIMIT_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, 
            Collections.singletonList(rateLimitKey), 
            rateLimit.maxRequests(), 
            rateLimit.timeWindow());

        if (result == -1) {
            throw new BusinessException("RATE_LIMIT_EXCEEDED", 
                "请求过于频繁，请稍后再试。限制：" + rateLimit.maxRequests() + "次/" + rateLimit.timeWindow() + "秒");
        }

        return true;
    }

    private RateLimit getRateLimitAnnotation(Object handler) {
        return new RateLimit() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return RateLimit.class;
            }
            
            @Override
            public String keyPrefix() {
                return "api_rate_limit";
            }
            
            @Override
            public int timeWindow() {
                return 60;
            }
            
            @Override
            public int maxRequests() {
                return 100;
            }
            
            @Override
            public RateLimitType type() {
                return RateLimitType.IP;
            }
        };
    }

    private String generateRateLimitKey(HttpServletRequest request, RateLimit rateLimit) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(rateLimit.keyPrefix()).append(":");
        
        switch (rateLimit.type()) {
            case IP:
                keyBuilder.append(getClientIP(request));
                break;
            case USER:
                // 从JWT token中获取用户ID
                String userId = getUserIdFromToken(request);
                keyBuilder.append("user:").append(userId);
                break;
            case GLOBAL:
                keyBuilder.append("global");
                break;
        }
        
        return keyBuilder.toString();
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }

    private String getUserIdFromToken(HttpServletRequest request) {
        // 从请求头中获取token并解析用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 这里应该解析JWT token获取用户ID
            // 为了演示，返回默认值
            return "default_user";
        }
        return "anonymous";
    }
}
