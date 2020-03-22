package com.charles.community.event;

import com.alibaba.fastjson.JSONObject;
import com.charles.community.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author charles
 * @date 2020/3/21 21:44
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     * @param event 事件
     */
    public void fireEvent(Event event) {
        //将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
