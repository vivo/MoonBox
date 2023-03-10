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

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.EnvEnum;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.common.api.model.ReplayAgentConfig;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.JSchUtil;
import com.vivo.internet.moonbox.service.console.AgentDistributionService;
import com.vivo.internet.moonbox.service.console.ConsoleAgentService;
import com.vivo.internet.moonbox.service.console.MoonBoxLogService;
import com.vivo.internet.moonbox.service.console.util.AgentUtil;
import com.vivo.internet.moonbox.service.console.vo.AgentDetailVo;
import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * AgentDistributionServiceImpl - {@link AgentDistributionServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/13 9:57
 */
@Service
@Slf4j
public class AgentDistributionServiceImpl implements AgentDistributionService {

    @Value("${moonbox.server.url}")
    private String serverUrl;

    @Value("${sandbox.agent.download.uri}")
    private String sandboxDownLoadUri;

    @Value("${moonbox.agent.download.uri}")
    private String moonBoxDownLoadUri;

    private  static String DEFAULT_TIME_OUT = "20000";

    @Resource
    private MoonBoxLogService moonBoxLogService;

    @Resource
    private ConsoleAgentService consoleAgentService;

    @Override
    public String startAgent(TaskRunInfoWithBLOBs taskRunInfo) throws Exception {
        EnvEnum envEnum = EnvEnum.getEnvType(taskRunInfo.getRunEnv());
        String sandboxLogLevel;
        String repeaterLogLevel;
        TaskType taskType = TaskType.getType(taskRunInfo.getRunType());
        if (taskType == TaskType.JAVA_RECORD) {
            RecordAgentConfig recordAgentConfig = JSON.parseObject(taskRunInfo.getRunConfig(), RecordAgentConfig.class);
            sandboxLogLevel = recordAgentConfig.getSandboxLogLevel();
            repeaterLogLevel = recordAgentConfig.getRepeaterLogLevel();
        } else {
            ReplayAgentConfig replayAgentConfig = JSON.parseObject(taskRunInfo.getRunConfig(), ReplayAgentConfig.class);
            sandboxLogLevel = replayAgentConfig.getSandboxLogLevel();
            repeaterLogLevel = replayAgentConfig.getRepeaterLogLevel();
        }
        String agentConfig = AgentUtil.getAgentStartConfig(serverUrl, taskRunInfo.getTaskRunId(), sandboxLogLevel, repeaterLogLevel);
        String result;

        //??????agent???????????????????????????
        checkAgentFile(envEnum);
        //????????????LOCAL???????????????Mac or Windows?????????????????????????????????
        if (envEnum == EnvEnum.LOCAL || AgentUtil.isWindowsOrMacOs()) {
            result = startLocalAgent(taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), agentConfig, moonBoxLogService);
        } else {
            result = startServerAgent(taskRunInfo, serverUrl + sandboxDownLoadUri, serverUrl + moonBoxDownLoadUri, agentConfig,moonBoxLogService);
        }
        log.info("??????id:{},?????????{}?????????????????????{}", taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), result);
        return result;
    }

    private void checkAgentFile(EnvEnum envEnum){
        if (EnvEnum.LOCAL == envEnum || AgentUtil.isWindowsOrMacOs()) {
            String userDir = System.getProperty("user.dir");
            File sandboxFileDir = new File(AgentUtil.SANDBOX_HOME);
            File sandboxModuleFileDir = new File(AgentUtil.SANDBOX_MODULE);
            if(isFileEmpty(sandboxFileDir)){
                installAgent(userDir + File.separator + "local-agent" + File.separator + "sandbox-stable-bin.tar", System.getProperty("user.home"));
            }
            if(isFileEmpty(sandboxModuleFileDir)){
                installAgent(userDir + File.separator + "local-agent" + File.separator + "monbox-agent.tar", System.getProperty("user.home"));
            }
            if (isFileEmpty(sandboxFileDir) || isFileEmpty(sandboxModuleFileDir)) {
                BusiException.throwsEx("sandbox??????.sandbox-module????????????????????????????????????????????????install-local-agent.sh???????????????");
            }
        } else {
            List<AgentDetailVo> agentDetailVos = consoleAgentService.getFileList();
            agentDetailVos.stream().forEach(agentDetailVo -> {
                if (StringUtils.isBlank(agentDetailVo.getContent())) {
                    BusiException.throwsEx("??????????????????????????????????????????????????????????????????agent");

                }
                if (!new File(agentDetailVo.getContent()).exists()) {
                    BusiException.throwsEx(agentDetailVo.getContent() + " ??????????????????????????????????????????????????????????????????");
                }
            });
        }
    }

    /**
     * ???????????? agent
     *
     * @param agentConfig agent????????????
     * @param appName     ????????????
     * @param taskRunId   ????????????id
     * @throws Exception ????????????
     */
    private static String startLocalAgent(String taskRunId, String appName, String agentConfig, MoonBoxLogService moonBoxLogService) throws Exception {
        String startCommand = AgentUtil.getLocalAgentStartCommand(appName, agentConfig);
        log.info("??????id:{},?????????{}??????????????????????????????{}", taskRunId, appName, agentConfig);
        moonBoxLogService.insertRunLog(MoonBoxLogVO.builder().taskRunId(taskRunId).content("??????????????????:" + startCommand).build());
        return AgentUtil.javaCommandExecute(startCommand);
    }

    /**
     * ????????????????????????????????????shell????????????
     *
     * @param agentConfig agent????????????
     * @param taskRunInfo ????????????
     * @throws Exception ????????????
     */
    private static String startServerAgent(TaskRunInfoWithBLOBs taskRunInfo, String sandboxDownLoadUrl, String moonboxDownLoadUrl, String agentConfig,MoonBoxLogService moonBoxLogService) throws Exception {
        Machine machine = JSON.parseObject(taskRunInfo.getRunHosts(), Machine.class);
        String command = AgentUtil.getRemoteAgentStartCommand(sandboxDownLoadUrl, moonboxDownLoadUrl, taskRunInfo.getAppName(), agentConfig);
        log.info("??????id:{},?????????{} ???????????????????????????{}", taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), command);
        moonBoxLogService.insertRunLog(MoonBoxLogVO.builder().taskRunId(taskRunInfo.getTaskRunId()).content("??????????????????:" + command).build());
        return JSchUtil.distributeAgent(machine.getHostIp(), machine.getSftpPort(), machine.getUserName(), machine.getPassWord(), DEFAULT_TIME_OUT, command);
    }


    /**
     * ??????????????????????????????agent????????????????????????????????????agent
     *
     * @param tarFilePath   ????????????
     * @param targetHomeDirStr ????????????
     */
    private static void installAgent(String tarFilePath, String targetHomeDirStr) {
        File tarFile = new File(tarFilePath);
        if (!tarFile.exists()) {
            return;
        }
        File targetHomeDir = new File(targetHomeDirStr);
        if (!targetHomeDir.exists()) {
            targetHomeDir.mkdirs();
        }
        try {
            AgentUtil.javaCommandExecute("tar xzf "+ tarFilePath+" -C "+ targetHomeDirStr);
        }catch (Exception e){ }
    }

    /**
     * ?????????????????????null
     * @param file ????????????
     * @return ????????????
     */
    private static boolean isFileEmpty(File file) {
        if (!file.exists() || !file.isDirectory()){
            return true;
        }
        if(file.listFiles() == null){
            return true;
        }
        if(file.listFiles().length < 1) {
            return true;
        }
        return false;
    }
}
