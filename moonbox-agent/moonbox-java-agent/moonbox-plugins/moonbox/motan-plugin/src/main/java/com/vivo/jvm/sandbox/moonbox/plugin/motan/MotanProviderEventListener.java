package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.MoonboxRecordIndicatorManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.UriSampleRateRandomUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MotanInvocation;
import com.vivo.internet.moonbox.common.api.model.DubboRecordInterface;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.MotanRecordInterface;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.List;
import java.util.Map;

/**
 * motan提供者
 */
public class MotanProviderEventListener extends DefaultEventListener {

    public MotanProviderEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    /**
     * 初始化调用器
     * doBefore 的时候会被调用
     * @param beforeEvent
     *            before事件
     * @return
     */
    @Override
    protected Invocation initInvocation(BeforeEvent beforeEvent) {
        MoonboxLogUtils.info("MotanProviderEventListener initInvocation");
        //com.weibo.api.motan.transport.ProviderMessageRouter#call(Request request, Provider<?> provider)
        MotanInvocation motanInvocation = null;
        Object[] argumentArray = beforeEvent.argumentArray;

        int key = beforeEvent.argumentArray[0].hashCode();
        Invocation cachedInvocation = MoonboxRecordCache.getInvocationIfPresent(key);
        if (cachedInvocation == null) {
            MoonboxLogUtils.warn("no valid cachedInvocation found in motan consumer  doBefore,type={},traceId={}",
                    invokeType, Tracer.getTraceId());
        } else if (!(cachedInvocation instanceof MotanInvocation)) {
            MoonboxLogUtils.warn(
                    "cachedInvocation found in motan consumer  doBefore is not a MotanInvocation,type={},traceId={}",
                    invokeType, Tracer.getTraceId());
        } else {
            motanInvocation = (MotanInvocation) cachedInvocation;
        }
        if (argumentArray != null) {
            try {
                Object request = argumentArray[0];
                Object provider = argumentArray[1];
                String interfaceName = (String) MethodUtils.invokeMethod(request,"getInterfaceName");
                String methodName = (String) MethodUtils.invokeMethod(request, "getMethodName");
                motanInvocation.setInterfaceName(interfaceName);
                motanInvocation.setMethodName(methodName);
                //请求参数和参数类型描述
                Object[] parameters = (Object[]) MethodUtils.invokeMethod(request, "getArguments");
                String paramtersDesc = (String) MethodUtils.invokeMethod(request, "getParamtersDesc");
                motanInvocation.setParameters(parameters);
                motanInvocation.setParamtersDesc(paramtersDesc);
                //从 AbstractReferer对象中获取URL对象 (或者可以读取serviceUrl字段)
                //motan://172.22.19.73:8002/com.weibo.motan.demo.service.MotanDemoService?group=motan-demo-rpc
                Object url = MethodUtils.invokeMethod(provider,"getUrl");
                String protocol = (String) MethodUtils.invokeMethod(url,"getProtocol");
                String host = (String) MethodUtils.invokeMethod(url,"getHost");
                Integer port = (Integer) MethodUtils.invokeMethod(url,"getPort");
                String address = host + ":" + port;
                motanInvocation.setProtocol(protocol);
                motanInvocation.setHost(host);
                motanInvocation.setPort(port);
                motanInvocation.setAddress(address);
                //从URL中获取扩展参数
                Map<String, String> parametersMap = (Map<String, String>)MethodUtils.invokeMethod(url,"getParameters");
                String group = parametersMap.get("getGroup");
                String version =  parametersMap.get("getVersion");
                motanInvocation.setGroup(group);
                motanInvocation.setVersion(version);
            }catch (Exception ex){
                MoonboxLogUtils.error("error occurred when  init motan invocation", ex);
            }
        }
        return motanInvocation;
    }

