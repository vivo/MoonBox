package com.vivo.internet.moonbox.common.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * JavaRecordInterface - {@link JavaRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 15:17
 */
@Data
public class JavaRecordInterface extends AbstractRecordInterface{

    private String classPattern;

    private String[] methodPatterns;

    private String   obtainApplicationContextMethod;

    @JSONField(serialize = false)
    @Override
    public String getUniqueKey() {
        return "java_" + classPattern;
    }
}
