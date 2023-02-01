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
package com.vivo.internet.moonbox.service.data.es.record.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.Tolerate;

/**
 * EsRecordEntity - 流量录制 es实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/31 16:42
 */
@Data
@Builder
@FieldNameConstants
public class EsRecordEntity {

    /**
     * 保存时间
     */
    private Long date;

    /**
     * 月光宝盒录制请求traceId
     */
    private String traceId;

    /**
     * 该请求耗时
     */
    private Long cost;

    /**
     * 录制任务对应的执行id
     */
    private String recordTaskRunId;

    /**
     * 录制模板id，冗余记录
     */
    private String recordTaskTemplateId;

    /**
     * 录制接口标识（dubbo-provider接口名+方法名 或者http接口uri）
     */
    private String entranceDesc;

    /**
     * 流量类型(dubbo或者http)
     */
    private String invokeType;

    /**
     * 被序列化的流量报文体
     */
    private String wrapperRecord;

    /**
     * 返回实体数据
     */
    private String response;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 环境信息(dev/test...)
     */
    private String environment;

    /**
     * 录制机器
     */
    private String recordHost;

    @Tolerate
    public EsRecordEntity() {
    }
}