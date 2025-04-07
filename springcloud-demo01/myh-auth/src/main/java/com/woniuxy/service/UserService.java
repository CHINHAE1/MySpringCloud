package com.woniuxy.service;

import com.woniuxy.entity.User;
import org.springframework.stereotype.Service;


public interface UserService {

    User findById(Integer uid);
}
