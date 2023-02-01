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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FieldDiffConfig - {@link FieldDiffConfig}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 17:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDiffConfig {

    /**
     * 字段对比作用范围，{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private Integer scope;

    /**
     * 忽略该字段的uri接口，如果{@link FieldDiffConfig#scope}值为{@link com.vivo.internet.moonbox.common.api.constants.DiffScope#APP_SCOPE}，那么该字段为空
     */
    private String uri;

    /**
     * 需要忽略的具体路径。例如$[0].xxx
     */
    private String fieldPath;
}
