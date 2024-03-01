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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
@Slf4j
@Configuration
public class JedisClusterConfig {

    @Resource
    private RedisProperties redisProperties;

    /**
     * 获取JedisCluster实例
     * 注意：返回的JedisCluster是单例的，并且可以直接注入到其他类中去使用
     * 当前只实现了redis集群模式，如果单节点部署，需要自行添加
     * @return JedisCluster实例
     */
    @Bean
    @Conditional(RedisPropCondition.class)
    public JedisCluster getJedisCluster() {
        try {
            //获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
            String[] serverArray = redisProperties.getClusterNodes().split(",");
            Set<HostAndPort> nodes = new HashSet<>();

            // 遍历每个节点的地址，将其解析成主机名和端口号，并添加到nodes中保存
            for (String ipPort : serverArray) {
                String[] ipPortPair = ipPort.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.parseInt(ipPortPair[1].trim())));
            }
            // 创建一个JedisCluster实例，并设置相关的参数，比如nodes、commandTimeout、poolConfig等
            return new JedisCluster(nodes, redisProperties.getCommandTimeout(), 1000, 1,
                    redisProperties.getPassword(), new GenericObjectPoolConfig<>());
        } catch (Exception e) {
            log.error("redis config init failed", e);
            return null;
        }
    }
}