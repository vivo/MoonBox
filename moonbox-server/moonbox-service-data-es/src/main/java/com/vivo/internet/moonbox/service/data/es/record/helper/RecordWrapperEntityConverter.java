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