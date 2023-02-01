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
import java.util.Set;

import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.service.data.model.record.RecordCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordDataListQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;

/**
 * {@link RecordDataService} api
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 10:40
 */
public interface RecordDataService {

    /**
     * get record uri count detail this function is provided for display to the
     * console web view
     *
     * @param query
     *            request {@link  RecordUriCountQuery }
     * @return {@link PageResult <RecordUriCountResult>}
     */
    List<RecordUriCountResult> getRecordUriCountList(RecordUriCountQuery query);

    /**
     * batch get task record count this function is provided for display to the
     * console web view
     *
     * @param recordTaskRunIds
     *            record recordTaskRunId set collection
     * @return {@link List <RecordCountResult>} record count list
     */
    List<RecordCountResult> batchGetRecordCountByRunIds(Set<String> recordTaskRunIds);

    /**
     * get record data list by recordTaskRunId this function is provided for display to
     * the console web view
     *
     * @param pageRequest
     *            {@link PageRequest< RecordDataListQuery > query param
     * @return {@link List <RecordCountResult>} record count list
     */
    PageResult<RecordWrapperEntity> getRecordDataList(PageRequest<RecordDataListQuery> pageRequest);

    /**
     * get record data detail this function is provided for display to the console
     * web view
     *
     * @param recordTaskRunId
     *            record recordTaskRunId
     * @param recordTraceId
     *            record traceId
     * @return {@link RecordWrapper} record count list
     */
    RecordWrapperEntity getRecordDataDetail(String recordTaskRunId, String recordTraceId);

    /**
     * delete record data this function is provided for display to the console web
     * view
     *
     * @param recordTaskRunId
     *            record recordTaskRunId
     * @param recordTraceId
     *            record traceId
     * @return {@link RecordWrapper} record count list
     */
    Boolean deleteData(String recordTaskRunId, String recordTraceId);

    /**
     * save record data
     *
     * @param entity
     *            entity
     * @return {@link boolean}
     */
    boolean saveData(RecordWrapperEntity entity);

    /**
     * 查询录制的数据
     *
     * @param recordTaskRunId recordTaskRunId
     * @param traceIds traceIds
     * @return {@link List< String>}
     */
    List<String> queryWrapperData(String recordTaskRunId, List<String> traceIds);

    /**
     * 根据录制任务id查询录制数据(WrapperData)
     *
     * @param recordTaskRunId
     *            recordTaskRunId
     * @param lastId
     *            lastId
     * @return {@link PageResult< String>} 返回失败的录制traceId
     */
    PageResult<String> queryRecordWrapperData(String recordTaskRunId, String lastId);
}
