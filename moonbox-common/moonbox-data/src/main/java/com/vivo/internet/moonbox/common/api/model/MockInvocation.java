/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.model;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.Difference;

/**
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class MockInvocation implements java.io.Serializable {
    private int index;
    /**
     * 记录原来用哪个数据做对比的
     */
    private int originDataIndex;
    private String traceId;
    private String repeatId;
    private boolean success;
    private boolean skip;
    private long cost;
    private String originUri;
    private String currentUri;
    private Object[] originArgs;
    private Object[] currentArgs;
    private List<Difference>  diffs;
    private boolean warning;

    /**
     * 调用栈信息    这里是截取过的 不包含spy开始的栈信息
     *  同时控制数量
     */
    private List<StackTraceElement> stackTraceElements;

    public List<StackTraceElement> getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(List<StackTraceElement> stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public List<Difference> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<Difference> diffs) {
        this.diffs = diffs;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setOriginDataIndex(int originDataIndex) {
        this.originDataIndex = originDataIndex;
    }

    public int getOriginDataIndex() {
        return originDataIndex;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getOriginUri() {
        return originUri;
    }

    public void setOriginUri(String originUri) {
        this.originUri = originUri;
    }

    public String getCurrentUri() {
        return currentUri;
    }

    public void setCurrentUri(String currentUri) {
        this.currentUri = currentUri;
    }

    public Object[] getOriginArgs() {
        return originArgs;
    }

    public void setOriginArgs(Object[] originArgs) {
        this.originArgs = originArgs;
    }

    public Object[] getCurrentArgs() {
        return currentArgs;
    }

    public void setCurrentArgs(Object[] currentArgs) {
        this.currentArgs = currentArgs;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }
}
