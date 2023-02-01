/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.model;

import java.util.List;


/**
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class RecordWrapper {

    private long timestamp;

    private long cost;

    private String taskRunId;

    private String appName;

    private String environment;

    private String host;

    private String traceId;

    /**
     * 入口描述
     */
    private String entranceDesc;
    /**
     * 入口调用
     */
    private Invocation entranceInvocation;
    /**
     * 子调用信息
     */
    private List<Invocation> subInvocations;

    public RecordWrapper() {}

    public RecordWrapper(RecordModel recordModel) {
        this.timestamp = recordModel.getTimestamp();
        this.taskRunId = recordModel.getTaskRunId();
        this.appName = recordModel.getAppName();
        this.environment = recordModel.getEnvironment();
        this.host = recordModel.getHost();
        this.traceId = recordModel.getTraceId();
        this.entranceDesc = recordModel.getEntranceInvocation().getIdentity().getUri();
        this.entranceInvocation = recordModel.getEntranceInvocation();
        this.subInvocations = recordModel.getSubInvocations();
    }

    /**
     * 将{@link RecordWrapper} 转换成 {@link RecordModel}
     * 
     * @return Record
     */
    public RecordModel reTransform() {
        RecordModel recordModel = new RecordModel();
        recordModel.setTimestamp(this.timestamp);

        // add by zlqian
        recordModel.setTaskRunId(this.taskRunId);

        recordModel.setTraceId(this.traceId);

        recordModel.setAppName(this.appName);
        recordModel.setEnvironment(this.environment);
        recordModel.setHost(this.host);
        recordModel.setEntranceInvocation(this.entranceInvocation);
        recordModel.setSubInvocations(this.subInvocations);
        return recordModel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getEntranceDesc() {
        return entranceDesc;
    }

    public void setEntranceDesc(String entranceDesc) {
        this.entranceDesc = entranceDesc;
    }

    public Invocation getEntranceInvocation() {
        return entranceInvocation;
    }

    public void setEntranceInvocation(Invocation entranceInvocation) {
        this.entranceInvocation = entranceInvocation;
    }

    public List<Invocation> getSubInvocations() {
        return subInvocations;
    }

    public void setSubInvocations(List<Invocation> subInvocations) {
        this.subInvocations = subInvocations;
    }

    public String getTaskRunId() {
        return taskRunId;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getCost() {
        return cost;
    }

    public void setTaskRunId(String taskRunId) {
        this.taskRunId = taskRunId;
    }
}
