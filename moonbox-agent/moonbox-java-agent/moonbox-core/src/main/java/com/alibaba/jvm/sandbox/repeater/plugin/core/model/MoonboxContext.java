/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.model;

import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.ExceptionAware;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.InetAddressUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.PropertyUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.google.common.base.Objects;

/**
 * {@link MoonboxContext} 描述一个基础应用模型
 * <p>
 * 应用名    {@link MoonboxContext#appName}
 * 机器名    {@link MoonboxContext#host}
 * 环境信息  {@link MoonboxContext#environment}
 * 模块配置  {@link MoonboxContext#config}
 * </p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class MoonboxContext {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 环境
     */
    private String environment;

    /**
     * host
     */
    private String host;

    /**
     * 任务编码
     */
    private String  taskId;

    /**
     * 任务执行编码
     */
    private String taskRunId;

    /**
     * 模式 0：录制 1：回放
     */
    private Integer repeatMode;

    /**
     * 回放对应的录制任务执行id。如果边录制边回放可以用来获取任务状态
     */
    private String replayForRecordRunId;

    /**
     * 如果是回放记录了回放是否结束  0：未结束 1：结束
     */
    private Integer repeatStatus = 0;

    /**
     * 请求接口是需要的密钥
     */
    private String httpUrl;

    /**
     * 请求接口是否记录子调用堆栈信息
     */
    private Boolean openStackTrace;
    /**
     * sandbox 安装的home目录
     */
    private String sandboxHome;

    /**
     * debug模式
     */
    private boolean isDebug = false;

    private boolean startEnd = false;

    private volatile RepeaterConfig config;

    private ExceptionAware ea = new ExceptionAware();

    private volatile boolean fusing = false;

    private static MoonboxContext instance = new MoonboxContext();

    private MoonboxContext() {
        // for example, you can define it your self
        this.appName = PropertyUtil.getSystemPropertyOrDefault("app.name", "unknown");
        this.environment = PropertyUtil.getSystemPropertyOrDefault("app.env", "unknown");
        this.host = InetAddressUtils.getLocalIp();
    }

    public static MoonboxContext getInstance() {
        return instance;
    }

    public Boolean getOpenStackTrace() {
        return openStackTrace;
    }

    public void setOpenStackTrace(Boolean openStackTrace) {
        this.openStackTrace = openStackTrace;
    }

    /**
     * 是否正在工作（熔断机制）
     *
     * @return true/false
     */
    public boolean isWorkingOn() {
        return !fusing;
    }

    /**
     * 是否降级（系统行为）
     *
     * @return true/false
     */
    public boolean isDegrade() {
        return config == null || config.isDegrade();
    }

    /**
     * 异常阈值检测
     *
     * @param throwable 异常类型
     */
    public void exceptionOverflow(Throwable throwable) {
        if (ea.exceptionOverflow(throwable, config == null ? 1000 : config.getExceptionThreshold())) {
            fusing = true;
            ea.printErrorLog();
        }
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

    public RepeaterConfig getConfig() {
        return config;
    }

    public void setConfig(RepeaterConfig config) {
        this.config = config;
    }

    public ExceptionAware getEa() {
        return ea;
    }

    public void setEa(ExceptionAware ea) {
        this.ea = ea;
    }

    public boolean isFusing() {
        return fusing;
    }

    public void setFusing(boolean fusing) {
        this.fusing = fusing;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskRunId() {
        return taskRunId;
    }

    public void setTaskRunId(String taskRunId) {
        this.taskRunId = taskRunId;
    }

    public Integer getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(Integer repeatMode) {
        this.repeatMode = repeatMode;
    }

    public boolean isRecordMode(){
        return repeatMode == 0;
    }

    public boolean isRepeatMode(){
        return Objects.equal(this.repeatMode, 1) ||Objects.equal(this.repeatMode, 2)  ;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public String getSandboxHome() {
        return sandboxHome;
    }

    public void setSandboxHome(String sandboxHome) {
        this.sandboxHome = sandboxHome;
    }

    public void setRepeatStatus(Integer repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    public Integer getRepeatStatus() {
        return repeatStatus;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public void setStartEnd(boolean startEnd) {
        this.startEnd = startEnd;
    }

    public boolean isStartEnd() {
        return startEnd;
    }

    public void setReplayForRecordRunId(String replayForRecordRunId) {
        this.replayForRecordRunId = replayForRecordRunId;
    }

    public String getReplayForRecordRunId() {
        return replayForRecordRunId;
    }

    public static void setInstance(MoonboxContext instance) {
        MoonboxContext.instance = instance;
    }
}
