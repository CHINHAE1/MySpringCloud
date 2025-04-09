package com.woniuxy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义UserDetailsService实现类
 * 注意：这是一个简化的实现，仅用于演示。
 * 实际应用中应该从数据库或其他数据源加载用户信息。
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    // 模拟用户数据，实际应该从数据库中加载
    private Map<String, String> users = new HashMap<>();
    
    public UserDetailsServiceImpl() {
        // 初始化一些测试用户
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        users.put("admin", encoder.encode("admin123"));
        users.put("user", encoder.encode("user123"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 检查用户是否存在
        if (!users.containsKey(username)) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 获取用户密码
        String password = users.get(username);
        
        // 设置用户角色
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        if ("admin".equals(username)) {
            authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        }
        
        // 创建UserDetails对象
        return new User(
                username,
                password,
                true,                 // 账号是否启用
                true,                 // 账号是否未过期
                true,                 // 凭证是否未过期
                true,                 // 账号是否未锁定
                Collections.singletonList(authority)
        );
    }
} 