/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repater.plugin.http;

import java.util.HashSet;
import java.util.Set;

/**
 * RequestProcessDto
 */
public class RequestProcessDto {

    private int processId;

    private Set<Integer> skipInvokeSet = new HashSet<>();

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public boolean shouldSkip(int invokeId) {
        return this.skipInvokeSet.contains(invokeId);
    }

    public void addSkipInvoke(int invokeId) {
        this.skipInvokeSet.add(invokeId);
    }
}
