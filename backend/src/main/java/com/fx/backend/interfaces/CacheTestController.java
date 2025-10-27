package com.fx.backend.interfaces;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.cache.Cacheable;
import com.fx.backend.common.ratelimit.RateLimit;
import com.fx.backend.service.CacheService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/test")
@Tag(name = "缓存和限流测试", description = "用于测试Redis缓存和接口限流功能")
public class CacheTestController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/cache")
    @Cacheable(keyPrefix = "test_cache", expireSeconds = 30)
    @Operation(summary = "测试缓存功能")
    public ResponseEntity<ApiResult<Map<String, Object>>> testCache() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "这是一个缓存测试");
        result.put("timestamp", LocalDateTime.now());
        result.put("random", Math.random());
        
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @GetMapping("/rate-limit")
    @RateLimit(keyPrefix = "test_rate_limit", maxRequests = 5, timeWindow = 60)
    @Operation(summary = "测试限流功能", description = "每分钟最多5次请求")
    public ResponseEntity<ApiResult<Map<String, Object>>> testRateLimit() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "限流测试成功");
        result.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @PostMapping("/clear-all-cache")
    @Operation(summary = "清除所有Redis缓存", description = "用于解决字符编码问题")
    public ResponseEntity<ApiResult<String>> clearAllCache() {
        try {
            cacheService.clearAllCache();
            return ResponseEntity.ok(ApiResult.ok("所有缓存清除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResult.error("CACHE_ERROR", "清除缓存失败: " + e.getMessage()));
        }
    }

    @GetMapping("/cache-status")
    @Operation(summary = "检查缓存状态", description = "检查Redis连接和缓存状态")
    public ResponseEntity<ApiResult<Map<String, Object>>> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            // 测试Redis连接
            cacheService.setCacheSafely("test_connection", "OK", 10);
            String testValue = cacheService.getCacheSafely("test_connection", String.class);
            
            status.put("redis_connected", "OK".equals(testValue));
            status.put("test_key", testValue);
            status.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResult.ok(status));
        } catch (Exception e) {
            status.put("redis_connected", false);
            status.put("error", e.getMessage());
            status.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(ApiResult.ok(status));
        }
    }
}
