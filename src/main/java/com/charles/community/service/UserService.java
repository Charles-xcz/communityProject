package com.charles.community.service;

import com.charles.community.dao.UserMapper;
import com.charles.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author charles
 * @date 2020/3/16 11:49
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
