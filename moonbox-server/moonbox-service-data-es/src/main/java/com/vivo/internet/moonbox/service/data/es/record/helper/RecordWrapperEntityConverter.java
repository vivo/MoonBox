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
package com.vivo.internet.moonbox.service.data.es.record.helper;

import com.vivo.internet.moonbox.service.data.es.record.model.EsRecordEntity;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;

/**
 * RecordWrapperEntityConverter - 对象转化器
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/14 15:09
 */
public class RecordWrapperEntityConverter {

    public static RecordWrapperEntity build(EsRecordEntity esRecordEntity) {
        if (null == esRecordEntity) {
            return null;
        }
        RecordWrapperEntity entity = new RecordWrapperEntity();
        entity.setWrapperData(esRecordEntity.getWrapperRecord());
        entity.setTemplateId(esRecordEntity.getRecordTaskTemplateId());
        entity.setTimestamp(esRecordEntity.getDate());
        entity.setCost(esRecordEntity.getCost());
        entity.setAppName(esRecordEntity.getAppName());
        entity.setEnvironment(esRecordEntity.getEnvironment());
        entity.setHost(esRecordEntity.getRecordHost());
        entity.setTraceId(esRecordEntity.getTraceId());
        entity.setEntranceDesc(esRecordEntity.getEntranceDesc());
        entity.setTaskRunId(esRecordEntity.getRecordTaskRunId());
        entity.setResponse(esRecordEntity.getResponse());
        return entity;
    }

}