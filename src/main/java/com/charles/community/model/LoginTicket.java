package com.charles.community.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/18 9:40
 */
@Data
@Accessors(chain = true)
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    /**
     * ticket的状态,0代表有效,1代表失效
     */
    private int status;
    private Date expired;
}
