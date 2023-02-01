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
import com.vivo.internet.moonbox.service.console.vo.ReplayDataVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDetailVo;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;

import lombok.Data;

/**
 * ConsoleReplayDataService - {@link ConsoleReplayDataService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/31 15:39
 */
public interface ConsoleReplayDataService {


    @Data
    class ReplayUriPageRequest {
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
        private String replayTaskRunId;

        /**
         * uri模糊匹配
         */
        private String uriCondition;

        /**
         * 回放状态去重: 1、全部成功  2、部分成功  3、全部失败
         */
        private Integer status;

    }

    @Data
    class ReplayDataListRequest {

        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;
        /**
         * 回放任务id
         */
        private String  replayTaskRunId;

        /**
         * 回放接口
         */
        private String  replayUri;

        /**
         * traceId
         */
        private String  traceIdCondition;

        /**
         * 按照错误聚合分组
         */
        private Boolean errorDistinct = false;

    }

    /**
     * query replay uri list
     * @param pageRequest page query request
     * @return query result
     */
     PageResult<ReplayUriCountResult> uriList(ReplayUriPageRequest pageRequest);


    /**
     * query replay uri list
     * @param pageRequest page query request
     * @return query result
     */
    PageResult<ReplayDataVo> dataList(ReplayDataListRequest pageRequest);


    /**
     * query replay data detail
     * @param replayTaskRunId replayTaskRunId
     * @param replayTraceId replayTraceId
     * @return query result
     */
    ReplayDetailVo dataDetail(String replayTaskRunId, String replayTraceId);


    /**
     * delete replay data
     * @param replayTaskRunId recordTaskRunId
     * @param traceId traceId
     * @return query result
     */
    void deleteDetail(String replayTaskRunId, String traceId);
}
