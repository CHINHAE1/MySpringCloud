package com.woniuxy.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT登录校验过滤器
 * 继承OncePerRequestFilter确保在一次请求中只通过一次过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            // 从请求头中获取JWT令牌
            String jwt = getJwtFromRequest(request);
            
            // 如果JWT令牌存在且有效，则设置用户认证信息
            if (StringUtils.hasText(jwt) && jwtUtils.validateJwt(jwt)) {
                // 从JWT中获取用户名
                String username = jwtUtils.getUsername(jwt);
                
                if (username != null) {
                    // 从JWT中获取角色信息，简单示例这里只添加一个USER角色
                    List<SimpleGrantedAuthority> authorities = 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // 设置到Spring Security上下文中
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.info("JWT认证成功，用户名: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("无法设置用户认证", e);
            SecurityContextHolder.clearContext();
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取JWT令牌
     * 
     * @param request HTTP请求
     * @return JWT令牌，如果不存在则返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
} 