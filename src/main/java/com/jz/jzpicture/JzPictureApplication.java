package com.jz.jzpicture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.jz.jzpicture.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
public class JzPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(JzPictureApplication.class, args);
    }

}
