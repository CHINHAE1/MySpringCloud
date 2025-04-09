package com.woniuxy.controller;

import com.woniuxy.model.LoginRequest;
import com.woniuxy.model.LoginResponse;
import com.woniuxy.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 处理用户登录请求
     * 
     * @param loginRequest 登录请求数据
     * @return 登录结果，包含JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());
        
        LoginResponse response = authService.authenticate(loginRequest);
        
        if (response.isSuccess()) {
            log.info("用户 {} 登录成功", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } else {
            log.warn("用户 {} 登录失败: {}", loginRequest.getUsername(), response.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 