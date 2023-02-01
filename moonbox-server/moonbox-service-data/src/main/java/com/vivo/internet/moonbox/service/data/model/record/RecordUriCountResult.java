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
package com.vivo.internet.moonbox.service.data.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReplayUriCountResult - {@link RecordUriCountResult}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:33
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecordUriCountResult implements Serializable {

    private static final long serialVersionUID = 6336524619247991040L;

    /**
     * 录制任务id
     */
    private String  recordTaskRunId;

    /**
     * 接口uri/java方法
     */
    private String  recordUri;

    /**
     * 接口类型
     */
    private String  invokeType;

    /**
     * 数据总量
     */
    private Long    recordCount;
}
