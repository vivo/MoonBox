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
package com.vivo.internet.moonbox.web.agent;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.Heartbeat;
import com.vivo.internet.moonbox.common.api.model.RecordPullModel;
import com.vivo.internet.moonbox.common.api.model.RecordPullRequest;
import com.vivo.internet.moonbox.service.agent.config.service.AgentFileDownLoadService;
import com.vivo.internet.moonbox.service.agent.config.service.HeartBeatService;
import com.vivo.internet.moonbox.service.agent.config.service.TaskConfigService;
import com.vivo.internet.moonbox.service.agent.record.service.RecordService;
import com.vivo.internet.moonbox.service.agent.replay.service.ReplayService;
import com.vivo.internet.moonbox.service.common.constants.CommonConstants;
import com.vivo.internet.moonbox.service.common.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RepeaterAgentController - {@link MoonBoxAgentController}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 16:10
 */
@RestController
@RequestMapping("/api/agent")
public class MoonBoxAgentController {

    private final TaskConfigService taskConfigService;

    private final HeartBeatService heartBeatService;

    private final RecordService recordService;

    private final ReplayService replayService;

    private final AgentFileDownLoadService agentFileDownLoadService;

    @Autowired
    public MoonBoxAgentController(TaskConfigService taskConfigService, HeartBeatService heartBeatService,
            RecordService recordService, ReplayService replayService,AgentFileDownLoadService agentFileDownLoadService ) {
        this.taskConfigService = taskConfigService;
        this.heartBeatService = heartBeatService;
        this.recordService = recordService;
        this.replayService = replayService;
        this.agentFileDownLoadService = agentFileDownLoadService;
    }

    /**
     * saving record data by agent in record task, after agent invoking doReturn()
     * and will call this interface
     *
     * @param request
     *            {@link com.vivo.internet.moonbox.common.api.model.RecordWrapper}
     * @return result {@link MoonBoxResult<String>}
     */
    @RequestMapping(value = "record/save", method = RequestMethod.POST)
    public MoonBoxResult<String> recordSave(HttpServletRequest request) {
        return recordService.saveRecord(RequestUtils.getStringFromBody(request));
    }

    /**
     * saving replay data by agent in replay task, after agent invoking doReturn()
     * and will call this interface
     *
     * @param request
     *            {@link com.vivo.internet.moonbox.common.api.model.RepeatModel}
     * @return {@link MoonBoxResult<String>}
     */
    @RequestMapping(value = "replay/save", method = RequestMethod.POST)
    public MoonBoxResult<String> replaySave(HttpServletRequest request) {
        return replayService.saveReplay(RequestUtils.getStringFromBody(request));
    }

    /**
     * pull record data by agent in replay task after agent start up
     *
     * @param recordPullRequest
     *            request {@link RecordPullRequest}
     * @return {@link MoonBoxResult<RecordPullModel>}
     */
    @RequestMapping(value = "record/pull", method = RequestMethod.POST)
    public MoonBoxResult<RecordPullModel> replayDataPull(@RequestBody
    RecordPullRequest recordPullRequest) {
        MoonBoxResult<RecordPullModel> result=MoonBoxResult.createSuccess(replayService.replayDataPull(recordPullRequest));
        //没有数据关闭任务
        if (!result.getData().getHasNext()) {
            taskConfigService.closeTask(recordPullRequest.getReplayTaskRunId());
        }
        return result;
    }

    /**
     * agent get config from server after agent init
     *
     * @param taskRunId
     *            record or replay recordTaskRunId
     *            {@link MoonBoxResult<AgentConfig>}
     */
    @RequestMapping("getConfig")
    public MoonBoxResult<AgentConfig> getConfig(@RequestParam("taskRunId")
    String taskRunId) {
        return MoonBoxResult.createSuccess(taskConfigService.getTaskConfigCache(taskRunId));
    }

    /**
     * receive heartBeat request from agent
     *
     * @param heartbeat
     *            true:agent alive false:agent close
     * @return {@link MoonBoxResult< Boolean>}
     */
    @RequestMapping("heartbeat")
    public MoonBoxResult<Boolean> heartBeat(@RequestBody
    Heartbeat heartbeat) {
        return MoonBoxResult.createSuccess(heartBeatService.heartBeat(heartbeat));
    }

    /**
     * downLoadSandBoxZipFile
     */
    @RequestMapping("downLoadSandBoxZipFile")
    public void downLoadSandBoxZipFile(HttpServletResponse httpServletResponse) {
        agentFileDownLoadService.downLoadFile(httpServletResponse, CommonConstants.DEFAULT_SANDBOX_FILE_NAME);
    }

    /**
     * downLoadSandBoxZipFile
     */
    @RequestMapping("downLoadMoonBoxZipFile")
    public void downLoadMoonBoxZipFile(HttpServletResponse httpServletResponse) {
        agentFileDownLoadService.downLoadFile(httpServletResponse, CommonConstants.DEFAULT_MOONBOX_FILE_NAME);
    }
}
