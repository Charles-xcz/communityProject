package com.charles.community.dao;

import com.charles.community.model.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/19 20:11
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
