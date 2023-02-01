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
package com.vivo.internet.moonbox.service.data.service;

import java.util.List;

import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayDataListQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;

/**
 * ReplayDataService - {link ReplayDataService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:19
 */
public interface ReplayDataService {

    /**
     * get replay uri count detail this function is provided for display to the
     * console web view
     *
     * @param query
     *            request {@link PageRequest<ReplayUriCountQuery>}
     * @return {@link PageResult <ReplayUriCountResult>}
     */
    List<ReplayUriCountResult> getReplayUriCountList(ReplayUriCountQuery query);

    /**
     * get replay uri data list this function is provided for display to the console
     * web view
     *
     * @param pageRequest
     *            request {@link PageRequest< ReplayDataListQuery >}
     * @return {@link PageResult <ReplayDataResult>}
     */
    PageResult<RepeatModelEntity> getReplayDataList(PageRequest<ReplayDataListQuery> pageRequest);

    /**
     * get replay uri data detail info this function is provided for display to the
     * console web view
     *
     * @param replayTaskRunId
     *            replayTaskRunId
     * @param replayTraceId
     *            replayTraceId
     * @return {@link RepeatModel}
     */
    RepeatModelEntity getReplayDataDetail(String replayTaskRunId, String replayTraceId);

    /**
     * get replay fail count web view
     *
     * @param replayTaskRunId
     *            replayTaskRunId
     * @return failCount
     */
    Long getReplayFailCount(String replayTaskRunId);

    /**
     * delete replay data detail this function is provided for display to the
     * console web view
     *
     * @param replayTaskRunId
     *            replayTaskRunId
     * @param replayTraceId
     *            replayTraceId
     * @return {@link RepeatModel}
     */
    Boolean deleteData(String replayTaskRunId, String replayTraceId);

    /**
     * 保存流量回复数据
     *
     * @param entity
     *            entity
     * @return {@link boolean}
     */
    boolean saveReplayData(RepeatModelEntity entity);

    /**
     * 根据回放任务id查询回放任务
     *
     * @param replayTaskRunId
     *            replayTaskRunId
     * @param lastId
     *            lastId
     * @return {@link PageResult< String>} 返回失败的录制traceId
     */
    PageResult<String> queryFailedRepeaters(String replayTaskRunId, String lastId);
}
