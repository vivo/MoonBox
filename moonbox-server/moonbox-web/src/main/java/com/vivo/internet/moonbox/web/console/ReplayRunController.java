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
import com.vivo.internet.moonbox.service.console.TaskRunService;
import com.vivo.internet.moonbox.service.console.impl.ReplayTaskRunServiceImpl;
import com.vivo.internet.moonbox.service.console.model.ReplayRunReq;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.ReplayRunDetailVo;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 回放任务接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:02
 */
@RequestMapping("/api/replay")
@RestController
public class ReplayRunController {

    @Resource(name = "replayTaskRunServiceImpl")
    private TaskRunService taskRunService;

    /**
     * 获取回放任务列表
     *
     * @param taskRunPageRequest run list
     * @return {@link MoonBoxResult <ReplayRunVo>} runResult
     */
    @GetMapping(value = "runList")
    public MoonBoxResult<PageResult<TaskRunVo>> runList(TaskRunService.TaskRunPageRequest taskRunPageRequest) {
        taskRunPageRequest.setIsRecord(false);
        PageResult<TaskRunVo> pageResult = taskRunService.taskRunList(taskRunPageRequest);
        return MoonBoxResult.createSuccess(pageResult);
    }

    /**
     * 运行回放任务
     *
     * @param replayRunReq replay run req
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @PostMapping(value = "run")
    public MoonBoxResult<Void> run(@RequestBody ReplayRunReq replayRunReq) {
        replayRunReq.setRunUser(UserUtils.getLoginName());
        return taskRunService.taskRun(replayRunReq);
    }

    /**
     * 重新回放
     *
     * @param taskRunId 重新回放任务id
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @PostMapping(value = "reRun")
    public MoonBoxResult<Void> reRun(@RequestParam("taskRunId") String taskRunId) {
        return taskRunService.reRun(taskRunId, UserUtils.getLoginName(), TaskRunService.ReRunType.NORMAL);
    }


    /**
     * 停止任务
     *
     * @param taskRunId 回放任务id
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @PostMapping(value = "stopRun")
    public MoonBoxResult<Void> stop(@RequestParam("taskRunId") String taskRunId) {
        taskRunService.stop(taskRunId, UserUtils.getLoginName());
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 重试失败数据
     *
     * @param taskRunId 回放任务id
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @RequestMapping(value = "reRunFailedData")
    public MoonBoxResult<Void> reRunFailed(@RequestParam("taskRunId") String taskRunId) {
        taskRunService.reRun(taskRunId, UserUtils.getLoginName(), TaskRunService.ReRunType.REPLAY_FAIL_DATA);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 回放详情
     *
     * @param taskRunId replayTaskRunId
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @GetMapping(value = "detail")
    public MoonBoxResult<ReplayRunDetailVo> detail(@RequestParam("taskRunId") String taskRunId) {
        ReplayRunDetailVo replayRunDetailVo = ((ReplayTaskRunServiceImpl) taskRunService).getReplayDetailVo(taskRunId);
        return MoonBoxResult.createSuccess(replayRunDetailVo);
    }

    /**
     * 删除数据
     *
     * @param taskRunId taskRunId
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @PostMapping(value = "delete")
    public MoonBoxResult<Void> delete(@RequestParam("taskRunId") String taskRunId) {
        taskRunService.deleteByPkId(taskRunId);
        return MoonBoxResult.createSuccess(null);
    }
}
