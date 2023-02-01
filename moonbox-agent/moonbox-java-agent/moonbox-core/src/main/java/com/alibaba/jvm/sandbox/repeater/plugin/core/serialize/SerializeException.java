/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.BaseException;

/**
 * {@link SerializeException} 序列化异常
 * <p>
 *
 * @author zhaoyb1990
 */
public class SerializeException extends BaseException {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    public SerializeException(String message) {
        super(message);
        MOONBOX_CONTEXT.exceptionOverflow(this);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
        MOONBOX_CONTEXT.exceptionOverflow(this);
    }
}