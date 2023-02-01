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
package com.vivo.internet.moonbox.service.agent.replay.service;

import java.util.List;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.model.RecordPullModel;
import com.vivo.internet.moonbox.common.api.model.RecordPullRequest;

/**
 * ReplayService - 回放相关服务类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 17:40
 */
public interface ReplayService {
    /**
     * 保存流量回放记录
     *
     * @param body
     *            hessian 序列化后的录制实体
     * @return {@link MoonBoxResult < String>}
     */
    MoonBoxResult<String> saveReplay(String body);

    /**
     * pull record data by agent for replay
     * @param recordPullRequest {@link RecordPullRequest}
     * @return record data list {@link List < RecordPullModel >}
     */
    RecordPullModel replayDataPull(RecordPullRequest recordPullRequest);
}