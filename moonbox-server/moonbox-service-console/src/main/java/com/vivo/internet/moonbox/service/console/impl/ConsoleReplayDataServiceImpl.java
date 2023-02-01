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

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.vivo.internet.moonbox.common.api.constants.ReplayStatus;
import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.util.PageResultUtils;
import com.vivo.internet.moonbox.service.console.ConsoleReplayDataService;
import com.vivo.internet.moonbox.service.console.util.RecordConvert;
import com.vivo.internet.moonbox.service.console.util.ReplayConvert;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDataVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDetailVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayDataListQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;
import com.vivo.internet.moonbox.service.data.service.ReplayDataService;

/**
 * ConsoleReplayDataServiceImpl - {@link ConsoleReplayDataServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/5 9:54
 */
@Service
public class ConsoleReplayDataServiceImpl implements ConsoleReplayDataService {

    @Resource
    private ReplayDataService replayDataService;

    @Resource
    private RecordDataService recordDataService;

    @Override
    public PageResult<ReplayUriCountResult> uriList(ReplayUriPageRequest req) {
        Assert.notNull(req, "repeater uri count query param cannot be null " + req);

        ReplayUriCountQuery replayUriCountQuery = ReplayUriCountQuery.builder().replayTaskRunId(req.getReplayTaskRunId()).uriMatchCondition(req.getUriCondition()).replayStatus(req.getStatus())
                .build();

        // 这边使用内存分页，线上如果数据量级较大，可以考虑其他优化方案(如任务定时统计存入数据库)
        List<ReplayUriCountResult> aggResult = replayDataService.getReplayUriCountList(replayUriCountQuery);
        if(req.getStatus()!=null) {
            aggResult =aggResult.stream().filter(replayUriCountResult -> {
                switch (req.getStatus()){
                    case 1:
                        return replayUriCountResult.getSuccessCount().equals(replayUriCountResult.getReplayCount());
                    case 2:
                        return replayUriCountResult.getSuccessCount() >0 && replayUriCountResult.getSuccessCount() < replayUriCountResult.getReplayCount();
                    case 3:
                        return replayUriCountResult.getSuccessCount() ==0;

                    default:
                        return true;
                }
            }).collect(Collectors.toList());
        }

        return PageResultUtils.getCurrentPage(aggResult, req.getPageNum(), req.getPageSize());
    }

    @Override
    public PageResult<ReplayDataVo> dataList(ReplayDataListRequest request) {

        PageRequest<ReplayDataListQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageSize(request.getPageSize());
        pageRequest.setPageNum(request.getPageNum());

        ReplayDataListQuery listQuery = ReplayDataListQuery.builder()
                .errorDistinct(request.getErrorDistinct())
                .replayTaskRunId(request.getReplayTaskRunId())
                .replayUri(request.getReplayUri())
                .traceIdCondition(request.getTraceIdCondition())
                .build();
        pageRequest.setQueryParam(listQuery);


        PageResult<RepeatModelEntity> pageResult = replayDataService.getReplayDataList(pageRequest);
        List<ReplayDataVo> replayDataVoList = pageResult.getResult().stream()
                .map(repeatModel -> ReplayDataVo.builder().hostIp(repeatModel.getHost())
                        .recordTaskRunId(repeatModel.getRecordTaskRunId())
                        .replayStatus(ReplayStatus.getReplayStatus(repeatModel.getStatus()).getCode())
                        .replayCode(ReplayStatus.getReplayStatus(repeatModel.getStatus()).getErrorCode())
                        .replayTraceId(repeatModel.getTraceId()).replayTime(new Date(repeatModel.getReplayTime()))
                        .recordTraceId(repeatModel.getRecordTraceId())
                        .replayMessage(ReplayStatus.getReplayStatus(repeatModel.getStatus()).getErrorMessage())
                        .replayTaskRunId(request.getReplayTaskRunId())
                        .failureNumber(repeatModel.getFailureNumber()).build())
                .collect(Collectors.toList());
        return new PageResult<ReplayDataVo>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(),
                replayDataVoList);
    }

    @Override
    public ReplayDetailVo dataDetail(String replayTaskRunId, String replayTraceId) {
        RepeatModelEntity repeatModel = replayDataService.getReplayDataDetail(replayTaskRunId, replayTraceId);
        RecordWrapperEntity recordWrapper = recordDataService.getRecordDataDetail(repeatModel.getRecordTaskRunId(),
                repeatModel.getRecordTraceId());
        RecordDetailVo recordDetailVo = RecordConvert.convertByDataDetail(recordWrapper);
        return ReplayConvert.convertByRepeatModel(recordDetailVo, repeatModel);
    }

    @Override
    public void deleteDetail(String replayTaskRunId, String traceId) {
        replayDataService.deleteData(replayTaskRunId, traceId);
    }
}
