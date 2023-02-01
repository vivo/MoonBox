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
package com.vivo.internet.moonbox.web.console;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.ConsoleRecordDataService;
import com.vivo.internet.moonbox.service.console.vo.RecordDataVo;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 录制数据接口
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:02
 */
@RequestMapping("/api/record/data")
@RestController
public class RecordDataController {

    @Resource
    private ConsoleRecordDataService recordDataService;

    /**
     * 分页查询接口列表
     *
     * @param recordUriPageRequest 分页查询请求参数
     * @return 分页查询结果
     */
    @GetMapping(value = "uriList")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<PageResult<RecordUriCountResult>> uriList(ConsoleRecordDataService.RecordUriPageRequest recordUriPageRequest) {
        PageResult<RecordUriCountResult>countResult=recordDataService.uriList(recordUriPageRequest);
        return MoonBoxResult.createSuccess(countResult);
    }

    /**
     * 分页查询接口流量列表
     *
     * @param pageRequest 分页查询请求参数
     * @return 分页查询结果
     */
    @GetMapping(value = "dataList")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<PageResult<RecordDataVo>> dataList( ConsoleRecordDataService.RecordDataListRequest pageRequest) {
        PageResult<RecordDataVo>countResult=recordDataService.dataList(pageRequest);
        return MoonBoxResult.createSuccess(countResult);
    }


    /**
     * 查询录制流量详情
     * @param taskRunId 录制任务id
     * @param traceId traceId
     * @return 流量详情对象
     */
    @GetMapping(value = "dataDetail")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<RecordDetailVo> dataDetail(@RequestParam("taskRunId")String taskRunId, @RequestParam("traceId")String traceId) {
        RecordDetailVo recordDetailVo=recordDataService.dataDetail(taskRunId,traceId);
        return MoonBoxResult.createSuccess(recordDetailVo);
    }

    /**
     * 删除录制数据
     * @param taskRunId 录制任务id
     * @param traceId traceId
     * @return 删除结果
     */
    @PostMapping(value = "deleteData")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<Void> deleteData(@RequestParam("taskRunId")String taskRunId, @RequestParam("traceId")String traceId) {
        recordDataService.deleteDetail(taskRunId,traceId);
        return MoonBoxResult.createSuccess(null);
    }
}
