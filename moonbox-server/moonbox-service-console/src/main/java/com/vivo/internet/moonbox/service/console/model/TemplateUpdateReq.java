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
                return CheckerSupported.CheckResult.builder().result(false).message("??????id????????????").build();
            }
            RecordAgentConfig recordAgentConfig = JSON.parseObject(data.getTemplateConfig(), RecordAgentConfig.class);
            if (recordAgentConfig.getRecordCount() <=0) {
                return CheckerSupported.CheckResult.builder().result(false).message("????????????????????????0").build();
            }
            if (recordAgentConfig.getRecordTaskDuration() <= 0) {
                return CheckerSupported.CheckResult.builder().result(false).message("???????????????????????????0").build();
            }
            if (AssertUtil.allOfNull(recordAgentConfig.getDubboRecordInterfaces(),
                    recordAgentConfig.getHttpRecordInterfaces(), recordAgentConfig.getJavaRecordInterfaces())) {
                return CheckerSupported.CheckResult.builder().result(false).message("????????????????????????????????????").build();
            }

            return CheckerSupported.CheckResult.builder().result(true).build();
        }, TemplateUpdateReq.class);
    }

    /**
     * ??????Id
     */
    private Long id;

    /**
     * ????????????
     */
    private String appName;

    /**
     * ????????????
     */
    private String templateName;

    /**
     * ??????????????????
     */
    private String templateConfig;

    /**
     * ?????????
     */
    private String updateUser;

}
