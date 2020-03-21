package com.charles.community.service;

import com.charles.community.model.User;
import com.charles.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author charles
 * @date 2020/3/21 9:59
 */
@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();
                //传入当前时间作为分数
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    /**
     * 查询某用户关注的某类实体的数量
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @return 关注该类实体的数量
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 查询某个实体的粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体Id
     * @return 粉丝数量
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询用户是否关注某实体
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 已关注返回true未关注返回false
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 查询某用户的关注列表,分页
     *
     * @param userId     userId
     * @param entityType 关注的实体类型
     * @param offset     offset
     * @param limit      limit
     * @return list
     */
    public List<Map<String, Object>> findFolloweeList(int userId, int entityType, int offset, int limit) {
        if (entityType == ENTITY_TYPE_USER) {
            String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
            return getUserMaps(followeeKey, offset, limit);
        } else {
            return null;
        }

    }

    /**
     * 查询某实体的粉丝列表,分页
     *
     * @param entityType 实体类型
     * @param entityId   entityId
     * @param offset     offset
     * @param limit      limit
     * @return list
     */
    public List<Map<String, Object>> findFollowersList(int entityType, int entityId, int offset, int limit) {
        if (entityType == ENTITY_TYPE_USER) {
            String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, entityId);
            return getUserMaps(followerKey, offset, limit);
        } else {
            return null;
        }
    }

    private List<Map<String, Object>> getUserMaps(String followerKey, int offset, int limit) {
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> followerList = new ArrayList<>();
        targetIds.forEach(targetId -> {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            followerList.add(map);
        });
        return followerList;
    }
}
