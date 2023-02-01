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
package com.vivo.internet.moonbox.service.console.model;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.DataSelect;
import com.vivo.internet.moonbox.common.api.constants.DeleteStatus;
import com.vivo.internet.moonbox.common.api.constants.EnvEnum;
import com.vivo.internet.moonbox.common.api.constants.LogLevel;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.common.api.model.ReplayAgentConfig;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.common.utils.IdGenerator;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * TemplateCreateReq - {@link TemplateCreateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
public class ReplayRunReq extends TaskRunReq {

    static {
        ConverterSupported.getInstance()
                .regConverter((ConverterSupported.Converter<ReplayRunReq, TaskRunInfoWithBLOBs>) taskRunReq -> {
                    TaskRunInfoWithBLOBs taskRunInfo = new TaskRunInfoWithBLOBs();
                    taskRunInfo.setRunEnv(taskRunReq.getRunEnv());
                    taskRunInfo.setRunStatus(TaskRunStatus.START_RUN.getCode());
                    taskRunInfo.setDeleteState(DeleteStatus.EXIST.getStatus());
                    taskRunInfo.setRunDesc(taskRunReq.getRunDesc());
                    taskRunInfo.setCreateUser(taskRunReq.getRunUser());
                    taskRunInfo.setRunHosts(JSON.toJSONString(taskRunReq.getRunHosts()));
                    taskRunInfo.setUpdateUser(taskRunReq.getRunUser());
                    taskRunInfo.setTaskStartTime(new Date());
                    taskRunInfo.setTaskRunId(IdGenerator.replayTaskRunId());
                    taskRunInfo.setRunType(TaskType.JAVA_REPLAY.getCode());

                    taskRunInfo.setRecordRunId(taskRunReq.getRecordTaskRunId());
                    ReplayAgentConfig replayAgentConfig = ReplayAgentConfig.builder().mock(true)
                            .subPlugins(taskRunReq.getSubInvocationPlugins()).dataSelectType(taskRunReq.getSelectType())
                            .traceIds(taskRunReq.getTraceIds())
                            .failDataForReplayTaskRunId(taskRunReq.getFailDataForReplayTaskRunId())
                            .recordTaskRunId(taskRunReq.getRecordTaskRunId())
                            .dataSelectType(taskRunReq.getSelectType())
                            .sandboxLogLevel(taskRunReq.getSandboxLogLevel())
                            .repeaterLogLevel(taskRunReq.getRepeaterLogLevel())
                            .build();
                    taskRunInfo.setRunConfig(JSON.toJSONString(replayAgentConfig));
                    return taskRunInfo;
                }, ReplayRunReq.class, TaskRunInfoWithBLOBs.class);

        CheckerSupported.getInstance().regChecker((CheckerSupported.Checker<ReplayRunReq>) data -> {
            Machine machine = data.getRunHosts();
            EnvEnum runEnv = EnvEnum.getEnvType(data.getRunEnv());
            if (EnvEnum.LOCAL == runEnv) {
                data.setRunHosts(Machine.LOCAL_MACHINE);
            } else {
                Assert.notNull(machine, "运行机器列表不能为空");
                Assert.hasText(machine.getHostIp(), "机器ip不能为空");
                Assert.hasText(machine.getPassWord(), "机器密码不能为空");
                Assert.hasText(machine.getSftpPort(), "机器端口不能为空");
                Assert.hasText(machine.getUserName(), "机器账号名不能为空");
            }
            DataSelect dataSelect = DataSelect.getSelectType(data.getSelectType());
            if (dataSelect == null) {
                BusiException.throwsEx("data select type error");
            }
            if (dataSelect == DataSelect.PART_DATA) {
                if (CollectionUtils.isEmpty(data.getTraceIds())) {
                    BusiException.throwsEx("select part data but traceId is empty");
                }
                Assert.isTrue(data.getTraceIds().size() < 20, "部分流量回放最多只能选择20条流量");
            }
            return CheckerSupported.CheckResult.builder().result(true).build();
        }, ReplayRunReq.class);
    }

    /**
     * 录制任务编码
     */
    private String recordTaskRunId;

    /**
     * 数据选择方式
     * DataSelect{@link com.vivo.internet.moonbox.common.api.constants.DataSelect}
     */
    private Integer selectType;

    /**
     * traceIds列表
     * <p>
     * if {@link ReplayRunReq#selectType} value is PART_DATA, this field must not be
     * null
     * </p>
     */
    private List<String> traceIds;

    /**
     * 回放失败任务id{@link com.vivo.internet.moonbox.common.api.constants.DataSelect#FAIL_DATA}
     */
    private String failDataForReplayTaskRunId;

    /**
     * 选择的子调用列表{@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String> subInvocationPlugins;

    /**
     * sandbox日志级别 {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel = LogLevel.OFF;

    /**
     * repeater日志级别 {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel = LogLevel.OFF;
}