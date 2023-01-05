package com.vivo.internet.moonbox.service.console.model;

import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.DeleteStatus;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import com.vivo.internet.moonbox.service.common.utils.AssertUtil;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.common.utils.IdGenerator;

import lombok.Data;

/**
 * TemplateCreateReq - {@link TemplateCreateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
public class TemplateCreateReq {

    static {
        ConverterSupported.getInstance()
                .regConverter((ConverterSupported.Converter<TemplateCreateReq, RecordTaskTemplateWithBLOBs>) data -> {
                    RecordTaskTemplateWithBLOBs template = new RecordTaskTemplateWithBLOBs();
                    template.setTemplateId(IdGenerator.templateId());
                    template.setTemplateConfig(data.getTemplateConfig());
                    template.setAppName(data.getAppName());
                    template.setTemplateDesc("");
                    template.setTemplateName(data.getTemplateName());
                    template.setDeleteState(DeleteStatus.EXIST.getStatus());
                    template.setCreateUser(data.getCreateUser());
                    template.setUpdateUser(data.getCreateUser());
                    template.setType(0);
                    return template;
                }, TemplateCreateReq.class, RecordTaskTemplateWithBLOBs.class);

        CheckerSupported.getInstance().regChecker((CheckerSupported.Checker<TemplateCreateReq>) data -> {
            RecordAgentConfig recordAgentConfig = JSON.parseObject(data.getTemplateConfig(), RecordAgentConfig.class);
            if (recordAgentConfig.getRecordCount() <= 0) {
                return CheckerSupported.CheckResult.builder().result(false).message("单个接口采集数量必须大于0").build();
            }
            if (recordAgentConfig.getRecordTaskDuration() <= 0) {
                return CheckerSupported.CheckResult.builder().result(false).message("运行时间必须要大于0").build();
            }
            if (AssertUtil.allOfNull(recordAgentConfig.getDubboRecordInterfaces(),
                    recordAgentConfig.getHttpRecordInterfaces(), recordAgentConfig.getJavaRecordInterfaces())) {
                return CheckerSupported.CheckResult.builder().result(false).message("至少需要配置一个采集接口").build();
            }
            return CheckerSupported.CheckResult.builder().result(true).build();
        }, TemplateCreateReq.class);
    }

    /**
     * 应用名称
     */
    @NotBlank
    private String appName;

    /**
     * 模板名称
     */
    @NotBlank
    private String templateName;

    /**
     * 模板配置
     */
    @NotBlank
    private String templateConfig;


    /**
     * 创建人
     */
    private String createUser;
}
