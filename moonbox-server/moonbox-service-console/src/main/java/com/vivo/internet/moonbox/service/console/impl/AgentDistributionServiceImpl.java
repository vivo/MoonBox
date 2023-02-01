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

        //校验agent文件是否已经安装了
        checkAgentFile(envEnum);
        //如果选择LOCAL或者环境是Mac or Windows表示用户在本地执行任务
        if (envEnum == EnvEnum.LOCAL || AgentUtil.isWindowsOrMacOs()) {
            result = startLocalAgent(taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), agentConfig, moonBoxLogService);
        } else {
            result = startServerAgent(taskRunInfo, serverUrl + sandboxDownLoadUri, serverUrl + moonBoxDownLoadUri, agentConfig,moonBoxLogService);
        }
        log.info("任务id:{},应用：{}任务执行结果：{}", taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), result);
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
                BusiException.throwsEx("sandbox或者.sandbox-module目录文件没有找到，请确认本地执行install-local-agent.sh安装了文件");
            }
        } else {
            List<AgentDetailVo> agentDetailVos = consoleAgentService.getFileList();
            agentDetailVos.stream().forEach(agentDetailVo -> {
                if (StringUtils.isBlank(agentDetailVo.getContent())) {
                    BusiException.throwsEx("文件上传记录没有找到，请确认在管理平台上传了agent");

                }
                if (!new File(agentDetailVo.getContent()).exists()) {
                    BusiException.throwsEx(agentDetailVo.getContent() + " 文件在服务器没有找到，请确认文件已经正确上传");
                }
            });
        }
    }

    /**
     * 开启本地 agent
     *
     * @param agentConfig agent启动参数
     * @param appName     应用名称
     * @param taskRunId   执行任务id
     * @throws Exception 异常信息
     */
    private static String startLocalAgent(String taskRunId, String appName, String agentConfig, MoonBoxLogService moonBoxLogService) throws Exception {
        String startCommand = AgentUtil.getLocalAgentStartCommand(appName, agentConfig);
        log.info("任务id:{},应用：{}，本地任务启动参数：{}", taskRunId, appName, agentConfig);
        moonBoxLogService.insertRunLog(MoonBoxLogVO.builder().taskRunId(taskRunId).content("任务启动参数:" + startCommand).build());
        return AgentUtil.javaCommandExecute(startCommand);
    }

    /**
     * 服务器上面执行任务，使用shell脚本执行
     *
     * @param agentConfig agent启动参数
     * @param taskRunInfo 运行信息
     * @throws Exception 异常信息
     */
    private static String startServerAgent(TaskRunInfoWithBLOBs taskRunInfo, String sandboxDownLoadUrl, String moonboxDownLoadUrl, String agentConfig,MoonBoxLogService moonBoxLogService) throws Exception {
        Machine machine = JSON.parseObject(taskRunInfo.getRunHosts(), Machine.class);
        String command = AgentUtil.getRemoteAgentStartCommand(sandboxDownLoadUrl, moonboxDownLoadUrl, taskRunInfo.getAppName(), agentConfig);
        log.info("任务id:{},应用：{} 远程任务启动命令：{}", taskRunInfo.getTaskRunId(), taskRunInfo.getAppName(), command);
        moonBoxLogService.insertRunLog(MoonBoxLogVO.builder().taskRunId(taskRunInfo.getTaskRunId()).content("任务启动参数:" + command).build());
        return JSchUtil.distributeAgent(machine.getHostIp(), machine.getSftpPort(), machine.getUserName(), machine.getPassWord(), DEFAULT_TIME_OUT, command);
    }


    /**
     * 如果用户没有手动安装agent，在本地帮助用户自动安装agent
     *
     * @param tarFilePath   安装路径
     * @param targetHomeDirStr 目标目录
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
     * 判断文件是否为null
     * @param file 文件路径
     * @return 判断结果
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
