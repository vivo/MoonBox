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
package com.vivo.internet.moonbox.service.console.impl;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.util.PageResultUtils;
import com.vivo.internet.moonbox.service.console.ConsoleRecordDataService;
import com.vivo.internet.moonbox.service.console.util.RecordConvert;
import com.vivo.internet.moonbox.service.console.vo.RecordDataVo;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordDataListQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;

/**
 * RecordDataServiceImpl - {@link ConsoleDataServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/31 15:46
 */
@Service
public class ConsoleDataServiceImpl implements ConsoleRecordDataService {

    @Resource
    private RecordDataService recordDataService;

    @Override
    public PageResult<RecordUriCountResult> uriList(RecordUriPageRequest recordUriPageRequest) {
        Assert.notNull(recordUriPageRequest, "uri count query param cannot be null ");
        // 这边使用内存分页，线上如果数据量级较大，可以考虑其他优化方案(如任务定时统计存入数据库)
        RecordUriCountQuery recordUriCountQuery = RecordUriCountQuery.builder()
                .recordTaskRunId(recordUriPageRequest.getTaskRunId())
                .uriMatchCondition(recordUriPageRequest.getUriCondition()).build();
        List<RecordUriCountResult> aggResult = recordDataService.getRecordUriCountList(recordUriCountQuery);
        return PageResultUtils.getCurrentPage(aggResult, recordUriPageRequest.getPageNum(),
                recordUriPageRequest.getPageSize());
    }

    @Override
    public PageResult<RecordDataVo> dataList(RecordDataListRequest dataListRequest) {

        PageRequest<RecordDataListQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageSize(dataListRequest.getPageSize());
        pageRequest.setPageNum(dataListRequest.getPageNum());

        RecordDataListQuery listQuery = RecordDataListQuery.builder().recordUri(dataListRequest.getUri() != null ? URLDecoder.decode(dataListRequest.getUri()) : null)
                .startTime(dataListRequest.getStartTime()).traceId(dataListRequest.getTraceIdCondition())
                .endTime(dataListRequest.getEndTime()).recordTaskRunId(dataListRequest.getTaskRunId()).build();
        pageRequest.setQueryParam(listQuery);

        PageResult<RecordWrapperEntity> pageResult = recordDataService.getRecordDataList(pageRequest);
        List<RecordDataVo> recordDataVoList = pageResult.getResult().stream()
                .map(data -> RecordDataVo.builder().cost(data.getCost()).host(data.getHost())
                        .recordTime(new Date(data.getTimestamp())).traceId(data.getTraceId())
                        .invokeType(InvokeType.getInvokeTypeByUri(data.getEntranceDesc()).getInvokeName())
                        .recordTaskRunId(data.getTaskRunId()).uri(data.getEntranceDesc()).build())
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(),
                recordDataVoList);
    }

    @Override
    public RecordDetailVo dataDetail(String taskRunId, String traceId) {
        RecordWrapperEntity recordWrapper = recordDataService.getRecordDataDetail(taskRunId, traceId);
        return RecordConvert.convertByDataDetail(recordWrapper);
    }

    @Override
    public void deleteDetail(String taskRunId, String traceId) {
        recordDataService.deleteData(taskRunId, traceId);
    }
}
