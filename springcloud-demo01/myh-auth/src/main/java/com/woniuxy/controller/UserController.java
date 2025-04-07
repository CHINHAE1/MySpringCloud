package com.woniuxy.controller;

import com.woniuxy.entity.User;
import com.woniuxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@EnableFeignClients
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 根据用户id，查找用户
     * @param uid
     * @return
     */
    @GetMapping("/findById/{uid}")
    public User findById(@PathVariable Integer uid){
        User user = userService.findById(uid);
        return user;
    }
}
