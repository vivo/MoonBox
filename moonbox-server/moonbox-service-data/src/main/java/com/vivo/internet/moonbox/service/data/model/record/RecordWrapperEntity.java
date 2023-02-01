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
package com.vivo.internet.moonbox.service.data.model.record;

import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RecordWrapperEntity - 录制数据保存实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/6 17:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecordWrapperEntity extends RecordWrapper {

    private String wrapperData;

    private String templateId;

    private String response;

}