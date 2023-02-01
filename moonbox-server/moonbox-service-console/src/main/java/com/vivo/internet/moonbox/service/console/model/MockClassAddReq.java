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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.ReplayMockTypeEnum;
import com.vivo.internet.moonbox.common.api.model.MockClass;
import com.vivo.internet.moonbox.common.api.model.SysTimeMockClasses;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * MockClassAddReq - {@link MockClassAddReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 16:44
 */
@Data
public class MockClassAddReq {

    static {
        ConverterSupported.getInstance()
                .regConverter((ConverterSupported.Converter<MockClassAddReq, SpecialMockConfig>) mockClassAddReq -> {
                    SpecialMockConfig specialMockConfig = new SpecialMockConfig();
                    specialMockConfig.setAppName(mockClassAddReq.getAppName());
                    specialMockConfig.setContentJson(mockClassAddReq.getContentJson());
                    specialMockConfig.setMockType(mockClassAddReq.getMockType());
                    specialMockConfig.setCreateUser(mockClassAddReq.getCreateUser());
                    specialMockConfig.setUpdateUser(mockClassAddReq.getCreateUser());
                    return specialMockConfig;
                }, MockClassAddReq.class, SpecialMockConfig.class);

        CheckerSupported.getInstance().regChecker((CheckerSupported.Checker<MockClassAddReq>) data -> {
            String content = data.getContentJson();
            ReplayMockTypeEnum replayMockTypeEnum = ReplayMockTypeEnum.getReplayMockTypeEnum(data.mockType);
            if (replayMockTypeEnum == null) {
                BusiException.throwsEx("mockType 类型不对");
            }
            switch (replayMockTypeEnum) {
                case TIME_MOCK_CLASSES:
                    SysTimeMockClasses mockClassesList = JSON.parseObject(content, SysTimeMockClasses.class);
                    if (CollectionUtils.isEmpty(mockClassesList.getSysTimeMockClasses())) {
                        BusiException.throwsEx("time mock class类不能为空");
                    }
                    break;
                case UNIVERSAL_MOCK_CLASSES:
                    List<MockClass> mockClass = JSON.parseArray(content, MockClass.class);
                    if (CollectionUtils.isEmpty(mockClass)) {
                        BusiException.throwsEx("mock class类列表不能为空");
                    }
                    mockClass.stream().forEach(mockClass1 -> {
                        if (StringUtils.isBlank(mockClass1.getClassName())) {
                            BusiException.throwsEx("mock class类名不能为空");
                        }
                        if (CollectionUtils.isEmpty(mockClass1.getMethodList())) {
                            BusiException.throwsEx("mock class方法列表不能为空");
                        }
                    });
                    break;
                default:
                    break;

            }
            return CheckerSupported.CheckResult.builder().result(true).build();
        }, MockClassAddReq.class);
    }

    /**
     * 应用名称
     */
    private String appName;

    /**
     * {@link ReplayMockTypeEnum}
     */
    private Integer mockType;

    /**
     * content内容
     */
    private String contentJson;

    /**
     * 创建人
     */
    private String createUser;
}
