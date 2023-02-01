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
import com.vivo.internet.moonbox.service.console.impl.RecordTaskRunServiceImpl;
import com.vivo.internet.moonbox.service.console.model.RecordRunReq;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.RecordRunDetailVo;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 录制任务接口
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:02
 */
@RequestMapping("/api/record")
@RestController
public class RecordRunController {

    @Resource(name = "recordTaskRunServiceImpl")
    private RecordTaskRunServiceImpl recordRunService;

    /**
     * 录制任务执行
     *
     * @param recordRunReq runRecordTemplate
     * @return {@link MoonBoxResult<Void>} runResult
     */
    @PostMapping(value = "run")
    public MoonBoxResult<Void> run(@RequestBody RecordRunReq recordRunReq) {
        recordRunReq.setRunUser(UserUtils.getLoginName());
        return  recordRunService.taskRun(recordRunReq);
    }
    /**
     * 重新执行
     *
     * @param taskRunId 录制任务id
     * @return {@link MoonBoxResult<Void>} runResult
     */
    @PostMapping(value = "reRun")
    public MoonBoxResult<Void> reRun(@RequestParam("taskRunId")String taskRunId) {
        return  recordRunService.reRun(taskRunId, UserUtils.getLoginName(), TaskRunService.ReRunType.NORMAL);
    }

    /**
     * 停止任务
     *
     * @param taskRunId
     *           taskRunId
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @PostMapping(value = "stopRun")
    public MoonBoxResult<Void> stop(@RequestParam("taskRunId") String taskRunId) {
        recordRunService.stop(taskRunId, UserUtils.getLoginName());
        return MoonBoxResult.createSuccess(null);
    }


    /**
     * 运行详情
     *
     * @param taskRunId
     *           taskRunId
     * @return {@link MoonBoxResult <Void>} runResult
     */
    @GetMapping(value = "runDetail")
    public MoonBoxResult<RecordRunDetailVo> runDetail(@RequestParam("taskRunId") String  taskRunId) {
        RecordRunDetailVo recordRunDetailVo =recordRunService.runDetail(taskRunId);
        return MoonBoxResult.createSuccess(recordRunDetailVo);
    }


    /**
     * 分页查询录制任务执行列表
     *
     * @param pageRequest run list
     * @return {@link MoonBoxResult<Void>} runResult
     */
    @GetMapping(value = "runList")
    public MoonBoxResult<PageResult<TaskRunVo>> runList(TaskRunService.TaskRunPageRequest pageRequest) {
        pageRequest.setIsRecord(true);
        PageResult<TaskRunVo> pageResult =recordRunService.taskRunList(pageRequest);
        return MoonBoxResult.createSuccess(pageResult);
    }


    /**
     * delete record run
     *
     * @param taskRunId taskRunId
     * @return {@link MoonBoxResult<Void>} deleteResult
     */
    @PostMapping(value = "deleteRun")
    public MoonBoxResult<Void> delete(@RequestParam("taskRunId")String  taskRunId) {
        recordRunService.deleteByPkId(taskRunId);
        return MoonBoxResult.createSuccess(null);
    }
}
