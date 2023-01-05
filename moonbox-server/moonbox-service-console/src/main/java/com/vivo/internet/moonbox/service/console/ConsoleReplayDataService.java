package com.vivo.internet.moonbox.service.console;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
        @NotNull
        private Integer pageNum;

        /**
         * 每页大小
         */
        @NotNull
        private Integer pageSize;

        /**
         * 录制任务id
         */
        @NotBlank
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
        @NotNull
        private Integer pageNum;

        /**
         * 每页大小
         */
        @NotNull
        private Integer pageSize;
        /**
         * 回放任务id
         */
        @NotBlank
        private String  replayTaskRunId;

        /**
         * 回放接口
         */
        @NotBlank
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
