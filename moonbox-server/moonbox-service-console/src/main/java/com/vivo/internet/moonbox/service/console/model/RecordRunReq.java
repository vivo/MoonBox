package com.vivo.internet.moonbox.service.console.model;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.DeleteStatus;
import com.vivo.internet.moonbox.common.api.constants.EnvEnum;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.common.utils.IdGenerator;
import lombok.Data;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * TaskRunDTO - 执行 taskRun 的请求参数
 *
 * @author 11119783
 * @version 1.0
 * @since 2020/12/28 14:05
 */
@Data
public class RecordRunReq extends TaskRunReq {

    static {
        ConverterSupported.getInstance()
                .regConverter((ConverterSupported.Converter<RecordRunReq, TaskRunInfoWithBLOBs>) taskRunReq -> {
                    TaskRunInfoWithBLOBs taskRunInfo = new TaskRunInfoWithBLOBs();
                    taskRunInfo.setRunEnv(taskRunReq.getRunEnv());
                    taskRunInfo.setRunStatus(TaskRunStatus.START_RUN.getCode());
                    taskRunInfo.setDeleteState(DeleteStatus.EXIST.getStatus());
                    taskRunInfo.setRunDesc(taskRunReq.getRunDesc());
                    taskRunInfo.setCreateUser(taskRunReq.getRunUser());
                    taskRunInfo.setUpdateUser(taskRunReq.getRunUser());
                    taskRunInfo.setRunHosts(JSON.toJSONString(taskRunReq.getRunHosts()));
                    taskRunInfo.setTemplateId(taskRunReq.getTemplateId());
                    taskRunInfo.setTaskStartTime(new Date());
                    taskRunInfo.setTaskRunId(IdGenerator.recordTaskRunId());
                    taskRunInfo.setRecordRunId(taskRunInfo.getTaskRunId());
                    taskRunInfo.setRunType(TaskType.JAVA_RECORD.getCode());
                    return taskRunInfo;
                }, RecordRunReq.class, TaskRunInfoWithBLOBs.class);

        CheckerSupported.getInstance().regChecker((CheckerSupported.Checker<RecordRunReq>) data -> {
            Machine machine = data.getRunHosts();
            EnvEnum runEnv = EnvEnum.getEnvType(data.getRunEnv());
            if(EnvEnum.LOCAL == runEnv){
                data.setRunHosts(Machine.LOCAL_MACHINE);
            }else {
                Assert.hasText(machine.getHostIp(), "机器ip不能为空");
                Assert.hasText(machine.getPassWord(), "机器密码不能为空");
                Assert.hasText(machine.getSftpPort(), "机器端口不能为空");
                Assert.hasText(machine.getUserName(), "机器账号名不能为空");
                Assert.hasText(data.getTemplateId(), "录制任务模板id不能为空");
            }
            return CheckerSupported.CheckResult.builder().result(true).build();
        }, RecordRunReq.class);
    }

    /**
     * 运行模板id
     */
    @NotBlank
    private String templateId;
}