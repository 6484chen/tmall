package com.tyut.tmall;

import com.tyut.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
//因为 jpa 的dao 做了 链接 redis 的，如果放在同一个包下，会彼此影响，出现启动异常
//所以elisaticsearch和JPA要指明不同的包
@EnableElasticsearchRepositories(basePackages = "com.tyut.tmall.elasticsearch")
@EnableJpaRepositories(basePackages = {"com.tyut.tmall.dao","com.tyut.tmall.pojo"})
@ServletComponentScan
public class Application extends SpringBootServletInitializer {
    //为了打包为war包，需要继承的SpringBootServletInitializer包
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(Application.class);
    }

    static{
        PortUtil.checkPort(6379,"Redis 服务端",true);
        PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
        PortUtil.checkPort(5601,"Kibana 工具", true);
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);    
    }
}