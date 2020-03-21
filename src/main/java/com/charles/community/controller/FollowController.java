package com.charles.community.controller;

import com.charles.community.model.Page;
import com.charles.community.model.User;
import com.charles.community.service.CommunityConstant;
import com.charles.community.service.FollowService;
import com.charles.community.service.UserService;
import com.charles.community.util.CommunityUtil;
import com.charles.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/21 10:07
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        followService.follow(hostHolder.getUser().getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已关注!");
    }

    @ResponseBody
    @PostMapping("/unfollow")
    public String unFollow(int entityType, int entityId) {
        followService.unFollow(hostHolder.getUser().getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已取消关注!");
    }

    /**
     * 某用户关注的用户列表
     */
    @GetMapping("/followees/{userId}")
    public String followees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("没有该用户!");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows(((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER)));

        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (followeeList != null) {
            followeeList.forEach(map -> {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowedUser(u.getId()));
            });
        }
        model.addAttribute("followees", followeeList);
        return "site/followee";
    }

    /**
     * 某用户的粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public String followers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("没有该用户!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows(((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId)));

        List<Map<String, Object>> followersList = followService.findFollowersList(ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());
        if (followersList != null) {
            followersList.forEach(map -> {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowedUser(u.getId()));
            });
        }
        model.addAttribute("followers", followersList);
        return "site/follower";
    }

    private boolean hasFollowedUser(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
