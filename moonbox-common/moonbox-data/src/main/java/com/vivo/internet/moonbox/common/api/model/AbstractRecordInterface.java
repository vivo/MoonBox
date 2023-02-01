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

import java.io.Serializable;

import lombok.Data;

/**
 * AbstractRecordInterface - {@link AbstractRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:39
 */
@Data
public abstract class AbstractRecordInterface implements Serializable {

    private String desc;

    private String sampleRate = "10000";

    /**
     * 获取接口唯一配置
     * 
     * @return 唯一键
     */
    public abstract String getUniqueKey();
}
