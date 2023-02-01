/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
