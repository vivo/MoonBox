/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * {@link RecordModel} 描述一次完整的请求记录信息
 * <p>
 * 一次调用包含一次流量入口调用{@link Invocation}和若干次的子调用{@link Invocation}，通过{@link InvokeType}区分调用类型
 *
 * 录制回放通过{@link RecordModel#entranceInvocation}作为入口流量通过{@link com.alibaba.jvm.sandbox.repeater.plugin.api.FlowDispatcher}发起回放
 *
 * 再通过{@link RecordModel#subInvocations}Mock还原当时的各个子调用信息，即可完成一次完整的Mock流量回放
 *
 * 对于一些幂等的写接口或者纯读接口，也可以只录制{@link RecordModel#entranceInvocation}，直接完成非Mock的流量录制回放
 *
 * </p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 * @since 1.0.0
 */
@Data
public class RecordModel implements java.io.Serializable {

    private long timestamp;

    private String taskRunId;

    private String appName;

    private String environment;

    private String host;

    private String traceId;

    private Invocation entranceInvocation;

    private List<Invocation> subInvocations;

    @JSONField(serialize = false)
    private transient List<Invocation> removeSubInvocations = new ArrayList<>();
}