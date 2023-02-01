/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.model;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.Difference;

/**
 * {@link RepeatModel} 回放消息数据类型
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class RepeatModel implements java.io.Serializable {

    private String repeatId;

    /**
     * replay task run id
     */
    private String taskRunId;

    /**
     * record task runId
     */
    private String recordTaskRunId;

    private String recordTraceId;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.ReplayStatus}
     */
    private Integer status;

    private Object response;

    private List<Difference> diffs;

    private Long cost;

    private String traceId;

    private String host;
    /**
     * 回放时间
     */
    private Long replayTime;

    private List<MockInvocation> mockInvocations;

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public String getTaskRunId() {
        return taskRunId;
    }

    public void setTaskRunId(String taskRunId) {
        this.taskRunId = taskRunId;
    }

    public String getRecordTraceId() {
        return recordTraceId;
    }

    public void setRecordTraceId(String recordTraceId) {
        this.recordTraceId = recordTraceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public void setDiffs(List<Difference> diffs) {
        this.diffs = diffs;
    }

    public List<Difference> getDiffs() {
        return diffs;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<MockInvocation> getMockInvocations() {
        return mockInvocations;
    }

    public void setMockInvocations(List<MockInvocation> mockInvocations) {
        this.mockInvocations = mockInvocations;
    }

    public void setRecordTaskRunId(String recordTaskRunId) {
        this.recordTaskRunId = recordTaskRunId;
    }

    public String getRecordTaskRunId() {
        return recordTaskRunId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public Long getReplayTime() {
        return replayTime;
    }

    public void setReplayTime(Long replayTime) {
        this.replayTime = replayTime;
    }

    @Override
    public String toString() {
        return "RepeatModel{" + "repeatId='" + repeatId + '\'' + ", taskRunId='" + taskRunId + '\''
                + ", recordTaskRunId='" + recordTaskRunId + '\'' + ", recordTraceId='" + recordTraceId + '\''
                + ", status=" + status + ", response=" + response + ", diffs=" + diffs + ", cost=" + cost
                + ", traceId='" + traceId + '\'' + ", mockInvocations=" + mockInvocations + '}';
    }
}
