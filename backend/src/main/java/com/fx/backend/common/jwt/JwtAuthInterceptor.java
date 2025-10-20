package com.fx.backend.common.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public JwtAuthInterceptor(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        // 放行无需鉴权的接口
        if (path.startsWith("/api/auth/login") || path.startsWith("/actuator") ) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        String token = auth.substring(7);
        try {
            DecodedJWT jwt = jwtUtil.verify(token);
            request.setAttribute("jwt", jwt);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}


