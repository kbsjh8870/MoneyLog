package org.example.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationsMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms}") long expirationsMs){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationsMs = expirationsMs;
    }

    // jwt 생성
    public String createToken(Long userId, String email){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationsMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email",email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // jwt 파싱
    public Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
