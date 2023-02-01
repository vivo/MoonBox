/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repater.plugin.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperRequest;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperResponseCopier;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperTransModel;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.StandaloneSwitch;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.MoonboxRecordIndicatorManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.UriSampleRateRandomUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.google.common.base.Splitter;
import com.vivo.internet.moonbox.common.api.model.HttpInvocation;
import com.vivo.internet.moonbox.common.api.model.HttpRecordInterface;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.RecordModel;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpStandaloneListener} 继承
 * {@link DefaultEventListener}但是由于http有同步异步两种策略，因此需要重写一些方法
 * <p>
 *
 * @author zhaoyb1990
 *
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@SuppressWarnings({ "AlibabaUndefineMagicConstant", "AlibabaThreadLocalShouldRemove", "UnstableApiUsage" })
@Slf4j
public class HttpStandaloneListener extends DefaultEventListener implements InvokeAdvice {

    private final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private final ThreadLocal<WrapperTransModel> wtmRef = new ThreadLocal<>();

    private final ThreadLocal<Pair<String, WrapperResponseCopier>> rtidCopierRef = new ThreadLocal<>();

    private final ThreadLocal<RequestProcessDto> REQUEST_PROCESS_DTO_THREAD_LOCAL = new ThreadLocal<>();

    HttpStandaloneListener(InvokeType invokeType, boolean entrance, InvocationListener listener,
            InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    /**
     * 重写initContext；对于http请求；before事件里面
     *
     * @param event
     *            事件
     */
    @Override
    protected boolean sample(Event event) {
        // 如果是录制流量则进行采样率计算
        if (!MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId()) && entrance && event instanceof BeforeEvent) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            try {
                Object request = beforeEvent.argumentArray[0];
                Object response = beforeEvent.argumentArray[1];
                if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
                    return false;
                }
                HttpServletRequest req = (HttpServletRequest) request;
                // 根据 requestURI 进行采样匹配
                List<HttpRecordInterface> patterns = MOONBOX_CONTEXT.getConfig().getHttpEntrancePatterns();
                if (!matchRequestUriAndSample(patterns, req.getRequestURI())) {
                    ContextResourceClear.sampleFalse();
                    return false;
                }
                return Tracer.getContext().inTimeSample(invokeType);
            } catch (Exception e) {
                log.error("error occurred when init dubbo invocation", e);
                ContextResourceClear.sampleFalse();
                return false;
            }
        } else {
            return super.sample(event);
        }
    }

    @Override
    protected void initContext(Event event) {
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent be = (BeforeEvent) event;
            Object request = be.argumentArray[0];
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = ((HttpServletRequest) request);
                if (req.getRequestURI().contains("sandbox")) {
                    // 忽略
                    log.debug("request:{} is ignored", req.getRequestURI());
                    return;
                }
                // header透传开始回放；
                String traceIdX = req.getHeader(Constants.HEADER_TRACE_ID_X);
                if (StringUtils.isEmpty(traceIdX)) {
                    traceIdX = req.getParameter(Constants.HEADER_TRACE_ID_X);
                }
                if (TraceGenerator.isValid(traceIdX)) {
                    if (log.isInfoEnabled()) {
                        log.info("Http before event, 从header或parameter中获取的 traceIdX 为：" + traceIdX);
                    }
                    RepeatMeta meta = new RepeatMeta();
                    meta.setAppName(MOONBOX_CONTEXT.getAppName());
                    meta.setMock(MOONBOX_CONTEXT.getConfig().isRepeaterMock());
                    meta.setTraceId(traceIdX);
                    meta.setMatchPercentage(100);
                    meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
                    meta.setRepeatId(traceIdX);
                    RepeaterResult<RecordModel> pr = StandaloneSwitch.getInstance().getBroadcaster().pullRecord(meta);
                    if (pr.isSuccess()) {
                        Tracer.start();
                        RepeatContext context = new RepeatContext(meta, pr.getData(), Tracer.getTraceId());
                        MoonboxRepeatCache.putRepeatContext(context);
                        return;
                    }
                }
                // header透传traceId
                String traceId = req.getHeader(Constants.HEADER_TRACE_ID);
                if (StringUtils.isEmpty(traceId)) {
                    traceId = req.getParameter(Constants.HEADER_TRACE_ID);
                }
                if (TraceGenerator.isValid(traceId)) {
                    if (log.isInfoEnabled()) {
                        log.info("Http before event, 从header或parameter中获取的traceId为：" + traceId);
                    }
                    Tracer.start(traceId);
                    return;
                }
            }
        }
        super.initContext(event);
    }

    @Override
    protected void doBefore(BeforeEvent event) {

        Object request = event.argumentArray[0];
        Object response = event.argumentArray[1];
        if (request instanceof WrapperRequest) {
            RequestProcessDto processDto = REQUEST_PROCESS_DTO_THREAD_LOCAL.get();
            if (processDto == null) {
                log.warn("invalid request, no matched processDto found, traceId={}", Tracer.getTraceId());
                return;
            }
            processDto.addSkipInvoke(event.invokeId);
            log.debug("skip event, javaClassName:{}, processId:{}, invokeId:{}", event.javaClassName, event.processId,
                    event.invokeId);
            return;
        }
        if (event.processId == event.invokeId) {
            RequestProcessDto requestProcessDto = new RequestProcessDto();
            requestProcessDto.setProcessId(event.processId);
            REQUEST_PROCESS_DTO_THREAD_LOCAL.set(requestProcessDto);
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            // get repeater-trace-id from request header
            String rtid = req.getHeader(Constants.HEADER_TRACE_ID);
            WrapperResponseCopier wrapperRes = new WrapperResponseCopier(resp);
            rtidCopierRef.set(Pair.of(rtid, wrapperRes));
            event.argumentArray[1] = wrapperRes;
            return;
        }
        WrapperResponseCopier wrapperRes = new WrapperResponseCopier(resp);
        WrapperRequest wrapperReq;
        Pair<String, String> identityUri = getIdentityUri(req.getRequestURI());
        try {
            wrapperReq = new WrapperRequest(req, wrapperRes, this);
            wrapperReq.setUriWithVariable(identityUri.getRight());
        } catch (IOException e) {
            log.error("error occurred when assemble wrapper request", e);
            ContextResourceClear.sampleFalse();
            return;
        }
        WrapperTransModel wtm = WrapperTransModel.build(wrapperReq);

        wtm.setBody(wrapperReq.getBody());

        MoonboxRecordIndicatorManager.getInstance().setRecordingUri(identityUri.getRight());
        wtm.copier = wrapperRes;
        wtm.request = wrapperReq;
        onRequest(wrapperReq, event);
        event.argumentArray[0] = wrapperReq;
        event.argumentArray[1] = wrapperRes;
        wtmRef.set(wtm);
        Tracer.getContext().setSampled(true);
        Tracer.getContext().setRecordEntranceUri(identityUri.getRight());
    }

    @Override
    protected void doThrow(ThrowsEvent event) {
        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            return;
        }
        ContextResourceClear.sampleFalse();
        // doFinish();
    }

    @Override
    protected void doReturn(ReturnEvent event) {
        RequestProcessDto processDto = REQUEST_PROCESS_DTO_THREAD_LOCAL.get();
        if (processDto == null) {
            log.warn("invalid request, no matched processDto found, traceId={}", Tracer.getTraceId());
            return;
        }
        if (processDto.shouldSkip(event.invokeId)) {
            log.debug("event skiped, processId:{}, invokeId:{}", event.processId, event.invokeId);
            return;
        }

        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            Pair<String, WrapperResponseCopier> rtidCopier = rtidCopierRef.get();
            if (rtidCopier == null) {
                return;
            }
            try {
                int status = rtidCopier.getRight().getStatus();
                String response;
                if (status >= 400) {
                    response = "http response status is: " + status;
                } else {
                    response = new String(rtidCopier.getRight().getResponseData(),
                            rtidCopier.getRight().getCharacterEncoding());
                }
                MoonboxRepeatCache.putHttpResponse(rtidCopier.getLeft(), response);
            } catch (IOException ioe) {
                log.error("error occurred when get replay response, error msg: {}", ioe.getMessage());
            } finally {
                rtidCopierRef.remove();
            }
            return;
        }
        doFinish();
    }

    private void doFinish() {
        WrapperTransModel wtm = wtmRef.get();
        if (wtm == null) {
            log.warn("invalid request, no matched wtm found, traceId={}", Tracer.getTraceId());
            return;
        }
        onResponse(wtm.request, wtm);
        wtmRef.remove();
    }

    @Override
    public void onStartAsync(WrapperRequest request) {
        HttpInvocation invocation = (HttpInvocation) MoonboxRecordCache.getInvocation(request.hashCode());
        if (invocation == null) {
            return;
        }
        invocation.setAsync(true);
    }

    @Override
    public void onComplete(WrapperRequest request, WrapperTransModel wtm) {
        HttpInvocation invocation = (HttpInvocation) MoonboxRecordCache.getInvocation(request.hashCode());
        if (invocation == null) {
            return;
        }
        onFinish(invocation, wtm);
    }

    @Override
    public void onRequest(WrapperRequest request, BeforeEvent event) {
        HttpInvocation invocation = new HttpInvocation();
        invocation.setInit(true);
        invocation.setStart(System.currentTimeMillis());
        invocation.setIndex(1);
        invocation.setType(InvokeType.HTTP);
        invocation.setEntrance(true);
        invocation.setTraceId(Tracer.getTraceId());
        invocation.setInvokeId(event.invokeId);
        invocation.setProcessId(event.processId);
        invocation.setClassLoader(event.javaClassLoader);
        invocation.setSerializeToken(ClassloaderBridge.instance().encode(event.javaClassLoader));
        invocation.setUriWithVariable(request.getUriWithVariable());
        MoonboxRecordCache.cacheInvocation(request.hashCode(), invocation);

    }

    @Override
    public void onResponse(WrapperRequest request, WrapperTransModel wtm) {
        HttpInvocation invocation = (HttpInvocation) MoonboxRecordCache.getInvocation(request.hashCode());
        if (invocation == null || invocation.isAsync()) {
            return;
        }
        try {
            // 先判断下是否4xx，如果是4xx那直接返回状态码
            int status = wtm.copier.getStatus();
            if (status >= 400) {
                wtm.setResponse("http response status is: " + status);
            } else {
                wtm.setResponse(new String(wtm.copier.getResponseData(), wtm.copier.getCharacterEncoding()));
            }

        } catch (Exception e) {
            log.error("error occurred when get response,message = {}", e.getMessage());
        }
        onFinish(invocation, wtm);
    }

    private void onFinish(HttpInvocation invocation, WrapperTransModel wtm) {
        if (invocation.isInit()) {
            assembleHttpAttribute(invocation, wtm);
            invocation.setEnd(System.currentTimeMillis());
            listener.onInvocation(invocation);
        }
    }

    private void assembleHttpAttribute(HttpInvocation invocation, WrapperTransModel wtm) {
        Identity identity = new Identity(InvokeType.HTTP.name(), wtm.getRequestURI(), "", null);
        invocation.setRequestURL(wtm.getRequestURL());
        invocation.setRequestURI(wtm.getRequestURI());
        invocation.setPort(wtm.getPort());
        invocation.setMethod(wtm.getMethod());
        invocation.setContentType(wtm.getContentType());
        invocation.setHeaders(wtm.getHeaders());
        invocation.setBody(wtm.getBody());
        invocation.setParamsMap(wtm.getParamsMap());
        // 翻译WrapperTransferModel参数
        Map<String, Object> params = new HashMap<>(8);
        params.put("requestURI", wtm.getRequestURI());
        params.put("requestURL", wtm.getRequestURL());
        params.put("method", wtm.getMethod());
        params.put("port", wtm.getPort());
        params.put("contentType", wtm.getContentType());
        params.put("headers", wtm.getHeaders());
        params.put("paramsMap", wtm.getParamsMap());
        params.put("body", wtm.getBody());
        invocation.setRequest(new Object[] { params });
        invocation.setResponse(wtm.getResponse());
        invocation.setIdentity(identity);
    }

    /**
     * 是否命中需要采样的requestURI和采样的概率
     *
     * @param patterns
     *            patterns
     * @param requestUri
     *            requestUri
     * @return {@link boolean}
     */
    private boolean matchRequestUriAndSample(List<HttpRecordInterface> patterns, String requestUri) {
        // 系统还没启动成功，不录制
        if (!MOONBOX_CONTEXT.isStartEnd()) {
            return false;
        }
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        Pair<String, String> identityUri = getIdentityUri(requestUri);
        for (HttpRecordInterface pattern : patterns) {
            if (uriMatch(identityUri.getLeft(), pattern.getUri())) {
                int random = UriSampleRateRandomUtils.getRandom(pattern.getUniqueKey()).nextInt(10000);
                // 计算采样率
                if (random < Integer.parseInt(pattern.getSampleRate())) {
                    // 计算采集量是否达到限制
                    if (!MoonboxRecordIndicatorManager.getInstance().canRecord(identityUri.getRight())) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    log.info("sampled uri:{}", requestUri);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断uri是否匹配
     *
     * @param userUri
     * @param configUri
     * @return
     */
    private static boolean uriMatch(String userUri, String configUri) {
        if (userUri.matches(configUri)) {
            return true;
        }
        if (!StringUtils.contains(configUri, "{}")) {
            return false;
        }
        List<String> configSplit = Splitter.on("/").splitToList(configUri);
        List<String> uriSplit = Splitter.on("/").splitToList(userUri);

        if (CollectionUtils.size(configSplit) != CollectionUtils.size(uriSplit)) {
            return false;
        }
        for (int i = 0; i < configSplit.size(); i++) {
            if (!StringUtils.equals(configSplit.get(i), uriSplit.get(i))
                    && !StringUtils.equals(configSplit.get(i), "{}")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据http请求uri获取录制使用的uri标识
     *
     * @param requestUri
     *            requestUri
     * @return {@link Pair< String, String>}
     */
    private Pair<String, String> getIdentityUri(String requestUri) {
        if (StringUtils.isBlank(requestUri)) {
            return Pair.of(null, null);
        }
        String absUri = requestUri.endsWith("/") ? requestUri.substring(0, requestUri.length() - 1) : requestUri;
        return Pair.of(absUri, "http://" + absUri);
    }

    @Override
    protected boolean isEntranceFinish(Event event) {
        if (event.type == Event.Type.BEFORE) {
            return false;
        }
        if (Tracer.getContext() == null) {
            return true;
        }

        if (Tracer.getContext().getInvokeType() != invokeType) {
            return false;
        }

        RequestProcessDto processDto = REQUEST_PROCESS_DTO_THREAD_LOCAL.get();
        if (processDto == null) {
            log.warn("invalid request, no matched processDto found, traceId={}", Tracer.getTraceId());
            return true;
        }
        if (event instanceof InvokeEvent) {
            InvokeEvent invokeEvent = (InvokeEvent) event;
            if (processDto.shouldSkip(invokeEvent.invokeId)) {
                log.debug("event is not entrance finish, processId:{}, invokeId:{}", invokeEvent.processId,
                        invokeEvent.invokeId);
                return false;
            }
        }
        // impossible
        return true;
    }
}
