package com.charles.community.controller;

import com.charles.community.model.DiscussPost;
import com.charles.community.model.Page;
import com.charles.community.service.ElasticsearchService;
import com.charles.community.service.LikeService;
import com.charles.community.service.UserService;
import com.charles.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/22 16:31
 */
@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        org.springframework.data.domain.Page<DiscussPost> searchResults = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResults == null ? 0 : (int) searchResults.getTotalElements());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResults != null) {
            searchResults.forEach(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            });
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        return "site/search";
    }
}
