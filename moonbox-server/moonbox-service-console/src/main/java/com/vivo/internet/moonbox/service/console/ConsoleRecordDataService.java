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
package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.vo.RecordDataVo;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;

import lombok.Data;

/**
 * ConsoleRecordDataService - {@link ConsoleRecordDataService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/31 15:39
 */
public interface ConsoleRecordDataService {

    @Data
    class RecordUriPageRequest {
        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        /**
         * 录制任务id
         */
        private String taskRunId;

        /**
         * uri模糊匹配
         */
        private String uriCondition;

    }

    @Data
    class RecordDataListRequest {

        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        /**
         * 录制任务id
         */
        private String taskRunId;

        /**
         * 录制接口uri
         */
        private String uri;

        /**
         * traceId
         */
        private String traceIdCondition;

        /**
         * 采集时间段-开始时间
         */
        private String startTime;

        /**
         * 采集时间段-结束时间
         */
        private String endTime;

    }

    /**
     * query record uri list
     * 
     * @param pageRequest
     *            page query request
     * @return query result
     */
    PageResult<RecordUriCountResult> uriList(RecordUriPageRequest pageRequest);

    /**
     * query record uri list
     * 
     * @param pageRequest
     *            page query request
     * @return query result
     */
    PageResult<RecordDataVo> dataList(RecordDataListRequest pageRequest);

    /**
     * query record data detail
     * 
     * @param taskRunId
     *            recordTaskRunId
     * @param traceId
     *            traceId
     * @return query result
     */
    RecordDetailVo dataDetail(String taskRunId, String traceId);

    /**
     * delete record data
     * 
     * @param taskRunId
     *            recordTaskRunId
     * @param traceId
     *            traceId
     * @return query result
     */
    void deleteDetail(String taskRunId, String traceId);
}
