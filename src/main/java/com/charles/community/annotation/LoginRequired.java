package com.charles.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解,用于标记拦截器 LoginRequiredInterceptor 需要拦截的路径
 * 和在WebMvcConfig的拦截器配置里可以添加/排除拦截路径起到同样的效果
 * 这是另一种可以达到相同效果的方法而已
 *
 * @author charles
 * @date 2020/3/19 11:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
