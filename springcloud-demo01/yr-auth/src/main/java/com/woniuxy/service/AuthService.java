package com.woniuxy.service;

import com.woniuxy.model.LoginRequest;
import com.woniuxy.model.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 验证用户登录信息并生成JWT令牌
     * 
     * @param loginRequest 登录请求数据
     * @return 登录响应数据，包含JWT令牌
     */
    LoginResponse authenticate(LoginRequest loginRequest);
} 