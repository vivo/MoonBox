/**
 * Copyright:©1995-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
package com.vivo.internet.moonbox.common.api.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.jvm.sandbox.repeater.plugin.Comparable;
import com.alibaba.jvm.sandbox.repeater.plugin.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;

/**
 * ComparableGenerator
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/19 20:25
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class ComparableGenerator {

    private static final String SYMBOL = "$";

    /**
     * 创建默认的比较器
     *
     * @param configs
     *            configs
     * @param defaultMode
     *            defaultMode
     * @return {@link Comparable}
     */
    public static Comparable generate(List<FieldDiffConfig> configs, Comparator.CompareMode defaultMode) {
        return generate(configs, defaultMode, false);
    }

    /**
     * 获取结果对比比较器，只支持 全路径对比 和 排除指定字段对比
     *
     * @param configs
     *            configs
     * @param defaultMode
     *            defaultMode
     * @param http
     *            http
     * @return {@link Comparable}
     */
    public static Comparable generate(List<FieldDiffConfig> configs, Comparator.CompareMode defaultMode, boolean http) {
        // 全部匹配模式
        if (null == configs || configs.size() == 0) {
            return ComparableFactory.instance().create(defaultMode);
        }

        // 走忽略对比差异对比模式
        List<String> paths = Lists.newArrayList();
        List<String> filedDiffNames = Lists.newArrayList();
        configs.forEach(cofig -> {
            // 路径比对
            if (StringUtils.startsWith(cofig.getFieldPath(), SYMBOL)) {
                if (http && (StringUtils.startsWith(cofig.getFieldPath(), "$.requestHeaders")
                        || StringUtils.startsWith(cofig.getFieldPath(), "$.requestParams")
                        || StringUtils.startsWith(cofig.getFieldPath(), "$.requestBody"))) {
                    paths.add(cofig.getFieldPath().replace(SYMBOL, "$[0]"));
                } else {
                    paths.add(cofig.getFieldPath());
                }
            }
            // 参数名比对
            else {
                filedDiffNames.add(cofig.getFieldPath());
            }
        });
        return ComparableFactory.instance().create(paths, filedDiffNames);
    }
}