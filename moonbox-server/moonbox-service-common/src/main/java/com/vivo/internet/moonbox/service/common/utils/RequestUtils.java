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
package com.vivo.internet.moonbox.service.common.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * RequestUtils - HttpServletRequest utils
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 16:29
 */
@Slf4j
public class RequestUtils {

    /**
     * 从request中获取body信息
     *
     * @param request
     *            request
     * @return {@link String}
     */
    public static String getStringFromBody(HttpServletRequest request) {
        List<String> body = null;
        try {
            body = IOUtils.readLines(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("getStringFromBody error!", e);
            return null;
        }
        if (CollectionUtils.isEmpty(body)) {
            log.error("getStringFromBody error!body empty");
            return null;
        }
        return body.get(0);
    }
}