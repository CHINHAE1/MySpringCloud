package com.woniuxy.service.Impl;


import com.woniuxy.dao.UserDao;
import com.woniuxy.entity.User;
import com.woniuxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 根据用户id查找用户信息
     * @param uid
     * @return
     */
    @Override
    public User findById(Integer uid){
        User user = null;
        user = userDao.findById(uid).orElse(null);
        return user;
    }
}
