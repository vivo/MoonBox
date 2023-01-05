package com.vivo.internet.moonbox.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * MoonboxApplication - 接口暴露服务
 *
 * @author 11038649
 * @version 1.0
 * @since 2020/9/22 14:37
 */
@SpringBootApplication(scanBasePackages="com.vivo.internet.moonbox")
@ImportResource({"classpath:spring/time-convert.xml"})
public class MoonBoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoonBoxApplication.class, args);
    }

}
