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
package com.vivo.internet.moonbox.service.console.vo;

import java.util.List;

import com.vivo.internet.moonbox.common.api.model.Machine;

import lombok.Builder;
import lombok.Data;


/**
 * TemplateCreateReq - {@link ReplayRunDetailVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
@Builder
public class ReplayRunDetailVo {
    /**
     * runEnv{@link com.vivo.internet.moonbox.common.api.constants.EnvEnum}
     */
    private String runEnv;

    /**
     * runIp
     */
    private Machine hosts;

    /**
     * runDesc
     */
    private String runDesc;


    /**
     * recordTaskRunId
     */
    private String recordTaskRunId;

    /**
     * DataSelect{@link com.vivo.internet.moonbox.common.api.constants.DataSelect}
     */
    private Integer selectType;

    /**
     * select  traceIds
     * <p>
     *     if {@link ReplayRunDetailVo#selectType} value  is PART_DATA,this field must not be null
     * </p>
     *
     */
    private List<String> traceIds;

    /**
     * invokeSubPlugin list {@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String> subInvocationPlugins;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel;
}