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
package com.vivo.internet.moonbox.common.api.dto;

import lombok.Data;

/**
 * PageResult MoonBox api request result
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/19 16:58
 */
@Data
public class MoonBoxResult<T> {

    /**
     * 响应结果
     */
    private boolean ret;

    /**
     * 响应值
     */
    private T data;

    /**
     * 错误描述
     */
    private String msg;

    /**
     * 错误码
     */
    private String code;

    public static <T> MoonBoxResult<T> createSuccess(T data) {
        return create(true, data, "SUCCESS", "成功");
    }

    public static <T> MoonBoxResult<T> createSuccess() {
        return create(true, null, "SUCCESS", "成功");
    }

    public static <T> MoonBoxResult<T> createFailResponse(String errCode, String errMsg) {
        return create(false, null, errCode, errMsg);
    }

    public static <T> MoonBoxResult<T> createFailResponse(String errMsg) {
        return create(false, null, "ERROR", errMsg);
    }

    public static <T> MoonBoxResult<T> create(boolean ret, T data, String code, String msg) {
        MoonBoxResult<T> res = new MoonBoxResult<>();
        res.ret = ret;
        res.code = code;
        res.msg = msg;
        res.data =data;
        return res;
    }

}
