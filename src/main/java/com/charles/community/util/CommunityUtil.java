package com.charles.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author charles
 * @date 2020/3/17 9:52
 */
public class CommunityUtil {
    /**
     * 生成随机字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5密码加密
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJsonString(int coed, String msg) {
        return getJsonString(coed, msg, null);
    }

    public static String getJsonString(int coed) {
        return getJsonString(coed, null, null);
    }

    public static String getJsonString(int coed, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", coed);
        jsonObject.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }
}
