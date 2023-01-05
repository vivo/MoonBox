package com.alibaba.jvm.sandbox.repeater.plugin.common;

/**
 * Constants
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 3:58 下午
 */
public final class Constants {

    /**
     * agent <==> module通信传输字段
     */
    public static final String DATA_TRANSPORT_IDENTIFY = "_data";

    /**
     * 默认配置拉取地址
     */
    public static final String DEFAULT_CONFIG_DATASOURCE = "repeat.config.url";

    /**
     * 是否开启单机工作模式
     */
    public static final String REPEAT_STANDALONE_MODE = "repeat.standalone.mode";

    /**
     * AGENT路径前缀
     */
    public static final String MOONBOX_AGENT_PREFIX = "/api/agent/%s";

    /**
     * AGENT配置拉取的url path地址
     */
    public static final String CONFIG_URL_PATH = String.format(MOONBOX_AGENT_PREFIX, "getConfig");

    /**
     * 存储流量的url path地址
     */
    public static final String RECORD_URL_PATH = String.format(MOONBOX_AGENT_PREFIX, "record/save");

    /**
     * 回放后存储的url path地址
     */
    public static final String REPEAT_RECORD_URL_PATH = String.format(MOONBOX_AGENT_PREFIX, "replay/save");

    /**
     * 拉取需要回放流量url path地址 pull record data
     */
    public static final String RECORD_PULL_URL_PATH = String.format(MOONBOX_AGENT_PREFIX, "record/pull");

    /**
     * report heartbeat url
     */
    public static final String HEART_BEAT_URL_PATH = String.format(MOONBOX_AGENT_PREFIX, "heartbeat");

    /**
     * 透传给下游的traceId；需要利用traceId串联回放流程
     */
    public static final String HEADER_TRACE_ID = "Repeat-TraceId";

    /**
     * 透传给下游的traceId；跟{@code HEADER_TRACE_ID}的差异在于，{@code HEADER_TRACE_ID_X}表示一次回放请求；需要进行Mock
     */
    public static final String HEADER_TRACE_ID_X = "Repeat-TraceId-X";

    /**
     * 最大uri数
     */
    public static final Integer MAX_RECORD_URI = 4000;

    /**
     * 插件自有类正则
     */
    public static final String[] PLUGIN_CLASS_PATTERN = new String[]{
            "^com.alibaba.jvm.sandbox.repeater.plugin.core..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.api..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.spi..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.domain..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.exception..*",
            "^org.slf4j..*",
            "^ch.qos.logback..*",
            "^org.apache.commons..*"
    };

}