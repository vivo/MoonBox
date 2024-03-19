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
package com.vivo.internet.moonbox.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JEDIS的属性配置，目前只给了jedis的客户端配置
 * 使用cluster模式，单节点需自行修改
 * 默认无密码方式，可以更改配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private int    expireSeconds;
    private String clusterNodes;
    private String password = null;
    private String host;
    private int    commandTimeout;
}