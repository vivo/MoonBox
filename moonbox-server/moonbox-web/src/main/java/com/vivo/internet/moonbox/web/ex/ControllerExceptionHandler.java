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
