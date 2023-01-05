package com.vivo.internet.moonbox.service.console;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
