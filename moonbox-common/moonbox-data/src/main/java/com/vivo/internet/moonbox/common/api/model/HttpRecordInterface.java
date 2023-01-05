
package com.vivo.internet.moonbox.common.api.model;

import lombok.Data;

/**
 * HttpRecordInterface - {@link HttpRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:39
 */
@Data
public class HttpRecordInterface extends AbstractRecordInterface {
    /**
     * http请求的uri 支持path variable部分，如 /api/thread/{},{}代表变量
     */
    private String uri;

    /**
     * path variable标志
     */
    public static final String VARIABLE_PATH = "{}";

    @Override
    public String getUniqueKey() {
        return "http" + "_" + uri;
    }
}
