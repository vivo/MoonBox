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
package com.vivo.internet.moonbox.service.agent.config.service;

import java.util.List;

import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;

/**
 * ReplayCompareConfigService - 回放对比配置服务
 *
 * @author 11105083
 * @version 1.0
 * @since 2021/2/25 19:19
 */
public interface ReplayCompareConfigService {

    /**
     * 根据系统名称，获取子调用对比配置信息
     *
     * @param appName
     *            appName
     * @return {@link List< FieldDiffConfig>}
     */
    List<FieldDiffConfig> querySubReplayConfigs(String appName);

    /**
     * 获取入口处，回放对比配置信息
     *
     * @param appName
     *            appName
     * @param entranceUri
     *            entranceUri
     * @return {@link List< FieldDiffConfig>}
     */
    List<FieldDiffConfig> queryEntranceUriConfigs(String appName, String entranceUri);

    /**
     * 查找全局特殊mock配置
     *
     * @param appName
     *            appName
     * @return {@link List< SpecialMockConfig>}
     */
    List<SpecialMockConfig> querySpecialMockConfigs(String appName);
}
