package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SignUtils
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/29 5:38 下午
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignUtils {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    /**
     * 添加请求headers
     *
     * @return 要添加的headers
     */
    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }
}
