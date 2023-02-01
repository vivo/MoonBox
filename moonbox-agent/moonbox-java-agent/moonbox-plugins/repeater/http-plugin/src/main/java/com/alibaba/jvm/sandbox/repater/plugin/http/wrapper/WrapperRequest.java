/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repater.plugin.http.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.jvm.sandbox.repater.plugin.http.HttpStandaloneListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class WrapperRequest extends HttpServletRequestWrapper {

    private final HttpServletResponse response;

    private final String body;

    private final HttpStandaloneListener listener;

    private final boolean usingBody;

    /*
     * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
     */
    private String uriWithVariable;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     *            请求
     * @throws IOException
     *             if the request is null
     */
    public WrapperRequest(HttpServletRequest request, HttpServletResponse response, HttpStandaloneListener listener)
            throws IOException {
        super(request);
        this.response = response;
        this.listener = listener;
        // application/json的方式提交，需要拦截body
        this.usingBody = usingBody(request);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        if (usingBody) {
            try {
                InputStream inputStream = request.getInputStream();
                if (inputStream != null) {
                    String ce = request.getCharacterEncoding();
                    if (StringUtils.isNotEmpty(ce)) {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ce));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    }
                    char[] charBuffer = new char[128];
                    int bytesRead;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                }
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }
        body = stringBuilder.toString();
    }

    /**
     * 是否录制body
     * 
     * @param request
     * @return
     */
    private boolean usingBody(HttpServletRequest request) {
        // appContent-api项目没加content-type
        if (MoonboxContext.getInstance().getAppName().equals("8573")) {
            return true;
        }
        return StringUtils.contains(request.getContentType(), "application/json");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return startAsync(getRequest(), response);
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        listener.onStartAsync(this);
        AsyncContext asyncContext = super.startAsync(this, response);
        asyncContext.addListener(new WrapperAsyncListener(listener, this, (WrapperResponseCopier) response));
        return asyncContext;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (usingBody) {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        } else {
            return super.getInputStream();
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (usingBody) {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        } else {
            return super.getReader();
        }
    }

    public String getBody() {
        return this.body;
    }

    public String getUriWithVariable() {
        return uriWithVariable;
    }

    public void setUriWithVariable(String uriWithVariable) {
        this.uriWithVariable = uriWithVariable;
    }
}