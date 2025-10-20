package com.fx.backend.common.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod hm)) return true;
        RoleRequired rr = hm.getMethodAnnotation(RoleRequired.class);
        if (rr == null) rr = hm.getBeanType().getAnnotation(RoleRequired.class);
        if (rr == null) return true;

        Object jwtObj = request.getAttribute("jwt");
        if (jwtObj == null) { response.setStatus(401); return false; }
        DecodedJWT jwt = (DecodedJWT) jwtObj;
        String role = jwt.getClaim("role").asString();
        if (role == null || Arrays.stream(rr.value()).noneMatch(role::equals)) {
            response.setStatus(403);
            return false;
        }
        return true;
    }
}


