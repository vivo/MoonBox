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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * InvokeType - {@link InvokeType} 平台支持的所有调用类型
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
@SuppressWarnings("ALL")
@Slf4j
public enum InvokeType {

    HTTP("http"),

    JAVA("java"),

    MYBATIS("mybatis"),

    MYBATIS_PLUS("mybatis-plus"),

    IBATIS("ibatis"),

    REDIS("redis"),

    DUBBO("dubbo"),

    MOTAN("motan"),

    HIBERNATE("hibernate"),

    SOCKETIO("socketio"),

    REST_CLIENT("rest-client"),

    OKHTTP("okhttp"),

    APACHE_HTTP_CLIENT("apache-http-client"),

    GUAVA_CACHE("guava-cache"),

    EH_CACHE("eh-cache"),

    UNIVERSAL("universal"),

    CAFFEINE_CACHE("caffeine-cache"),

    KAFKA_PRODUCER("kafka-producer"),

    SPRING_SESSION("spring-session"),

    JAVA_SHUFFLE_PLUGIN("java-shuffle"),

    ELASTICSEARCH_PLUGIN("elasticsearch"),

    SPRING_MONGO("spring-mongo"),

    LOCAL_DATE_TIME("local-date-time"),

    JPA("jpa"),

    TARS_CLIENT("tars-client"),

    HBASE("hbase"),

    UNKNOWN("unknown");

    private String invokeName;

    InvokeType(String invokeName) {
        this.invokeName = invokeName;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public static boolean isHttpSubInvocation(InvokeType invokeType) {
        if (null == invokeType) {
            return false;
        }
        return Objects.equals(invokeType.name(), OKHTTP.name())
                || Objects.equals(invokeType.name(), APACHE_HTTP_CLIENT.name())
                || Objects.equals(invokeType.name(), REST_CLIENT.name());
    }

    /**
     * 判断下是否记录栈，类似vivo-cfg/guava等很多系统调用量非常大容易有性能问题所以不记录
     *
     * @param invokeType
     * @return
     */
    public static boolean isRecordStack(InvokeType invokeType) {
        return null != invokeType;
    }

    /**
     * 对于下面这几种类型插件，不需要记录匹配过程，如果失败了，直接去执行源代码逻辑就行。
     * 
     * @param invokeType
     * @return
     */
    public static boolean isNotRecordMockInvocation(InvokeType invokeType) {
        if (null == invokeType) {
            return false;
        }
        return invokeType.equals(InvokeType.JAVA_SHUFFLE_PLUGIN) || invokeType.equals(InvokeType.UNIVERSAL);
    }

    /***
     * @param invokeType
     * @return
     */
    public static boolean isKeyInvocation(InvokeType invokeType) {
        if (invokeType == null) {
            return false;
        }
        return invokeType.equals(InvokeType.GUAVA_CACHE) || invokeType.equals(InvokeType.CAFFEINE_CACHE)
                || invokeType.equals(InvokeType.EH_CACHE) || invokeType.equals(InvokeType.REDIS);
    }

    public static InvokeType getInvokeTypeByUri(String uri) {
        String invokeType = "";
        if (StringUtils.isNotBlank(uri) && uri.contains("://")) {
            invokeType = uri.substring(0, uri.indexOf("://"));
        }
        return InvokeType.getByName(invokeType);
    }

    public static InvokeType getByName(String name) {
        for (InvokeType invokeType : InvokeType.values()) {
            if (invokeType.name().equalsIgnoreCase(name)) {
                return invokeType;
            }
        }
        return InvokeType.UNKNOWN;
    }

    /**
     * shuffle插件要认请求参数，这个方法没有返回值。但是在请求的时候不能及时序列化这个结果
     *
     * @return
     */
    public boolean isInTimeExcludeResponse() {
        return this.equals(InvokeType.JAVA_SHUFFLE_PLUGIN);
    }
}