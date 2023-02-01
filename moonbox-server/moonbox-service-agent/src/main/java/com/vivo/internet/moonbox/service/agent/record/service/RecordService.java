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
package com.vivo.internet.moonbox.service.agent.record.service;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;

/**
 * RecordService - 流量录制服务
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 15:48
 */
public interface RecordService {

    /**
     * 保存流量录制记录
     *
     * @param body
     *            hessian 序列化后的录制实体
     * @return {@link MoonBoxResult< String>}
     */
    MoonBoxResult<String> saveRecord(String body);
}