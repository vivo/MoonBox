package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MotanInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.net.URL;
import java.util.Map;

/**
 * motan消费者的事件监听器
 *
 * @author dinglang
 */
public class MotanConsumerEventListener extends DefaultEventListener {

    public MotanConsumerEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
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
        MoonboxLogUtils.info("MotanConsumerEventListener initInvocation");
        MotanInvocation motanInvocation = null;
        // com.weibo.api.motan.rpc.AbstractReferer # call(Request request)
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
        //方法参数
        Object[] argumentArray = beforeEvent.argumentArray;
        //AbstractReferer实例
        Object referer = beforeEvent.target;
        if (argumentArray != null) {
            try {
                Object request = argumentArray[0];
                String interfaceName = (String)MethodUtils.invokeMethod(request,"getInterfaceName");
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
                Object url = MethodUtils.invokeMethod(referer,"getUrl");
                String protocol = (String) MethodUtils.invokeMethod(url,"getProtocol");
                String host = (String) MethodUtils.invokeMethod(url,"getHost");
                Integer port = (Integer) MethodUtils.invokeMethod(url,"getPort");
                String address = host+":"+port;
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

    @Override
    protected void initContext(Event event) {
        MoonboxLogUtils.info("MotanConsumerEventListener initContext");
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
        MoonboxLogUtils.info("MotanConsumerEventListener doBefore");
        if (event.javaClassName.equals("com.weibo.api.motan.rpc.AbstractReferer")
                && event.javaMethodName.contains("call")
                && !MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            MoonboxLogUtils.info("MotanConsumerEventListener,is not RepeatFlow ，exec doBefore");
            MotanInvocation motanInvocation = new MotanInvocation();
            motanInvocation.setStart(System.currentTimeMillis());
            int key = event.argumentArray[0].hashCode();
            MoonboxRecordCache.cacheInvocation(key, motanInvocation);
            //return;
        }
        super.doBefore(event);
    }

}
