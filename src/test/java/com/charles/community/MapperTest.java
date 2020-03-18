package com.charles.community;

import com.charles.community.dao.DiscussPostMapper;
import com.charles.community.dao.UserMapper;
import com.charles.community.model.DiscussPost;
import com.charles.community.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

/**
 * @author charles
 * @date 2020/3/16 11:21
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testUserMapper() {
        User user = userMapper.selectById(101);
        System.out.println(user);
        System.out.println(userMapper.selectByName("liubei"));
    }
    @Test
    public void testDiscussPostMapper() {
        System.out.println(discussPostMapper.selectDiscussPostRows(149));
        System.out.println(discussPostMapper.selectDiscussPosts(149, 0, 10));
    }

    @Test
    public void testLoginTicket() {

    }
}
