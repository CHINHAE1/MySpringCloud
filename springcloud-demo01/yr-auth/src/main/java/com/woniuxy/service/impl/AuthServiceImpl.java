package com.woniuxy.service.impl;

import com.woniuxy.model.LoginRequest;
import com.woniuxy.model.LoginResponse;
import com.woniuxy.service.AuthService;
import com.woniuxy.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // 使用Spring Security进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 验证成功，生成JWT令牌
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", userDetails.getUsername());
            // 这里可以添加更多声明，如用户ID、角色等
            
            String token = jwtUtils.createJwt(claims);

            // 构建并返回成功的登录响应
            return LoginResponse.builder()
                    .success(true)
                    .token(token)
                    .username(userDetails.getUsername())
                    .message("登录成功")
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("用户名或密码错误: {}", e.getMessage());
            return LoginResponse.builder()
                    .success(false)
                    .message("用户名或密码错误")
                    .build();
        } catch (AuthenticationException e) {
            log.error("认证异常: {}", e.getMessage());
            return LoginResponse.builder()
                    .success(false)
                    .message("认证失败: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("服务器异常: {}", e.getMessage());
            return LoginResponse.builder()
                    .success(false)
                    .message("服务器异常，请稍后再试")
                    .build();
        }
    }
} 