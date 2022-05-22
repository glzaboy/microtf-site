package com.microtf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 程序启动
 * @author glzaboy
 */
@SpringBootApplication
//@EnableWebMvc
//@EnableJpaRepositories(basePackages = {"com.microtf.jpa","com.microtf.sharebook.jpa"})
//@ComponentScan({"com.microtf"})
//@EntityScan({"com.microtf.jpa","com.microtf.sharebook.jpa"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
