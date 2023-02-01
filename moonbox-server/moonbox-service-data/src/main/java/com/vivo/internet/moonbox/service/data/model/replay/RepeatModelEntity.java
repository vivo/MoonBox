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
package com.vivo.internet.moonbox.service.data.model.replay;

import com.vivo.internet.moonbox.common.api.model.RepeatModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RepeatModelDto - 流量回放实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/6 11:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RepeatModelEntity extends RepeatModel {
    /**
     * 子调用失败uri
     */
    private String subErrorCurrentUri;

    /**
     * 差异结果
     */
    private String diffResult;

    /**
     * 入口标识
     */
    private String entranceDesc;

    /**
     * 环境信息
     */
    private String environment;

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 流量类型(dubbo或者http)
     */
    private String invokeType;
    /**
     * 录制模板id，冗余记录
     */
    private String recordTaskTemplateId;

    /**
     * 失败个数
     */
    private Long failureNumber;

}