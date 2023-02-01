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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AgentDetailVo - {@link AgentDetailVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/1 20:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDetailVo {

    private String fileName;

    private String digestHex;

    private String desc;

    private String content;

    private String updateUser;

    private Date   createTime;

    private Date   updateTime;

    private Boolean fileUploadFlag =Boolean.FALSE;
}
