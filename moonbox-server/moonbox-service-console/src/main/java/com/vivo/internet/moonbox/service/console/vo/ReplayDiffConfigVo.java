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
package com.vivo.internet.moonbox.service.console.vo;

import java.util.Date;

import com.vivo.internet.moonbox.common.api.constants.DiffScope;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * ReplayDiffConfigVo - {@link ReplayDiffConfigVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 15:28
 */
@Data
public class ReplayDiffConfigVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 忽略路径
     */
    private String fieldPath;

    /**
     * 作用的uri
     */
    private String diffUri;

    /**
     * 作用范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private Integer diffScope;

    /**
     * 作用范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private String diffScopeMsg;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
