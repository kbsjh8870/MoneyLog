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
    private final long accessTokenExpirationsMs;
    private final SecretKey refreshKey;
    private final long refreshTokenExpirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms}") long expirationsMs,
                       @Value("${jwt.refresh-key}") String refreshKey,
                       @Value("${jwt.refresh-expiration-ms}") long refreshTokenExpirationMs){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpirationsMs = expirationsMs;
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    // accessToken 생성
    public String createAccessToken(Long userId, String email){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationsMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email",email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // accessToken 파싱
    public Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // refreshToken 생성
    public String createRefreshToken(Long userId){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(refreshKey)
                .compact();
    }

    // refreshToken 파싱
    public Claims parseRefreshToken(String token){
        return Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}
