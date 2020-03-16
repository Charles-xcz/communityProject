package com.charles.community.dao;

import com.charles.community.model.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/16 11:33
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * Param注解用于给参数取别名,
     * 如果只有一个参数,并且在<if>里使用,则必须加别名.
     *
     * @param userId userId
     * @return int
     */
    int selectDiscussPostRows(@Param("userId") int userId);
}
