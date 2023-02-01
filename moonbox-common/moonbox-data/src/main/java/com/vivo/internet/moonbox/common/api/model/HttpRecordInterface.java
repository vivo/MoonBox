
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

import lombok.Data;

/**
 * HttpRecordInterface - {@link HttpRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:39
 */
@Data
public class HttpRecordInterface extends AbstractRecordInterface {
    /**
     * http请求的uri 支持path variable部分，如 /api/thread/{},{}代表变量
     */
    private String uri;

    /**
     * path variable标志
     */
    public static final String VARIABLE_PATH = "{}";

    @Override
    public String getUniqueKey() {
        return "http" + "_" + uri;
    }
}
