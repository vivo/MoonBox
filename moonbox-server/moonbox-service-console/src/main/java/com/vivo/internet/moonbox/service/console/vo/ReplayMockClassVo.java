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

import lombok.Data;

/**
 * ReplayMockClassVo - {@link ReplayMockClassVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 15:42
 */
@Data
public class ReplayMockClassVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * mock类型描述
     */
    private String mockTypeMsg;

    /**
     * 描述
     */
    private Integer mockType;

    /**
     * 内容详情
     */
    private String contentJson;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
