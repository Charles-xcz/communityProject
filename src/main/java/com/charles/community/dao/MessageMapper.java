package com.charles.community.dao;

import com.charles.community.model.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/20 9:39
 */
@Mapper
public interface MessageMapper {
    /**
     * 私信列表
     * 查询当前用户的所有会话,针对每个会话只返回最新的一条消息
     *
     * @param userId 用户id
     * @param offset offset
     * @param limit  每页条数
     * @return 会话列表
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 私信数量
     *
     * @param userId 用户id
     * @return 会话数量
     */
    int selectConversationCount(int userId);

    /**
     * 查询单个会话的消息详情
     *
     * @param conversationId 会话id
     * @param offset         offset
     * @param limit          每页条数
     * @return 单个会话的消息
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 某个会话的消息数量
     *
     * @param conversationId 会话id
     * @return 某个会话的消息数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读消息数量
     *
     * @param userId         userId
     * @param conversationId 会话id
     * @return 未读消息数
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    /**
     * 查询某个主题下的最新通知
     *
     * @param userId 用户id
     * @param topic  主题
     * @return 返回最新的通知
     */
    Message selectLatestNotice(int userId, String topic);

    /**
     * 查询某个主题的通知数量
     *
     * @param userId userId
     * @param topic  主题
     * @return 返回通知数量
     */
    int selectNoticeCount(int userId, String topic);

    /**
     * 查询某个主题的未读通知数量
     *
     * @param userId userId
     * @param topic  主题
     * @return 返回通知数量
     */
    int selectNoticeUnreadCount(int userId, String topic);

    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
