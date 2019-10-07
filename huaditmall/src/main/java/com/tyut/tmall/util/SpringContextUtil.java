package com.tyut.tmall.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName SpringContextUtil
 * @Description  从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了
 *                  通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存
 * @Author 王琛
 * @Date 2019/9/25 15:06
 * @Version 1.0
 */
public class SpringContextUtil implements ApplicationContextAware {

    private SpringContextUtil(){
    }

    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz){
        return  applicationContext.getBean(clazz);
    }
}
