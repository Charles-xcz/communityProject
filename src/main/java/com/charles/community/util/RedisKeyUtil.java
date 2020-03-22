package com.charles.community.util;

/**
 * @author charles
 * @date 2020/3/20 19:01
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity:";
    private static final String PREFIX_USER_LIKE = "like:user:";
    private static final String PREFIX_FOLLOWEE = "followee:";
    private static final String PREFIX_FOLLOWER = "follower:";
    private static final String PREFIX_KAPTCHA = "kaptcha:";
    private static final String PREFIX_TICKET = "ticket:";
    private static final String PREFIX_USER = "user:";
    private static final String PREFIX_UV = "uv:";
    private static final String PREFIX_DAU = "dau:";
    private static final String PREFIX_POST = "post:";

    /**
     * 某个实体的赞,构造key
     * like:entity:entityType:entityId->set{userId...}
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞,构造key
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + userId;
    }

    /**
     * 某个用户关注的实体
     * followee:userId:entityType->zset(entityId,now)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId->zset(userId,now)
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + owner;
    }

    /**
     * 登录的凭证
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + ticket;
    }

    /**
     * 缓存用户
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + userId;
    }
}
