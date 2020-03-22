package com.charles.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {
    @PostConstruct
    public void init() {
        /*
        解决netty启动冲突问题(redis和elasticsearch底层都调用netty了,产生的冲突)
        io.netty.util.NettyRuntime setAvailableProcessors方法中报错
        通过setProperty("es.set.netty.runtime.available.processors", "false")修改了
        org.elasticsearch.transport.netty4.Netty4Utils类中的 setAvailableProcessors() 方法
         */
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
