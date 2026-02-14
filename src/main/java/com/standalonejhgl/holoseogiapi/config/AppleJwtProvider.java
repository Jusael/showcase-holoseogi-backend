package com.standalonejhgl.holoseogiapi.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleJwtProvider {

    @Value("${apple.appstore.key-id}")
    private String keyId;

    @Value("${apple.appstore.issuer-id}")
    private String issuerId;

    @Value("${apple.appstore.private-key-path}")
    private Resource privateKeyResource;

    @Value("${apple.appstore.bundle-id}")
    private String bundleId;

    public String generateJwt() {
        try {
            PrivateKey privateKey = loadPrivateKey();

            Instant now = Instant.now();

            return Jwts.builder()
                    .setHeaderParam("alg", "ES256")
                    .setHeaderParam("kid", keyId)
                    .setIssuer(issuerId)
                    .setAudience("appstoreconnect-v1")
                    .claim("bid", bundleId)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusSeconds(1800))) // 30분
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();

        } catch (Exception e) {
            throw new IllegalStateException("Apple JWT 생성 실패", e);
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {

        // NOTE : AWS 푸쉬 후, 도커내에서 파일을 탐색 하도록 코드 추가
        InputStream is;

        File externalFile = new File("appstore_server_api.p8");
        if (externalFile.exists()) {
            is = new FileInputStream(externalFile);
        } else {
            is = privateKeyResource.getInputStream(); // classpath (local)
        }

        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        key = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return KeyFactory.getInstance("EC").generatePrivate(keySpec);
    }
}