package com.woniuxy.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.utils.JWTUtils;
import com.woniuxy.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 马宇航
 * @Todo: 全局过滤器
 * @DateTime: 25/04/09/星期三 16:43
 * @Component: 成都蜗牛学苑
 **/
@Component
public class AuthGlobalFilter implements GlobalFilter,Ordered {
    @Autowired
    RedisUtils redisUtils;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("我们的全局过滤器生效！"+exchange.getRequest().getURI());
        //判断是否是登录请求，如果是登录请求，直接放行
        if (exchange.getRequest().getURI().getPath().contains("/auth/login")){
            return chain.filter(exchange);
        }
        //判断是否是注册请求，如果是注册请求，直接放行
        if (exchange.getRequest().getURI().getPath().contains("/auth/register")){
            return chain.filter(exchange);
        }
        //不是上诉请求，就需要校验token
        String refreshToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        //判断是否有refreshToken
        if (redisUtils.hasKey(refreshToken)) {
            //取出里面的内容
            Map<String, Object> content = redisUtils.hgetAll(refreshToken);
            String token = (String) content.get("token");
            //判断token是否过期
            JWTUtils.TokenStatusEnum verify = JWTUtils.verify(token);
            //只有token过期在30min~60min之间，才需要重新生成token
            if (verify == JWTUtils.TokenStatusEnum.TOKEN_EXPIRE) {
                //token过期，但是refreshToken还在，重新生成token
                String newToken = JWTUtils.createToken((String) content.get("account"));
                //更新redis中的token
                content.put("token", newToken);
                //把新的token重新更新进去
                redisUtils.hsetAll(refreshToken, content);
                //给某个key设置过期时间
                redisUtils.expire(refreshToken, 60, TimeUnit.MINUTES);
                //重新更新了 新token后，要放行
                return chain.filter(exchange);
            }
            else if (verify == JWTUtils.TokenStatusEnum.TOKEN_ERROR) {
                //定义一个自定义Flux流式响应的方法
               return returnUnauthorized(exchange);
            } else if (verify == JWTUtils.TokenStatusEnum.TOKEN_SUCCESS) {
                //校验通过业务
                return chain.filter(exchange);
            }else{
                //token 完全不存在
                return returnUnauthorized(exchange);
            }
        }else {
            return returnUnauthorized(exchange);
        }
    }

    private Mono<Void> returnUnauthorized(ServerWebExchange exchange) {
        //获取响应
        ServerHttpResponse response = exchange.getResponse();
        ResponseMyEntity responseMyEntity = new ResponseMyEntity(HttpStatus.UNAUTHORIZED.value(), "token过期");
        //想要在WebFlux返回，不能直接用，用我们的字节流方案，自己处理自定义响应类型
        try {
            //转换为字节流
            byte[] bytes = new ObjectMapper().writeValueAsBytes(responseMyEntity);
            DataBuffer wrap = response.bufferFactory().wrap(bytes);
            //响应Mono方案
            return response.writeWith(Mono.just(wrap));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 