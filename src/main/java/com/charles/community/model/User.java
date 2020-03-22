package com.charles.community.model;

import lombok.Data;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/16 11:06
 */
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
