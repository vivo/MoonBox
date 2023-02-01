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
 * Heartbeat - {@link Heartbeat}
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/10/16 10:31
 */
@Data
public class Heartbeat {
    /**
     * agent taskRunId
     */
    private String taskRunId;
    /**
     * agent host ip
     */
    private String ip;
}
