package com.vivo.internet.moonbox.service.console.model;

import java.util.List;

import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String appName;

    /**
     * {@link ReplayMockTypeEnum}
     */
    @NotBlank
    private Integer mockType;

    /**
     * content内容
     */
    @NotBlank
    private String contentJson;

    /**
     * 创建人
     */
    private String createUser;
}
