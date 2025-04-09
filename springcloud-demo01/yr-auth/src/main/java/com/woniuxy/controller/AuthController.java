package com.woniuxy.controller;

import com.woniuxy.entity.User;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.service.AuthService;
import com.woniuxy.utils.JWTUtils;
import com.woniuxy.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 马宇航
 * @Todo: TODO
 * @DateTime: 25/04/09/星期三 15:21
 * @Component: 成都蜗牛学苑
 **/
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    RedisUtils redisUtils;
    @PostMapping("/login")
    public ResponseMyEntity login(@RequestBody User user) {
        //不要直接传账号密码去匹配，通常只传账号
        User account = authService.login(user.getUsername());
        if (account == null) {
            return new ResponseMyEntity(500, "账号或密码错误");
        }else if (account.getPassword().equals(user.getPassword())){
            //token本身的过期时间 30min
            String token = JWTUtils.createToken(account.getUsername());
            //token使用双token，不要把长token返回给前端，因为token并非安全
            //refreshToken自身没有过期效果 所以只能通过redis来控制过期时间 60min
            String refreshToken = UUID.randomUUID().toString();
            /*
                1.用户长时间不操作，30min到60min之间，token过期，但是refreshToken还在，
                2.所以，重新自动生产新的token，而不要用户重新登录
                3.但是用户如果超过了60min，refreshToken过期，用户需要重新登录
                4.所以，refreshToken的过期时间要比token的过期时间长
                5.所以，refreshToken是一个key，token是一个value的一部分

                思考：为什么token的过期时间要设置的很短？
                核心还是安全问题，token不安全。不能让一个token一直存在
             */
            //把token和用户信息，都存起来，key是refreshToken ，用什么类型？
            Map<String, Object> value= new HashMap<>();
            value.put("token", token);
            value.put("account", account.getUsername());
            redisUtils.hsetAll(refreshToken, value);
            //给某个key设置过期时间
            redisUtils.expire(refreshToken, 60, TimeUnit.MINUTES);
            return new ResponseMyEntity(refreshToken);
        }else
            return new ResponseMyEntity(500, "账号或密码错误");
    }
} 