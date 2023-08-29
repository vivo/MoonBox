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
package com.vivo.internet.moonbox.service.data.es.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * EsConfig - es 启动配置类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/1 10:20
 */
@SuppressWarnings("deprecation")
@Configuration
@Slf4j
public class EsConfig {

    @Value("${config.elasticsearch.nodes}")
    private String esNodesUrl;

    @Value("${config.elasticsearch.username}")
    private String esUsername;

    @Value("${config.elasticsearch.password}")
    private String esPwd;

    /**
     * ElasticSearch Rest client 配置
     *
     * @return RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esUsername, esPwd));

        // ElasticSearch 连接地址地址
        HttpHost[] httpHosts = this.getElasticSearchHttpHosts();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(httpHosts).setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
        );

        // 虚拟机关闭后，关闭客户端
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }));
        return client;
    }

    /**
     * 获取es集群地址配置
     *
     * @param
     * @return {@link HttpHost[]}
     */
    private HttpHost[] getElasticSearchHttpHosts() {
        String[] hosts = esNodesUrl.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < httpHosts.length; i++) {
            String host = hosts[i];
            host = host.replaceAll("http://", "").replaceAll("https://", "");
            Assert.isTrue(host.contains(":"),
                    String.format("your host %s format error , Please refer to [ 127.0.0.1:9200 ] ", host));
            httpHosts[i] = new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]), "http");
        }
        return httpHosts;
    }

}