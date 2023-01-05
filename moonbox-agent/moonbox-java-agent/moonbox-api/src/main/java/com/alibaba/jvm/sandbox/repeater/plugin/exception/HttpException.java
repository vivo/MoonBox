package com.alibaba.jvm.sandbox.repeater.plugin.exception;

/**
 * HttpException
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/20 11:27 上午
 */
public class HttpException extends RuntimeException {

    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}