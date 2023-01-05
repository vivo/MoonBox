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
