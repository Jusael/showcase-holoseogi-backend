package com.standalonejhgl.holoseogiapi.service.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${app.jwt.secret}")   // application.yml 에서 가져오기
    private String secret;

    //TODO: “JWT sub가 내부 user_id가 아님 현재 비정상 반환이 의심됨”
    @Override
    public String parseSubject(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject(); // = uid
    }
    @Override
    public String issueToken(String uid, long expireSeconds) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(uid)
                .issuedAt(new java.util.Date(now))
                .expiration(new java.util.Date(now + expireSeconds * 11600
                ))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

}