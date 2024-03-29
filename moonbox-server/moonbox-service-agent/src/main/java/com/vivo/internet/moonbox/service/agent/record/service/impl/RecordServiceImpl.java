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
package com.vivo.internet.moonbox.service.agent.record.service.impl;

import com.vivo.internet.moonbox.common.api.serialize.Serializer;
import com.vivo.internet.moonbox.common.api.serialize.SerializerProvider;
import com.vivo.internet.moonbox.redis.RecordRedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.alibaba.jvm.sandbox.repeater.plugin.util.JacksonUtils;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.common.api.serialize.SerializeException;
import com.vivo.internet.moonbox.common.api.util.SerializerWrapper;
import com.vivo.internet.moonbox.service.agent.config.service.TaskConfigService;
import com.vivo.internet.moonbox.service.agent.record.service.RecordService;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * RecordServiceImpl - 流量录制服务
 */
@Service
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final TaskConfigService taskConfigService;

    private final RecordDataService recordDataService;

    @Autowired(required = false)
    private RecordRedisService recordRedisService;

    @Autowired
    public RecordServiceImpl(TaskConfigService taskConfigService, RecordDataService recordDataService) {
        this.taskConfigService = taskConfigService;
        this.recordDataService = recordDataService;
    }

    @Override
    public MoonBoxResult<String> saveRecord(String body) {

        Assert.notNull(body, "save record body cannot be null");
        // 反序列化JSON内容
        RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(body, RecordWrapper.class);
        if (wrapper == null || StringUtils.isEmpty(wrapper.getAppName())
                || StringUtils.isBlank(wrapper.getTaskRunId())) {
            return MoonBoxResult.createFailResponse("invalid request");
        }

        AgentConfig agentConfig = taskConfigService.getTaskConfigCache(wrapper.getTaskRunId());
        Assert.notNull(agentConfig, "agent config query error!recordTaskRunId=" + wrapper.getTaskRunId());

        RecordWrapperEntity entity = new RecordWrapperEntity();
        BeanUtils.copyProperties(wrapper, entity);
        entity.setWrapperData(body);
        entity.setTemplateId(agentConfig.getRecordTemplateId());
        try {
            Object response = SerializerWrapper.hessianDeserialize(wrapper.getEntranceInvocation().getResponseSerialized(), Object.class);
            if (response instanceof String) {
                entity.setResponse((String) response);
            } else {
                entity.setResponse(JSON.toJSONString(response));
            }
        } catch (SerializeException e){
            log.error("deserialize response body failed, response:{}.",wrapper.getEntranceInvocation().getResponseSerialized(), e);
        }

        Object[] objects = SerializerProvider.instance().provide(Serializer.Type.HESSIAN).deserialize(wrapper.getEntranceInvocation()
                .getRequestSerialized(), Object[].class);
        try{
            entity.setRequest(JacksonUtils.serialize(objects));
        } catch (Exception e) {
            log.error("deserialize request body failed, body:{}.",wrapper.getEntranceInvocation().getRequestSerialized(), e);
        }

        //请求重复校验(不同类型、不同接口的核心字段配置请在web后台任务创建时自行选择)
        String errMsg = recordRedisService == null ? "" :recordRedisService.judgeSave(wrapper, agentConfig.getRecordAgentConfig(), entity);
        if (StringUtils.isNotEmpty(errMsg)) {
            return MoonBoxResult.createFailResponse(errMsg);
        }

        boolean isSuccess = recordDataService.saveData(entity);
        return isSuccess ? MoonBoxResult.createSuccess("-/-") : MoonBoxResult.createFailResponse("failed");
    }
}