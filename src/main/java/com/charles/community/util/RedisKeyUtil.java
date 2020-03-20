package com.charles.community.util;

/**
 * @author charles
 * @date 2020/3/20 19:01
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity:";

    /**
     * 某个实体的赞,构造key
     * like:entity:entityType:entityId->set{userId...}
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + entityType + SPLIT + entityId;
    }
}
