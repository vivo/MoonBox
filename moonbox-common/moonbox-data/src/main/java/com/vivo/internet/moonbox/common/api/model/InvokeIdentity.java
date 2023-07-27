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
package com.vivo.internet.moonbox.common.api.model;

import lombok.extern.slf4j.Slf4j;

/**
 * InvokeType - {@link InvokeIdentity} 平台插件标识和类型映射
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
@SuppressWarnings("ALL")
@Slf4j
public enum InvokeIdentity {

    HTTP("http", InvokeType.HTTP),

    DUBBO_PROVIDER("dubbo-provider", InvokeType.DUBBO),

    JAVA_ENTRANCE("java-entrance", InvokeType.JAVA),

    MOTAN_PROVIDER("motan-provider", InvokeType.MOTAN),

    DUBBO_CONSUMER("dubbo-consumer", InvokeType.DUBBO),

    MOTAN_CONSUMER("motan-consumer", InvokeType.MOTAN),

    MYBATIS("mybatis", InvokeType.MYBATIS),

    MYBATIS_PLUS("mybatis-plus", InvokeType.MYBATIS_PLUS),

    REDIS("redis", InvokeType.REDIS),

    IBATIS("ibatis", InvokeType.IBATIS),

    OK_HTTP("okhttp", InvokeType.OKHTTP),

    APACHE_HTTP_CLIENT("apache-http-client", InvokeType.APACHE_HTTP_CLIENT),

    GUAVA_CACHE("guava-cache", InvokeType.GUAVA_CACHE),

    HIBERNATE_PLUGIN("hibernate-plugin", InvokeType.HIBERNATE),

    CAFFEINE_CACHE("caffeine-cache", InvokeType.CAFFEINE_CACHE),

    EH_CACHE_PLUGIN("eh-cache", InvokeType.EH_CACHE),

    SPRING_MONGO("spring-mongo", InvokeType.SPRING_MONGO),

    ELASTICSEARCH_PLUGIN("elasticsearch", InvokeType.ELASTICSEARCH_PLUGIN),

    LOCAL_DATE_TIME_PLUGIN("local-date-time", InvokeType.LOCAL_DATE_TIME),

    JAVA_SHUFFLE_PLUGIN("java-shuffle", InvokeType.JAVA_SHUFFLE_PLUGIN);

    private String identity;

    private InvokeType invokeType;

    InvokeIdentity(String identity, InvokeType invokeType) {
        this.identity = identity;
        this.invokeType = invokeType;
    }

    public String getIdentity() {
        return identity;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }}