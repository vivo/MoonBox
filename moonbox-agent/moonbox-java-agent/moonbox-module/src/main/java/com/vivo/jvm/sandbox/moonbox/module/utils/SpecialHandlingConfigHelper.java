
package com.vivo.jvm.sandbox.moonbox.module.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.SpecialHandlingSysTimeMockClasses;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.SpecialHandlingUniversalMockClass;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.enums.SpecialHandlingTypeEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * SpecialHandlingConfigHelper - 获取特殊配置
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/7 10:56 上午
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecialHandlingConfigHelper {

    private static final String JAVA_SYS_DATE_CLASS = "java.util.Date";

    /**
     * 获取需要mock的和时间相关的类
     *
     * @param specialHandlingConfig specialHandlingConfig
     * @return {@link List<String>}
     */
    public static List<String> getSysTimeMockClasses(Map<Integer, String> specialHandlingConfig) {
        Set<String> sysTimeMockClass = innerGetSysTimeMockClasses(specialHandlingConfig);
        sysTimeMockClass.add(JAVA_SYS_DATE_CLASS);
        return new ArrayList<>(sysTimeMockClass);
    }

    public static List<SpecialHandlingUniversalMockClass> getUniversalClasses(Map<Integer, String> specialHandlingConfig) {
        if (Objects.isNull(specialHandlingConfig)) {
            return new ArrayList<>(0);
        }
        String config = specialHandlingConfig.get(SpecialHandlingTypeEnum.UNIVERSAL_MOCK_CLASSES.getCode());
        if (StringUtils.isBlank(config)) {
            return new ArrayList<>(0);
        }
        try {
            return JSON.parseArray(config, SpecialHandlingUniversalMockClass.class);
        } catch (Exception e) {
            log.error("parse special handling config failed, config:{}", config);
            return new ArrayList<>(0);
        }
    }

    private static Set<String> innerGetSysTimeMockClasses(Map<Integer, String> specialHandlingConfig) {
        if (Objects.isNull(specialHandlingConfig)) {
            return new HashSet<>(0);
        }

        String config = specialHandlingConfig.get(SpecialHandlingTypeEnum.TIME_MOCK_CLASSES.getCode());
        if (StringUtils.isBlank(config)) {
            return new HashSet<>(0);
        }

        try {
            SpecialHandlingSysTimeMockClasses specialHandlingSysTimeMockClasses = JSON.parseObject(config, SpecialHandlingSysTimeMockClasses.class);
            if (Objects.isNull(specialHandlingSysTimeMockClasses)) {
                return new HashSet<>(0);
            }
            return new HashSet<>(specialHandlingSysTimeMockClasses.getSysTimeMockClasses());
        } catch (Exception e) {
            log.error("parse special handling config failed, config:{}", config);
            return new HashSet<>(0);
        }
    }

}