package com.tyut.tmall.config;

import com.tyut.tmall.interceptor.LoginInterceptor;
import com.tyut.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName WebMvcConfigurer
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/24 10:47
 * @Version 1.0
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(getOtherIntercepter())
                .addPathPatterns("/**");
    }
}
