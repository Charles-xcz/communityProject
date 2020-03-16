package com.charles.community.dao;

import com.charles.community.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author charles
 * @date 2020/3/16 10:52
 */
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
