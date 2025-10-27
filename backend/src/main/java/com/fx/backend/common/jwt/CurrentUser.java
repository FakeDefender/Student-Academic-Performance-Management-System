package com.fx.backend.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CurrentUser {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.issuer}")
    private String issuer;
    
    public String getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Algorithm algo = Algorithm.HMAC256(secret);
                DecodedJWT decodedJWT = JWT.require(algo).withIssuer(issuer).build().verify(token);
                return decodedJWT.getSubject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    public String getCurrentUserRole() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Algorithm algo = Algorithm.HMAC256(secret);
                DecodedJWT decodedJWT = JWT.require(algo).withIssuer(issuer).build().verify(token);
                return decodedJWT.getClaim("role").asString();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
