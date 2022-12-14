package com.microtf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 程序启动
 *
 * @author glzaboy
 */
@SpringBootApplication
@EnableWebMvc
@EnableJpaRepositories(basePackages = {"com.microtf.framework.jpa"})
@ComponentScan({"com.microtf"})
@EntityScan({"com.microtf.framework.jpa"})
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
