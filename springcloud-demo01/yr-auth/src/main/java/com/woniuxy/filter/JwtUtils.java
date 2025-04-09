package com.woniuxy.filter;

import com.woniuxy.utils.JWTUtils;
import org.springframework.stereotype.Component;

/**
 * JWT工具类适配器
 * 适配 JWTUtils 到 JwtAuthenticationFilter 所需的接口
 */
@Component
public class JwtUtils {

    /**
     * 验证JWT令牌是否有效
     * 
     * @param token JWT令牌
     * @return 如果令牌有效则返回true，否则返回false
     */
    public boolean validateJwt(String token) {
        JWTUtils.TokenStatusEnum status = JWTUtils.verify(token);
        return status == JWTUtils.TokenStatusEnum.TOKEN_SUCCESS;
    }

    /**
     * 从JWT令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsername(String token) {
        return JWTUtils.getUserName(token);
    }
} 