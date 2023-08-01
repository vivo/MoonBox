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
            if (AssertUtil.allOfNull(recordAgentConfig.getDubboRecordInterfaces(),recordAgentConfig.getMotanRecordInterfaces(),
                    recordAgentConfig.getHttpRecordInterfaces(), recordAgentConfig.getJavaRecordInterfaces())) {
                return CheckerSupported.CheckResult.builder().result(false).message("至少需要配置一个采集接口").build();
            }
            return CheckerSupported.CheckResult.builder().result(true).build();
        }, TemplateCreateReq.class);
    }

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板配置
     */
    private String templateConfig;


    /**
     * 创建人
     */
    private String createUser;
}
