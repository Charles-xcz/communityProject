package com.charles.community.model;

import lombok.Data;

/**
 * @author charles
 * @date 2020/3/18 12:05
 */
@Data
public class PostDto {
    private User user;
    private DiscussPost discussPost;
    private long likeCount;
}
