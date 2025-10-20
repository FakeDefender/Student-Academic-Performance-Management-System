package com.fx.backend.interfaces;
import com.fx.backend.common.api.ApiResult;
import com.fx.backend.common.exception.BusinessException;
import com.fx.backend.common.jwt.JwtUtil;
import com.fx.backend.domain.entity.User;
import com.fx.backend.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth 认证", description = "登录/鉴权接口")
public class AuthController {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthController(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(
            summary = "登录获取JWT",
            description = "使用用户名与密码登录，返回token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\\n  \\\"username\\\": \\\"admin\\\",\\n  \\\"password\\\": \\\"admin123\\\"\\n}"))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功，返回token"),
                    @ApiResponse(responseCode = "400", description = "认证失败/参数错误")
            }
    )
    public ResponseEntity<ApiResult<Map<String, String>>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        User user = userMapper.findByUsername(username).orElseThrow(() -> new BusinessException("AUTH_FAIL", "用户不存在"));
        if (!user.getPassword().equals(password)) { // 演示用；生产请使用BCrypt
            throw new BusinessException("AUTH_FAIL", "密码错误");
        }
        String token = jwtUtil.generateToken(user.getId().toString(), Map.of("role", user.getRole(), "username", user.getUsername()));
        return ResponseEntity.ok(ApiResult.ok(Map.of("token", token)));
    }
}


