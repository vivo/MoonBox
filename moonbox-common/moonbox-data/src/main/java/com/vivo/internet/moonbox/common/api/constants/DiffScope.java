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
package com.vivo.internet.moonbox.common.api.constants;

/**
 * DiffScope - 字段对比范围设置
 * <P>
 *     如果是{@link DiffScope#APP_SCOPE}代表整个应用忽略该对比字段
 *     如果是{@link DiffScope#ENTRANCE_URL_SCOPE}代表只允许某个uri接口忽略该对比字段
 *     如果是{@link DiffScope#SUB_URL_SCOPE}代表只允许某个子调用忽略该对比字段
 * </P>
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/11/17 16:16
 */
public enum DiffScope {

    /**
     * 整个应用都生效
     */
    APP_SCOPE(1,"作用整个应用"),

    /**
     * 作用在了入口url上
     */
    ENTRANCE_URL_SCOPE(2,"作用在入口url上"),

    /**
     * 作用在了子调用上
     */
    SUB_URL_SCOPE(3,"作用在子调用上");

    private int code;

    private String message;

    DiffScope(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DiffScope getDiffScope(Integer code) {
        for (DiffScope diffScope: DiffScope.values()) {
            if (diffScope.getCode() == code) {
                return diffScope;
            }
        }
        return null;
    }
}