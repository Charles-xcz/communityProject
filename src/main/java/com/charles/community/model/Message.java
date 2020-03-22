package com.charles.community.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/20 9:37
 */
@Data
@Accessors(chain = true)
public class Message {
    private int id;
    private int fromId;
    private int toId;
    /**
     * 由fromId和toId拼接而成,且id数值小的在前,即不分from和to的顺序
     * 冗余数据,方便查询
     */
    private String conversationId;
    private String content;
    /**
     * 0表示未读,1表示已读,2表示被删除
     */
    private int status;
    private Date createTime;
}
