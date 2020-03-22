package com.charles.community.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/21 21:37
 */
@Data
@Accessors(chain = true)
public class Event {
    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    /**
     * 事件接收人
     */
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
