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

import com.vivo.internet.moonbox.common.api.model.Machine;
import lombok.Data;

/**
 * TaskRunReq - {@link TaskRunReq}
 *
 * @author 11119783
 * @version 1.0
 * @since 2020/12/28 14:05
 */
@Data
public class TaskRunReq {

    /**
     * 运行环境
     */
    private String runEnv;

    /**
     * 运行人
     */
    private String runUser;


    /**
     * 运行描述
     */
    private String runDesc;

    /**
     * 执行机器，目前支持单个机器，可以考虑多个
     */
    private Machine runHosts;
}