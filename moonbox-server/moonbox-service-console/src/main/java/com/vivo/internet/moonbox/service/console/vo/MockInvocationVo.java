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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * MockInvocationVo - {@link MockInvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/5 17:36
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvocationVo {

    private String uri;
    private int index;
    private String traceId;
    private String type;
    private Integer replayStatus;
    private String replayStatusErrorCode;
    private String replayStatusErrorMessage;
    private long cost;
    private String currentUri;
    private Object[] currentArgs;
    /**
     * record sub invocation
     */
    private InvocationVo originData;

    /**
     * diff list
     */
    private List<ReplayDiffVo> diffs;

    /**
     * stack trace
     */
    private String stackTraces;
}
