package com.charles.community.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/16 11:32
 */
@Data
@Accessors(chain = true)
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
