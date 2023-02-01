/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@link PropertyUtil} 属性操作
 * <p>
 *
 * @author zhaoyb1990
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyUtil {

    private static final Properties properties = new Properties();

    /**
     * 获取系统属性带默认值
     *
     * @param key          属性key
     * @param defaultValue 默认值
     * @return 属性值 or 默认值
     *
     */
    public static String getSystemPropertyOrDefault(String key, String defaultValue) {
        String property = System.getProperty(key);
        return StringUtils.isEmpty(property) ? defaultValue : property;
    }

    static {
        try {
            InputStream is = new FileInputStream(new File(PathUtils.getConfigPath() + "/repeater.properties"));
            properties.load(is);
        } catch (Exception e) {
            // cause this class will be load in repeater console, use this hard code mode to solve compatibility problem.
            if (PropertyUtil.class.getClassLoader().getClass().getCanonicalName().contains("sandbox")) {
                throw new RuntimeException("load repeater-core.properties failed", e);
            }
        }
    }

    /**
     * 获取repeater-core.properties的配置信息
     *
     * @param key          属性key
     * @param defaultValue 默认值
     * @return 属性值 or 默认值
     */
    public static String getPropertyOrDefault(String key, String defaultValue) {
        String property = properties.getProperty(key);
        return StringUtils.isEmpty(property) ? defaultValue : property;
    }
}
