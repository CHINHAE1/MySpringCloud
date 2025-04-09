package com.woniuxy.service;

import com.woniuxy.model.LoginRequest;
import com.woniuxy.model.LoginResponse;
import com.woniuxy.entity.User;

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

    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户实体，如果用户不存在则返回null
     */
    User login(String username);
} 