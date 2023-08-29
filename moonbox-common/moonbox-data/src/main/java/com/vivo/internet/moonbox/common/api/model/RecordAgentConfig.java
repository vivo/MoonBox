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
package com.vivo.internet.moonbox.common.api.model;

import com.vivo.internet.moonbox.common.api.constants.LogLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ReplayAgentConfig - {@link RecordAgentConfig} {@link RecordAgentConfig}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
@Data
public class RecordAgentConfig implements Serializable {

    private static final long serialVersionUID = 1477288821472001724L;

    /**
     * dubbo record interfaces
     */
    private List<DubboRecordInterface> dubboRecordInterfaces;

    private List<MotanRecordInterface> motanRecordInterfaces;

    /**
     * http record interfaces
     */
    private List<HttpRecordInterface>  httpRecordInterfaces;

    /**
     * java record interfaces
     */
    private List<JavaRecordInterface>  javaRecordInterfaces;

    /**
     * open invoke plugin list {@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String>               subInvocationPlugins;

    /**
     * record count for per interface
     */
    private Long                       recordCount =500L;

    /**
     * 录制任务执行时长,单位min
     */
    private Integer                    recordTaskDuration = 60;

    /**
     * special mock config
     */
    private Map<Integer, String>       specialHandlingConfig;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel = LogLevel.OFF;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel =LogLevel.OFF;

}
