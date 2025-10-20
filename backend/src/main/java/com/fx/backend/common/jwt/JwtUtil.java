package com.fx.backend.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expire-minutes}")
    private long expireMinutes;

    public String generateToken(String subject, Map<String, String> claims) {
        Algorithm algo = Algorithm.HMAC256(secret);
        var builder = JWT.create().withIssuer(issuer).withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(expireMinutes, ChronoUnit.MINUTES)));
        if (claims != null) {
            claims.forEach(builder::withClaim);
        }
        return builder.sign(algo);
    }

    public DecodedJWT verify(String token) {
        Algorithm algo = Algorithm.HMAC256(secret);
        return JWT.require(algo).withIssuer(issuer).build().verify(token);
    }
}


