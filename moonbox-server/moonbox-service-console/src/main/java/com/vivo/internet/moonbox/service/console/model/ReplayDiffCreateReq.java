package com.vivo.internet.moonbox.service.console.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * TemplateCreateReq - {@link ReplayDiffCreateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
public class ReplayDiffCreateReq {

    static {
        ConverterSupported.getInstance().regConverter(
                (ConverterSupported.Converter<ReplayDiffCreateReq, ReplayDiffConfig>) replayDiffCreateReq -> {
                    ReplayDiffConfig replayDiffConfig = new ReplayDiffConfig();
                    replayDiffConfig.setAppName(replayDiffCreateReq.getAppName());
                    replayDiffConfig.setDiffUri(replayDiffCreateReq.getDiffUri());
                    replayDiffConfig.setFieldPath(replayDiffCreateReq.getFieldPath());
                    replayDiffConfig.setCreateUser(replayDiffCreateReq.getCreateUser());
                    replayDiffConfig.setUpdateUser(replayDiffCreateReq.getCreateUser());
                    replayDiffConfig.setDiffScope(replayDiffCreateReq.getDiffScope());

                    return replayDiffConfig;
                }, ReplayDiffCreateReq.class, ReplayDiffConfig.class);
    }

    /**
     * 应用名称(这里暂定设置所有对比路径都挂在某个应用下面，后续可以支持全局忽略路径或者字段
     */
    @NotBlank
    private String  appName;

    /**
     * 字段路径
     */
    @NotBlank
    private String  fieldPath;

    /**
     * 忽略字段对应的uri
     */
    @NotBlank
    private String  diffUri;

    /**
     * 忽略范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    @NotNull
    private Integer diffScope;

    /**
     * 创建人
     */
    private String createUser;
}
