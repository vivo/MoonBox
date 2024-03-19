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
package com.vivo.internet.moonbox.redis;

import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.redis.dto.UniqModel;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;

import java.util.List;

public interface RecordRedisService {

    /**
     * 判断是否需要保存流量记录
     *
     * @param taskConfigResult 接口配置信息
     * @param esRecordEntity 请求对象实体
     * @return 判断结果，true表示需要保存，false表示不需要保存
     */
    UniqModel judgeNeedSaveRecord(RecordWrapper recordWrapper, RecordAgentConfig taskConfigResult, RecordWrapperEntity esRecordEntity);

    /**
     * 更新唯一字符串到Redis中
     *
     * @param uniqModel 唯一字符串模型
     */
    void updateUniqueStringToRedis(UniqModel uniqModel);

    /**
     * 根据返回值判断是否需要保存流量记录
     *
     * @param recordWrapper 流量记录包装器
     * @param taskConfig 接口配置
     * @param esRecordEntity 请求对象
     * @return 是否需要保存流量记录的唯一模型
     */
    String judgeSave(RecordWrapper recordWrapper, RecordAgentConfig taskConfig, RecordWrapperEntity esRecordEntity);

    /**
     * 将任务执行记录的追踪信息保存到Redis中
     *
     * @param taskRunId 任务执行记录ID
     * @param traceId 追踪信息ID
     */
    void saveRecordTraceToRedis(String taskRunId, String traceId);

    /**
     * 从Redis中获取录制任务的记录轨迹
     *
     * @param replayTaskRunId 回放任务运行ID
     * @param recordTaskRunId 录制任务运行ID
     * @param size 获取的记录轨迹数量
     * @return 记录轨迹列表
     */
    List<String> getRecordTracesFromRedis(String replayTaskRunId, String recordTaskRunId, int size);
}
