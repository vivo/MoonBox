package com.vivo.internet.moonbox.service.console.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import com.vivo.internet.moonbox.service.common.utils.AssertUtil;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * TemplateUpdateReq - {@link TemplateUpdateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 10:26
 */
@Data
public class TemplateUpdateReq {

    static {
        ConverterSupported.getInstance()
                .regConverter((ConverterSupported.Converter<TemplateUpdateReq, RecordTaskTemplateWithBLOBs>) data -> {
                    RecordTaskTemplateWithBLOBs template = new RecordTaskTemplateWithBLOBs();
                    template.setTemplateConfig(data.getTemplateConfig());
                    template.setUpdateUser(data.getUpdateUser());
                    template.setType(0);
                    template.setTemplateName(data.getTemplateName());
                    template.setId(data.getId());
                    return template;
                }, TemplateUpdateReq.class, RecordTaskTemplateWithBLOBs.class);

        CheckerSupported.getInstance().regChecker((CheckerSupported.Checker<TemplateUpdateReq>) data -> {
            if(data.getId() == null || data.getId() <0){
                return CheckerSupported.CheckResult.builder().result(false).message("主键id不能为空").build();
            }
            RecordAgentConfig recordAgentConfig = JSON.parseObject(data.getTemplateConfig(), RecordAgentConfig.class);
            if (recordAgentConfig.getRecordCount() <=0) {
                return CheckerSupported.CheckResult.builder().result(false).message("采集数量必须大于0").build();
            }
            if (recordAgentConfig.getRecordTaskDuration() <= 0) {
                return CheckerSupported.CheckResult.builder().result(false).message("运行时间必须要大于0").build();
            }
            if (AssertUtil.allOfNull(recordAgentConfig.getDubboRecordInterfaces(),
                    recordAgentConfig.getHttpRecordInterfaces(), recordAgentConfig.getJavaRecordInterfaces())) {
                return CheckerSupported.CheckResult.builder().result(false).message("至少需要配置一个采集接口").build();
            }

            return CheckerSupported.CheckResult.builder().result(true).build();
        }, TemplateUpdateReq.class);
    }

    /**
     * 主键Id
     */
    @NotNull
    private Long id;

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
     * 模板配置信息
     */
    @NotBlank
    private String templateConfig;

    /**
     * 操作人
     */
    private String updateUser;

}
