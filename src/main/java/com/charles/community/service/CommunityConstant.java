package com.charles.community.service;

/**
 * @author charles
 * @date 2020/3/17 12:46
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;
    /**
     * 默认状态登录凭证的超时时间 12h
     */
    int DEFAULT_EXPIRED_SECONDS = 12 * 3600;
    /**
     * remember me 状态登录凭证的超时时间 15天
     */
    int REMEMBER_EXPIRED_SECONDS = 15 * 24 * 3600;
    /**
     * 实体类型:帖子
     */
    int ENTITY_TYPE_POST = 1;
    /**
     * 实体类型:评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
