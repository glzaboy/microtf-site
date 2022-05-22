package com.microtf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 程序启动
 * @author glzaboy
 */
@SpringBootApplication
@EnableWebMvc
@EnableJpaRepositories(basePackages = {"com.microtf.framework.jpa","com.microtf.sharebook.jpa"})
@ComponentScan({"com.microtf"})
@EntityScan({"com.microtf.framework.jpa","com.microtf.sharebook.jpa"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
