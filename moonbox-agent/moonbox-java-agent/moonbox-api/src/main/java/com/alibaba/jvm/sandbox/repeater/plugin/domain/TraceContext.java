/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import com.vivo.internet.moonbox.common.api.model.InvokeType;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link TraceContext} 定义一个简单的上下文，用于串联一次完成调用
 *
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class TraceContext {

    /**
     * 唯一标识一次调用
     */
    private String traceId;

    /**
     * 调用发生时间
     */
    private long timestamp;

    /**
     * 被采样到 - 采样规则由入口调用计算，子调用不计算
     */
    private volatile boolean sampled;

    /**
     * 被采样到 - 采样规则由入口调用计算，子调用不计算
     */
    private volatile boolean recordStack =true;

    /**
     * 记录录制流量入口URI
     */
    private String recordEntranceUri;

    /**
     * 调用的类型
     */
    private InvokeType invokeType;

    /**
     * 额外需要透传的信息可以用这个承载
     */
    private Map<String, String> extra = new HashMap<>();

    public TraceContext(String traceId) {
        this.timestamp = System.currentTimeMillis();
        this.traceId = traceId;
    }

    /**
     * 及时计算采样
     * @param invokeType 调用类型
     * @return 是否被采样
     */
    public boolean inTimeSample(InvokeType invokeType) {
        // 第一级入口流量才会计算采样；非自身入口类型直接抛弃
        if (this.invokeType == null || this.invokeType.equals(invokeType)) {
            this.invokeType = invokeType;
            this.sampled = true;
            return sampled;
        } else {
            // 下级入口不采集
            return false;
        }
    }


    public String getTraceId() {
        return traceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getExtra(String key) {
        return extra.get(key);
    }

    public String putExtra(String key, String value) {
        return extra.put(key, value);
    }

    public boolean isSampled() {
        return sampled;
    }

    public void setSampled(boolean sampled) {
        this.sampled = sampled;
    }

    public void setRecordStack(boolean recordStack) {
        this.recordStack = recordStack;
    }

    public boolean isRecordStack() {
        return recordStack;
    }

    public void setRecordEntranceUri(String recordEntranceUri) {
        this.recordEntranceUri = recordEntranceUri;
    }

    public String getRecordEntranceUri() {
        return recordEntranceUri;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
    }

}