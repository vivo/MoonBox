/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
