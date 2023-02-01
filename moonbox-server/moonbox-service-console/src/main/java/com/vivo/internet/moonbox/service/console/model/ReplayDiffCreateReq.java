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

import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * TemplateCreateReq - {@link ReplayDiffCreateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
public class ReplayDiffCreateReq {

    static {
        ConverterSupported.getInstance().regConverter(
                (ConverterSupported.Converter<ReplayDiffCreateReq, ReplayDiffConfig>) replayDiffCreateReq -> {
                    ReplayDiffConfig replayDiffConfig = new ReplayDiffConfig();
                    replayDiffConfig.setAppName(replayDiffCreateReq.getAppName());
                    replayDiffConfig.setDiffUri(replayDiffCreateReq.getDiffUri());
                    replayDiffConfig.setFieldPath(replayDiffCreateReq.getFieldPath());
                    replayDiffConfig.setCreateUser(replayDiffCreateReq.getCreateUser());
                    replayDiffConfig.setUpdateUser(replayDiffCreateReq.getCreateUser());
                    replayDiffConfig.setDiffScope(replayDiffCreateReq.getDiffScope());

                    return replayDiffConfig;
                }, ReplayDiffCreateReq.class, ReplayDiffConfig.class);
    }

    /**
     * 应用名称(这里暂定设置所有对比路径都挂在某个应用下面，后续可以支持全局忽略路径或者字段
     */
    private String  appName;

    /**
     * 字段路径
     */
    private String  fieldPath;

    /**
     * 忽略字段对应的uri
     */
    private String  diffUri;

    /**
     * 忽略范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private Integer diffScope;

    /**
     * 创建人
     */
    private String createUser;
}
