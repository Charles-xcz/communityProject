package com.charles.community.util;

import com.charles.community.model.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象
 *
 * @author charles
 * @date 2020/3/18 22:42
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
