package com.vivo.internet.moonbox.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FieldDiffConfig - {@link FieldDiffConfig}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 17:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDiffConfig {

    /**
     * 字段对比作用范围，{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private Integer scope;

    /**
     * 忽略该字段的uri接口，如果{@link FieldDiffConfig#scope}值为{@link com.vivo.internet.moonbox.common.api.constants.DiffScope#APP_SCOPE}，那么该字段为空
     */
    private String uri;

    /**
     * 需要忽略的具体路径。例如$[0].xxx
     */
    private String fieldPath;
}
