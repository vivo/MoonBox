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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReplayUriCountResult - {@link ReplayUriCountResult}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:33
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplayUriCountResult implements Serializable {

    private static final long serialVersionUID = 6336524619247991040L;

    /**
     * 回放执行编码
     */
    private String  replayTaskRunId;

    /**
     * 回放接口
     */
    private String  replayUri;

    /**
     * 接口类型
     */
    private String  invokeType;

    /**
     * 接口数量
     */
    private Long    replayCount;

    /**
     * 成功数量
     */
    private Long    successCount;

}
