/**
 * Copyright:©1985-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
package com.vivo.internet.moonbox.web.ex;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 11112487
 * @since 2020/6/4 14:30
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 捕获全局其他异常
     *
     * @param req
     *            req
     * @param e
     *            e
     */
    @ExceptionHandler(value = Exception.class)
    public MoonBoxResult exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("请求发生业务异常！原因是：{}", e.getMessage(), e);
        log.info("uri:{}, querystring:{}", req.getRequestURI(), req.getQueryString());
        if (DBExceptionUtils.isUniqueKeyException(e)) {
            return MoonBoxResult.createFailResponse("BIZ_EXCEPTION", "数据库唯一键异常，请使用更新");
        }
        return MoonBoxResult.createFailResponse("BIZ_EXCEPTION",e.getMessage());
    }
}
