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

import javax.annotation.Resource;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.ConsoleReplayDataService;
import com.vivo.internet.moonbox.service.console.vo.ReplayDataVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDetailVo;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;

/**
 * 回放数据接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:02
 */
@RequestMapping("/api/replay/data")
@RestController
public class ReplayDataController {

    @Resource
    private ConsoleReplayDataService consoleReplayDataService;

    /**
     * 分页查询回放接口列表
     *
     * @param pageRequest 分页查询参数
     * @return 分页查询结果
     */
    @GetMapping(value = "uriList")
    public MoonBoxResult<PageResult<ReplayUriCountResult>> uriList(ConsoleReplayDataService.ReplayUriPageRequest pageRequest) {
        PageResult<ReplayUriCountResult> pageResult = consoleReplayDataService.uriList(pageRequest);
        return MoonBoxResult.createSuccess(pageResult);
    }

    /**
     * 分页查询回放接口数据列表
     *
     * @param pageRequest 分页查询请求参数
     * @return 分页查询结果
     */
    @GetMapping(value = "dataList")
    public MoonBoxResult<PageResult<ReplayDataVo>> dataList(ConsoleReplayDataService.ReplayDataListRequest pageRequest) {
        PageResult<ReplayDataVo> result = consoleReplayDataService.dataList(pageRequest);
        return MoonBoxResult.createSuccess(result);
    }

    /**
     * 查询回放数据详情
     * 
     * @param replayTaskRunId
     *            回放任务id
     * @param traceId
     *            traceId
     * @return {@link MoonBoxResult<PageResult<ReplayDetailVo>} runResult
     */
    @GetMapping(value = "dataDetail")
    public MoonBoxResult<ReplayDetailVo> dataDetail(@RequestParam("replayTaskRunId") String replayTaskRunId,
            @RequestParam("traceId") String traceId) {
        ReplayDetailVo replayDetailVo =consoleReplayDataService.dataDetail(replayTaskRunId,traceId);
        return MoonBoxResult.createSuccess(replayDetailVo);
    }

    /**
     * 删除回放数据
     * 
     * @param replayTaskRunId
     *            回放任务id
     * @param traceId
     *            traceId
     * @return 删除结果
     */
    @DeleteMapping(value = "deleteData")
    public MoonBoxResult<Void> deleteData(@RequestParam("replayTaskRunId") String replayTaskRunId,
            @RequestParam("traceId") String traceId) {
        consoleReplayDataService.deleteDetail(replayTaskRunId,traceId);
        return MoonBoxResult.createSuccess(null);
    }
}
