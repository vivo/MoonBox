/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.java;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MethodSignatureParser;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.JavaRecordInterface;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Java类型入口回放器；在sandbox两种挂载模式下工作条件不同（因为无法获取到运行实例）
 * <p>
 * agent启动 ：能够回放spring容器中的任何bean实例
 * <p>
 * attach启动：需要引入repeater-client并在spring中注入{@code SpringContextAware}
 * <p>
 * or
 * <p>
 * 兜底逻辑会使用{@link JavaInstanceCache} 进行实例获取
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class JavaRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!getType().equals(invocation.getType())) {
            throw new RepeatException("invoke type miss match, required invoke type is: " + invocation.getType());
        }
        Identity identity = invocation.getIdentity();
        String[] array = identity.getEndpoint().split("~");

        // array[0]=/methodName
        String methodName = getTargetMethod(array[0]);

        Object bean = getTargetBean(identity, methodName);

        ClassLoader classLoader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        // fix issue#9 int.class基本类型被解析成包装类型，通过java方法签名来规避这类问题
        // array[1]=javaMethodDesc
        // 这里需要兼容 没有参数的方法 的情况，例如 public void nonArgsMethod(){}，此时 array[1] 会抛出数组越界的情况
        Method method;
        if (array.length > 1) {
            MethodSignatureParser.MethodSpec methodSpec = MethodSignatureParser.parseIdentifier(array[1]);
            Class<?>[] parameterTypes = MethodSignatureParser.loadClass(methodSpec.getParamIdentifiers(), classLoader);
            method = bean.getClass().getDeclaredMethod(methodName, parameterTypes);
        } else {
            method = bean.getClass().getDeclaredMethod(methodName);
        }

        // 这里没法办像HTTP、DUBBO透传traceId，因此在执行前先执行Trace.start()，根据traceId创建好TraceContext，避免在
        Tracer.start(context.getTraceId());

        // 开始invoke
        return method.invoke(bean, invocation.getRequest());
    }

    /**
     * 获得即将回放的java方法
     */
    private String getTargetMethod(String methodName) {
        String method = methodName.substring(1);
        // 这里 method 可能为 method()，不知道是不是录制的地方有地方冗余了，这里先简单处理下
        int leftBracketIndex = method.indexOf("(");
        if (leftBracketIndex != -1) {
            method = method.substring(0, leftBracketIndex);
        }
        return method;
    }

    /**
     * 获得回放目标 bean
     */
    private Object getTargetBean(Identity identity, String methodName) throws Exception {
        Object bean = SpringContextAdapter.getBeanByType(identity.getLocation());

        if (bean == null) {
            bean = JavaInstanceCache.getInstance(identity.getLocation());
        }

        if (bean == null) {
            RepeaterConfig repeaterConfig = MoonboxContext.getInstance().getConfig();
            List<JavaRecordInterface> javaRecordInterfaces = repeaterConfig.getJavaRecordInterfaces();
            String contextMethod = null;
            for (JavaRecordInterface javaRecordInterface : javaRecordInterfaces) {
                if (javaRecordInterface.getClassPattern().contains(identity.getLocation())) {
                    String[] methodPatterns = javaRecordInterface.getMethodPatterns();
                    for (String methodPattern : methodPatterns) {
                        if (methodPattern.equals(methodName)) {
                            contextMethod = javaRecordInterface.getObtainApplicationContextMethod();
                            break;
                        }
                    }
                }
            }
            if (contextMethod != null) {
                int lastIndexOfDot = contextMethod.lastIndexOf(".");
                Class<?> applicationContext = ClassloaderBridge.instance().findClassInstance(contextMethod.substring(0, lastIndexOfDot));
                Object applicationContextObject = MethodUtils.invokeStaticMethod(applicationContext, contextMethod.substring(lastIndexOfDot + 1));
                Method getBeanMethod = applicationContextObject.getClass().getMethod("getBean", String.class);
                int lastDotIndex = identity.getLocation().lastIndexOf(".");
                String className = identity.getLocation().substring(lastDotIndex + 1);
                bean = getBeanMethod.invoke(applicationContextObject, StringUtils.uncapitalize(className));
            }
        }

        if (bean == null) {
            throw new RepeatException("no bean found in context, className=" + identity.getLocation());
        }

        return bean;
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA;
    }

    @Override
    public String identity() {
        return "java";
    }
}