    /**
     * 初始化上下文信息
     * 在DefaultEventListener#onEvent() 刚执行时被处理
     * @param event
     */
    @Override
    protected void initContext(Event event) {
        MoonboxLogUtils.info("MotanProviderEventListener initContext");
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            if (entrance) {
                Tracer.end();
            }
            Object request = beforeEvent.argumentArray[0];
            try {
                // 回放流量时会跨线程，所以需要将traceId传递过来，通过rpcContext的方式来传递
                Map<String, String> attachments = (Map<String, String>) MethodUtils.invokeMethod(request,
                        "getAttachments");
                // 如果存在traceIdX，则是回放流量
                String traceIdX = attachments.get(Constants.HEADER_TRACE_ID_X);
                if (TraceGenerator.isValid(traceIdX)) {
                    // 走到这里代表是回放流量
                    Tracer.start(traceIdX);
                } else {
                    // 走到这里代表是录制流量
                    super.initContext(event);
                }
            } catch (Exception e) {
                MoonboxLogUtils.error("initContext occur exception", e);
            }
        }
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        MoonboxLogUtils.info("MotanProviderEventListener doBefore");
        if (event.javaClassName.equals("com.weibo.api.motan.transport.ProviderMessageRouter")
                && event.javaMethodName.contains("call")
                && !MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            MoonboxLogUtils.info("MotanProviderEventListener,is not RepeatFlow ，exec doBefore");
            MotanInvocation motanInvocation = new MotanInvocation();
            motanInvocation.setRequest(processor.assembleRequest(event));
            try {
                SerializerWrapper.inTimeSerialize(motanInvocation);
            } catch (SerializeException e) {
                MoonboxLogUtils.error("MotanProviderEventListener exec doBefore serialize error",e);
            }
            motanInvocation.setStart(System.currentTimeMillis());
            int key = event.argumentArray[0].hashCode();
            MoonboxRecordCache.cacheInvocation(key, motanInvocation);
            //return;
        }
        super.doBefore(event);
    }

    /**
     * 采样比率
     * @param event
     * @return
     */
    @Override
    protected boolean sample(Event event) {
        // 如果是录制流量而且是入口请求，则在BeforeEvent触发时进行采样率计算
        if (!MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId()) && entrance && event instanceof BeforeEvent) {
            try {
                BeforeEvent beforeEvent = (BeforeEvent) event;
                String javaMethodName = beforeEvent.javaMethodName;
                if (("call").equals(javaMethodName)) {
                    // 系统还没启动成功，不录制
                    if (!MoonboxContext.getInstance().isStartEnd()) {
                        return false;
                    }
                    List<MotanRecordInterface> patterns = MoonboxContext.getInstance().getConfig()
                            .getMotanEntrancePatterns();
                    //com.weibo.api.motan.transport.ProviderMessageRouter#call(Request request, Provider<?> provider)
                    Object[] argumentArray = beforeEvent.argumentArray;
                    Object request = argumentArray[0];
                    Object provider = argumentArray[1];
                    String interfaceName = (String) MethodUtils.invokeMethod(request,"getInterfaceName");
                    String methodName = (String) MethodUtils.invokeMethod(request, "getMethodName");
                    // 接口匹配，采样率计算
                    if (!matchRequestUriAndSample(patterns, interfaceName, methodName)) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    // 计算客户端是否采集达到了数量限制
                    String uri = processor.assembleIdentity((BeforeEvent) event).getUri();
                    MoonboxRecordIndicatorManager.getInstance().setRecordingUri(uri);
                    Tracer.getContext().setSampled(true);
                    Tracer.getContext().setRecordEntranceUri(uri);
                    return true;
                }
            }catch (Exception exception){
                MoonboxLogUtils.error("MotanProviderEventListener exec sample error",exception);
            }
        }
        //回放的流量直接跳过
        return super.sample(event);
    }

    private boolean matchRequestUriAndSample(List<MotanRecordInterface> patterns, String interfaceName,
                                             String methodName) {
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (MotanRecordInterface pattern : patterns) {
            if (interfaceName.matches(pattern.getInterfaceName()) && methodName.matches(pattern.getMethodName())) {
                int random = UriSampleRateRandomUtils.getRandom(pattern.getUniqueKey()).nextInt(10000);
                // 采样判断
                if (random < Integer.parseInt(pattern.getSampleRate())) {
                    // 数量判断
                    if (!MoonboxRecordIndicatorManager.getInstance().canRecord(pattern.getUniqueKey())) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
