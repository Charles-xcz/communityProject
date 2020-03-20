package com.charles.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

/**
 * @author charles
 * @date 2020/3/20 17:56
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String, Object> template;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        template.opsForValue().set(redisKey, 1);
        System.out.println(template.opsForValue().get(redisKey));
        System.out.println(template.opsForValue().increment(redisKey));
        System.out.println(template.opsForValue().decrement(redisKey));
    }

    @Test
    public void testKeys() {
        template.opsForValue().set("test:user", 1);
        template.delete("test:user");
        template.expire("test:students", 10, TimeUnit.SECONDS);
    }

    //多次操作同一个key
    @Test
    public void testBoundOperation() {
        String redisKey = "test:count";
        BoundValueOperations<String, Object> operations = template.boundValueOps(redisKey);
        operations.decrement();
        operations.increment();
    }

    //编程式事务
    @Test
    public void testTransactional() {
        Object obj = template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:set";
                operations.multi();
                operations.opsForSet().add(redisKey, "张三");
                operations.opsForSet().add(redisKey, "李四");
                operations.opsForSet().add(redisKey, "wag");
                operations.opsForSet().add(redisKey, "zeep");
                System.out.println(operations.opsForSet().members(redisKey));
                System.out.println("===");
                return operations.exec();
            }
        });
        System.out.println(obj);
    }

    // 编程式事务
    @Test
    public void testTransactional2() {
        Object obj = template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
