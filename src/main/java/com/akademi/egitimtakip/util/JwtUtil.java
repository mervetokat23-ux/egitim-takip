package com.akademi.egitimtakip.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT Utility Class
 * 
 * JWT token oluşturma ve doğrulama işlemlerini yönetir.
 * H2 database ile uyumlu çalışır, token'lar memory'de saklanır.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 saat (milliseconds)
    private Long expiration;

    /**
     * Secret key'i oluşturur (HS256 algoritması için)
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Kullanıcı email'inden JWT token oluşturur
     */
    public String generateToken(String email, String rol) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Token'dan email çıkarır
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Token'dan rol çıkarır
     * Rol bilgisi yoksa null döner
     */
    public String getRolFromToken(String token) {
        try {
            return getClaimFromToken(token, claims -> {
                Object rolClaim = claims.get("rol");
                if (rolClaim == null) {
                    return null;
                }
                return rolClaim.toString();
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Token'dan belirli bir claim'i çıkarır
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'dan tüm claim'leri çıkarır
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Token'ın geçerliliğini kontrol eder
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = getEmailFromToken(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Token'ın süresinin dolup dolmadığını kontrol eder
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Token'dan expiration date'i çıkarır
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}

