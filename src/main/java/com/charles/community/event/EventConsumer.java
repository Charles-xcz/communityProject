package com.charles.community.event;

import com.alibaba.fastjson.JSONObject;
import com.charles.community.model.DiscussPost;
import com.charles.community.model.Event;
import com.charles.community.model.Message;
import com.charles.community.service.DiscussPostService;
import com.charles.community.service.ElasticsearchService;
import com.charles.community.service.MessageService;
import com.charles.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/21 21:48
 */
@Component
@Slf4j
public class EventConsumer implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null) {
            log.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息内容为空");
            return;
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID)
                .setToId(event.getEntityUserId())
                .setConversationId(event.getTopic())
                .setCreateTime(new Date());
        Map<String, Object> map = new HashMap<>();

        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));

        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null) {
            log.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息内容为空");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
}
