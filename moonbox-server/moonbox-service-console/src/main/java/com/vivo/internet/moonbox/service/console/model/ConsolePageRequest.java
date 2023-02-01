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
package com.vivo.internet.moonbox.service.console.model;

import lombok.Data;

/**
 * ConsolePageRequest - {@link ConsolePageRequest}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/20 15:37
 */
@Data
public class ConsolePageRequest {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 查询参数
     */
    private String  appName;

    /**
     * 查询参数
     */
    private String   condition;


    private String   templateId;

    /**
     * 回放对应的录制任务id标识
     */
    private String   replayRecordTaskRunId;
}
