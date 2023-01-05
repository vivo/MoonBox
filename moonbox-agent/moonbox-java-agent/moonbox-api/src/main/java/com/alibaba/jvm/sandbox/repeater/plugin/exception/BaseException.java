package com.alibaba.jvm.sandbox.repeater.plugin.exception;

/**
 * BaseException
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 2:13 下午
 */
public class BaseException extends Exception {

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

}