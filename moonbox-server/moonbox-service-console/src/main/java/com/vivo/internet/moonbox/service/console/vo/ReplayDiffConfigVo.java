package com.vivo.internet.moonbox.service.console.vo;

import java.util.Date;

import com.vivo.internet.moonbox.common.api.constants.DiffScope;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;

import lombok.Data;

/**
 * ReplayDiffConfigVo - {@link ReplayDiffConfigVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 15:28
 */
@Data
public class ReplayDiffConfigVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 忽略路径
     */
    private String fieldPath;

    /**
     * 作用的uri
     */
    private String diffUri;

    /**
     * 作用范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private Integer diffScope;

    /**
     * 作用范围{@link com.vivo.internet.moonbox.common.api.constants.DiffScope}
     */
    private String diffScopeMsg;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
