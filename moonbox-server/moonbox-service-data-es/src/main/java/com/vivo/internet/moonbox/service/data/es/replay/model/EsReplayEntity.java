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
package com.vivo.internet.moonbox.service.data.es.replay.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.Tolerate;

/**
 * EsReplayEntity - 流量回放 es实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/31 17:12
 */
@Data
@Builder
@FieldNameConstants
public class EsReplayEntity {

    /**
     * 回放执行时间
     */
    private Long replayDate;

    /**
     * 回放traceId标识
     */
    private String replayTraceId;

    /**
     * 回放耗时
     */
    private Long cost;

    /**
     * 回放任务执行id
     */
    private String replayTaskRunId;

    /**
     * 子调用mock过程
     */
    private String mockInvocation;

    /**
     * 录制任务对应的执行id
     */
    private String recordTaskRunId;

    /**
     * 录制模板id，冗余记录
     */
    private String recordTaskTemplateId;

    /**
     * 回放接口标识（dubbo-provider接口名+方法名 或者http接口uri）
     */
    private String entranceDesc;

    /**
     * 流量类型(dubbo或者http)
     */
    private String invokeType;
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 回放对应的录制流量trace_id
     */
    private String recordTraceId;
    /**
     * 环境信息(dev/test...)
     */
    private String environment;

    /**
     * 对比失败路径uri
     */
    private String compareErrorUri;

    /**
     * 回放结果
     */
    private String replayResponse;

    /**
     * 回放结果对比差异结果
     */
    private String responseDiffResult;

    /**
     * 回放机器
     */
    private String replayHost;

    /**
     * 回放结果
     */
    private Integer replayResultStatus;

    @Tolerate
    public EsReplayEntity() {
    }
}