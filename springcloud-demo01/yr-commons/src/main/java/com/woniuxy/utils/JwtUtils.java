package com.woniuxy.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类，用于生成、解析和验证JWT令牌
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * 默认密钥，实际生产环境应从配置文件中读取
     */
    private static final String DEFAULT_SECRET_KEY = "woniuxy-spring-cloud-jwt-secret-key-must-be-at-least-256-bits";
    
    /**
     * 默认过期时间（30分钟）
     */
    private static final long DEFAULT_EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * 根据密钥生成SecretKey对象
     */
    private SecretKey generalKey(String secretKey) {
        byte[] encodedKey = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(encodedKey);
    }

    /**
     * 创建JWT
     * 
     * @param claims 存储在JWT中的声明信息
     * @param ttlMillis 过期时间（毫秒）
     * @return JWT token
     */
    public String createJwt(Map<String, Object> claims, long ttlMillis) {
        SecretKey secretKey = generalKey(DEFAULT_SECRET_KEY);
        
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        
        // 如果未指定过期时间，则使用默认过期时间
        long expireTime = ttlMillis > 0 ? ttlMillis : DEFAULT_EXPIRE_TIME;
        Date expireDate = new Date(nowMillis + expireTime);
        
        // 创建JWT
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())  // JWT ID
                .setIssuedAt(now)                     // 签发时间
                .setExpiration(expireDate)            // 过期时间
                .signWith(secretKey);                 // 签名算法和密钥
        
        // 添加自定义声明信息
        if (claims != null) {
            claims.forEach(builder::claim);
        }
        
        return builder.compact();
    }
    
    /**
     * 创建使用默认过期时间的JWT
     * 
     * @param claims 存储在JWT中的声明信息
     * @return JWT token
     */
    public String createJwt(Map<String, Object> claims) {
        return createJwt(claims, DEFAULT_EXPIRE_TIME);
    }
    
    /**
     * 解析JWT
     * 
     * @param jwt JWT token
     * @return Claims对象，包含JWT中的所有声明信息
     * @throws JwtException 如果JWT无效或已过期
     */
    public Claims parseJwt(String jwt) throws JwtException {
        SecretKey secretKey = generalKey(DEFAULT_SECRET_KEY);
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
    
    /**
     * 验证JWT是否有效
     * 
     * @param jwt JWT token
     * @return 是否有效
     */
    public boolean validateJwt(String jwt) {
        try {
            parseJwt(jwt);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取JWT过期时间
     * 
     * @param jwt JWT token
     * @return 过期时间，如果JWT无效则返回null
     */
    public Date getExpirationDate(String jwt) {
        try {
            Claims claims = parseJwt(jwt);
            return claims.getExpiration();
        } catch (JwtException e) {
            log.error("Failed to get expiration date: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从JWT中获取用户ID
     * 
     * @param jwt JWT token
     * @return 用户ID，如果JWT无效或不包含用户ID则返回null
     */
    public String getUserId(String jwt) {
        try {
            Claims claims = parseJwt(jwt);
            return claims.get("userId", String.class);
        } catch (JwtException e) {
            log.error("Failed to get userId: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从JWT中获取用户名
     * 
     * @param jwt JWT token
     * @return 用户名，如果JWT无效或不包含用户名则返回null
     */
    public String getUsername(String jwt) {
        try {
            Claims claims = parseJwt(jwt);
            return claims.get("username", String.class);
        } catch (JwtException e) {
            log.error("Failed to get username: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 判断JWT是否已过期
     * 
     * @param jwt JWT token
     * @return 是否已过期
     */
    public boolean isExpired(String jwt) {
        try {
            Claims claims = parseJwt(jwt);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("Failed to check if JWT is expired: {}", e.getMessage());
            return true;
        }
    }
} 