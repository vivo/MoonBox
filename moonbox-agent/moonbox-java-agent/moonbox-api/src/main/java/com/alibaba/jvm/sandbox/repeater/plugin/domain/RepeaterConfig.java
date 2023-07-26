/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.*;

/**
 * {@link RepeaterConfig} 基础配置项
 * <p>
 * 基础配置从服务端推送到启动的agent或者由agent启动的时候主动去服务端拉取配置；
 * <p>
 * 配置主要包含一些模块的工作模式；插件启动鉴权；采样率等
 * </p>
 *
 * @author zhaoyb1990
 * @since 1.0.0
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class RepeaterConfig implements java.io.Serializable{

    /**
     * 是否开启ttl线程上下文切换
     * <p>
     * 开启之后，才能将并发线程中发生的子调用记录下来，否则无法录制到并发子线程的子调用信息
     * <p>
     * 原理是将住线程的threadLocal拷贝到子线程，执行任务完成后恢复
     *
     */
    private boolean useTtl;

    /**
     * 是否执行录制降级策略
     * <p>
     * 开启之后，不进行录制，只处理回放请求
     */
    private boolean degrade;

    /**
     * 异常发生阈值；默认1000
     * 当{@code ExceptionAware} 感知到异常次数超过阈值后，会降级模块
     */
    private Integer exceptionThreshold = 1000;
    /**
     * 由于HTTP接口的量太大（前后端未分离的情况可能还有静态资源）因此必须走白名单匹配模式才录制
     */
    private List<HttpRecordInterface> httpEntrancePatterns = Lists.newArrayList();

    /**
     * 需要录制入口流量的 dubbo provider
     */
    private List<DubboRecordInterface> dubboEntrancePatterns = Lists.newArrayList();

    /**
     * 需要录制入口流量的 motan provider
     */
    private List<MotanRecordInterface> motanEntrancePatterns = Lists.newArrayList();

    /**
     * 需要录制java方法
     */
    private List<JavaRecordInterface> javaRecordInterfaces = Lists.newArrayList();

    /**
     * java入口插件动态增强的行为
     */
    private List<Behavior> javaEntranceBehaviors = Lists.newArrayList();

    /**
     * java子调用插件动态增强的行为
     */
    private List<Behavior> javaSubInvokeBehaviors = Lists.newArrayList();

    /**
     * 需要启动的插件
     */
    private List<String> pluginIdentities = Lists.newArrayList();

    /**
     * 单接口最多采集的数据总量
     */
    private Long recordCount =3000L;

    /**
     * 统一mock类
     */
    private List<SpecialHandlingUniversalMockClass> universalMockClassList;

    /**
     * 回放器插件
     */
    private List<String> repeatIdentities = Lists.newArrayList();

    /**
     * 回放时，是否需要对子调用进行mock
     */
    private boolean repeaterMock = true;

    /**
     * 子调用匹配查找策略，开启mock回放后生效
     */
    private String strategyType;

    /**
     * 子调用对比忽略配置
     */
    private List<FieldDiffConfig> fieldDiffConfigs;

    public boolean isUseTtl() {
        return useTtl;
    }

    public void setUseTtl(boolean useTtl) {
        this.useTtl = useTtl;
    }

    public boolean isDegrade() {
        return degrade;
    }

    public void setDegrade(boolean degrade) {
        this.degrade = degrade;
    }

    public Integer getExceptionThreshold() {
        return exceptionThreshold;
    }

    public void setExceptionThreshold(Integer exceptionThreshold) {
        this.exceptionThreshold = exceptionThreshold;
    }

    public List<HttpRecordInterface> getHttpEntrancePatterns() {
        return httpEntrancePatterns;
    }

    public void setHttpEntrancePatterns(List<HttpRecordInterface> httpEntrancePatterns) {
        this.httpEntrancePatterns = httpEntrancePatterns;
    }

    public List<Behavior> getJavaEntranceBehaviors() {
        return javaEntranceBehaviors;
    }

    public void setJavaEntranceBehaviors(List<Behavior> javaEntranceBehaviors) {
        this.javaEntranceBehaviors = javaEntranceBehaviors;
    }

    public List<Behavior> getJavaSubInvokeBehaviors() {
        return javaSubInvokeBehaviors;
    }

    public void setJavaSubInvokeBehaviors(List<Behavior> javaSubInvokeBehaviors) {
        this.javaSubInvokeBehaviors = javaSubInvokeBehaviors;
    }

    public List<String> getPluginIdentities() {
        return pluginIdentities;
    }

    public void setPluginIdentities(List<String> pluginIdentities) {
        this.pluginIdentities = pluginIdentities;
    }

    public List<String> getRepeatIdentities() {
        return repeatIdentities;
    }

    public void setRepeatIdentities(List<String> repeatIdentities) {
        this.repeatIdentities = repeatIdentities;
    }

    public List<DubboRecordInterface> getDubboEntrancePatterns() {
        return dubboEntrancePatterns;
    }

    public void setDubboEntrancePatterns(List<DubboRecordInterface> dubboEntrancePatterns) {
        this.dubboEntrancePatterns = dubboEntrancePatterns;
    }

    public boolean isRepeaterMock() {
        return repeaterMock;
    }

    public void setRepeaterMock(boolean repeaterMock) {
        this.repeaterMock = repeaterMock;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public List<SpecialHandlingUniversalMockClass> getUniversalMockClassList() {
        return universalMockClassList;
    }

    public void setUniversalMockClassList(List<SpecialHandlingUniversalMockClass> universalMockClassList) {
        this.universalMockClassList = universalMockClassList;
    }
    public List<JavaRecordInterface> getJavaRecordInterfaces() {
        return javaRecordInterfaces;
    }

    public void setJavaRecordInterfaces(List<JavaRecordInterface> javaRecordInterfaces) {
        this.javaRecordInterfaces = javaRecordInterfaces;
    }

    public List<FieldDiffConfig> getFieldDiffConfigs() {
        return fieldDiffConfigs;
    }

    public void setFieldDiffConfigs(List<FieldDiffConfig> fieldDiffConfigs) {
        this.fieldDiffConfigs = fieldDiffConfigs;
    }

    public List<MotanRecordInterface> getMotanEntrancePatterns() {
        return motanEntrancePatterns;
    }

    public void setMotanEntrancePatterns(List<MotanRecordInterface> motanEntrancePatterns) {
        this.motanEntrancePatterns = motanEntrancePatterns;
    }

    @Override
    public String toString() {
        return "{plugin=" + pluginIdentities + "}";
    }
}