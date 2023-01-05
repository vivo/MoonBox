package com.vivo.internet.moonbox.service.common.ex;


/**
 * BusiException - {@link BusiException}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 10:48
 */
public class BusiException extends RuntimeException {

    public BusiException(String message) {
        super(message);
    }

    public BusiException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwsEx(String message){
        throw new BusiException(message);
    }

    public static void throwsEx(String message,Throwable casue){
        throw new BusiException(message,casue);
    }

}
