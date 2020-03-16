package com.charles.community.model;

import lombok.Data;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/16 11:32
 */
@Data
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
