package com.charles.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.charles.community.model.Event;
import com.charles.community.model.Message;
import com.charles.community.model.Page;
import com.charles.community.model.User;
import com.charles.community.service.MessageService;
import com.charles.community.service.UserService;
import com.charles.community.util.CommunityConstant;
import com.charles.community.util.CommunityUtil;
import com.charles.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author charles
 * @date 2020/3/20 10:20
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            conversationList.forEach(conversation -> {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", conversation);
                map.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                //会话的头像框
                User target = getLetterTarget(conversation.getConversationId());
                map.put("target", target);
                conversations.add(map);
            });
        }
        model.addAttribute("conversations", conversations);
        //查询总未读消息
        int unreadCount = messageService.findLetterUnreadCount(user.getId());
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId());
        model.addAttribute("letterUnreadCount", unreadCount);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            letterList.forEach(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            });
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> messages) {
        List<Integer> ids = new ArrayList<>();
        if (messages != null) {
            messages.forEach(message -> {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            });
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJsonString(1, "用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId())
                .setToId(target.getId())
                .setContent(content)
                .setCreateTime(new Date());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> commentVo = getNoticeVo(user.getId(), TOPIC_COMMENT);
        model.addAttribute("commentNotice", commentVo);
        Map<String, Object> followVo = getNoticeVo(user.getId(), TOPIC_FOLLOW);
        model.addAttribute("followNotice", followVo);
        Map<String, Object> likeVo = getNoticeVo(user.getId(), TOPIC_LIKE);
        model.addAttribute("likeNotice", likeVo);
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId());
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId());
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/notice";
    }

    private Map<String, Object> getNoticeVo(int userId, String topic) {
        Message message = messageService.findLatestNotice(userId, topic);
        Map<String, Object> noticeVo = new HashMap<>();
        if (message != null) {
            noticeVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            noticeVo.put("user", userService.findUserById((Integer) data.get("userId")));
            noticeVo.put("entityType", data.get("entityType"));
            noticeVo.put("entityId", data.get("entityId"));
            noticeVo.put("postId", data.get("postId"));
            int count = messageService.findNoticeCount(userId, topic);
            noticeVo.put("count", count);
            int unread = messageService.findNoticeUnreadCount(userId, topic);
            noticeVo.put("unread", unread);
        }
        return noticeVo;
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticesDetail(@PathVariable("topic") String topic, Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            noticeList.forEach(notice -> {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content1 = notice.getContent();
                String content = HtmlUtils.htmlUnescape(content1);
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            });
        }
        model.addAttribute("notices", noticeVoList);
        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }
}
