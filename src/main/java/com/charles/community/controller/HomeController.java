package com.charles.community.controller;

import com.charles.community.model.DiscussPost;
import com.charles.community.model.Page;
import com.charles.community.model.PostDto;
import com.charles.community.model.User;
import com.charles.community.service.DiscussPostService;
import com.charles.community.service.UserService;
import com.charles.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/16 11:54
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> posts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<PostDto> postDtos = new ArrayList<>();
        if (posts != null) {
            for (DiscussPost post : posts) {
                PostDto postDto = new PostDto();
                User user = userService.findUserById(post.getUserId());
                postDto.setUser(user);
                postDto.setDiscussPost(post);
                postDtos.add(postDto);
            }
        }
        model.addAttribute("discussPosts", postDtos);
        model.addAttribute("page", page);
        return "index";
    }

    @GetMapping("/demo")
    public String demo() {
        return "ajaxdemo";
    }
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name + ":" + age);
        return CommunityUtil.getJsonString(0, "操作成功");
    }
}
