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