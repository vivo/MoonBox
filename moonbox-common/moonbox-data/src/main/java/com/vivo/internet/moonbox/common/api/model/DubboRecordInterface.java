package com.vivo.internet.moonbox.common.api.model;

import lombok.Data;

/**
 * DubboRecordInterface - {@link DubboRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:45
 */
@Data
public class DubboRecordInterface extends AbstractRecordInterface {

    private String interfaceName;

    private String methodName;

    @Override
    public String getUniqueKey() {
        return "dubbo" + "_" + interfaceName + "_" + methodName;
    }
}
