/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.MethodDescriptor;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.apache.dubbo.rpc.service.GenericService;
import org.kohsuke.MetaInfServices;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;


/**
 * {@link DubboRepeater} dubbo回放器
 * <p>
 *
 * @author zhaoyb1990
 *
 *Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@MetaInfServices(Repeater.class)
public class DubboRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!(invocation instanceof DubboInvocation)) {
            throw new RepeatException("type miss match, required DubboInvocation but found " + invocation.getClass().getSimpleName());
        }
        DubboInvocation dubboInvocation = (DubboInvocation) invocation;
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("common-comment-admin");
        // require address to initialize registry config
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");

        String version = "1.0.0";
        Integer port = 20880;
        String zk = "";
        String group = "";
        String[] parameterTypes = null;

        List<ProviderModel> providerModels = Lists.newArrayList();
        List<ProviderModel> providerModels1 = Lists.newArrayList();


        Collection<ProviderModel> providerConfigs = ApplicationModel.allProviderModels();
        for (ProviderModel providerModel : providerConfigs) {
            String[] array = providerModel.getServiceName().split(":");
            if (array[0].contains(dubboInvocation.getInterfaceName())) {
                for (MethodDescriptor methodDescriptor : providerModel.getAllMethods()) {
                    if (dubboInvocation.getMethodName().equals(methodDescriptor.getMethodName())) {
                        if (Arrays.equals(dubboInvocation.getParameterTypes(), parameterTypes)) {
                            parameterTypes = transformClass(methodDescriptor.getParameterClasses());
                            providerModels.add(providerModel);
                            continue;
                        }
                        if (typeEquals(dubboInvocation.getParameterTypes(), parameterTypes)) {
                            parameterTypes = transformClass(methodDescriptor.getParameterClasses());
                            providerModels.add(providerModel);
                            continue;
                        }
                        providerModels1.add(providerModel);
                    }
                }
            }
        }

        //如果实在找不到最合适的那就直接默认匹配一个dubbo
        if (providerModels.size() == 0) {
            providerModels.addAll(providerModels1);
        }

        ProviderModel selectProviderModel = null;
        for (ProviderModel providerModel : providerModels) {
            version = getVersion(providerModel);
            port = getPort(providerModel);
            zk = getRegistryHost(providerModel);
            group = getGroup(providerModel);
            selectProviderModel = providerModel;
            //有些系统会存在两个版本，但是group不同，兼容下
            if (dubboInvocation.getGroup() != null && dubboInvocation.getGroup().equalsIgnoreCase(group)) {
                break;
            }
        }

        for (MethodDescriptor methodDescriptor : selectProviderModel.getAllMethods()) {
            if (dubboInvocation.getMethodName().equals(methodDescriptor.getMethodName())) {
                parameterTypes = transformClass(methodDescriptor.getParameterClasses());
                if (Arrays.equals(dubboInvocation.getParameterTypes(), parameterTypes)) {
                    break;
                }
                if (typeEquals(dubboInvocation.getParameterTypes(), parameterTypes)) {
                    break;
                }

            }
        }

        // using special address
        registryConfig.setAddress(zk);
        reference.setRegistry(registryConfig);
        reference.setVersion(version);
        // 指定某个服务提供方
        String url = "dubbo://localhost:" + port;
        reference.setUrl(url);

        // set protocol / interface / version / timeout
        reference.setProtocol(dubboInvocation.getProtocol());
        reference.setInterface(dubboInvocation.getInterfaceName());
        if (StringUtils.isNotBlank(group)) {
            reference.setGroup(group);
        }
        MoonboxLogUtils.info("generaic dubbo call, url: {}, group: {}", url, group);
        reference.setCheck(false);
        // timeout
        reference.setTimeout(context.getMeta().getTimeout());
        // use generic invoke
        reference.setGeneric(true);
        // fix issue #45
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(GenericService.class.getClassLoader());

            // 回放流量时会跨线程，所以需要将traceId传递过来，通过rpcContext的方式来传递
            RpcContext rpcContext = RpcContext.getContext();
            rpcContext.setAttachment(Constants.HEADER_TRACE_ID_X, context.getTraceId());

            GenericService genericService = reference.get();
            MoonboxLogUtils.info("generaic dubbo call, record  parameterTypes: {},current parameterTypes:{}", JSON.toJSONString(dubboInvocation.getParameterTypes()), JSON.toJSONString(parameterTypes));

            genericService.$invoke(dubboInvocation.getMethodName(), parameterTypes, invocation.getRequest());
            return MoonboxRepeatCache.getDubboResponse(context.getTraceId());
        } finally {
            MoonboxLogUtils.info("generate dubbo call end");
            MoonboxRepeatCache.removeDubboResponse(context.getTraceId());
            MoonboxRepeatCache.removeRepeatContext(context.getTraceId());
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    @Override
    public InvokeType getType() {
        return InvokeType.DUBBO;
    }

    @Override
    public String identity() {
        return "dubbo";
    }

    private int getPort(ProviderModel providerModel) {
        int port = 0;
        try {
            ProviderModel.RegisterStatedURL registerStatedURL = providerModel.getStatedUrl().get(0);
            port = registerStatedURL.getProviderUrl().getPort();
        } catch (Throwable t) {
            MoonboxLogUtils.error(t.getMessage());
        }
        if (port != 0) {
            return port;
        }

        try {
            Class c = Class.forName("org.apache.dubbo.registry.support.ProviderConsumerRegTable");
            Field f = c.getField("providerInvokers");
            Map map = (Map) f.get(c);
            Iterator it = map.values().iterator();
            if (it.hasNext()) {
                Map providerWrapperMap = (Map) it.next();
                Object providerWrapper = providerWrapperMap.values().iterator().next();
                // 反射拿到method
                Class providerInvokeWrapperClass = Class.forName("org.apache.dubbo.registry.support.ProviderInvokerWrapper");
                Method getProviderUrlMethod = providerInvokeWrapperClass.getMethod("getProviderUrl");
                URL providerUrl = (URL) getProviderUrlMethod.invoke(providerWrapper);
                port = providerUrl.getPort();
            }
        } catch (Throwable e) {
            MoonboxLogUtils.error("老版本dubbo获取dubbo信息报错");
            port = 20880;
        }
        return port;
    }

    private String getRegistryHost(ProviderModel providerModel) {
        try {
            return providerModel.getServiceConfig().getRegistry().getAddress();
        } catch (Throwable t) {
            MoonboxLogUtils.error(t.getMessage());
        }

        try {
            Class c = Class.forName("org.apache.dubbo.registry.support.ProviderConsumerRegTable");
            Field f = c.getField("providerInvokers");
            Map map = (Map) f.get(c);
            Iterator it = map.values().iterator();
            if (it.hasNext()) {
                Map providerWrapperMap = (Map) it.next();
                Object providerWrapper = providerWrapperMap.values().iterator().next();
                // 反射拿到method
                Class providerInvokeWrapperClass = Class.forName("org.apache.dubbo.registry.support.ProviderInvokerWrapper");
                Method getRegistryUrlMethod = providerInvokeWrapperClass.getMethod("getRegistryUrl");
                URL registryUrl = (URL) getRegistryUrlMethod.invoke(providerWrapper);
                return registryUrl.getHost();
            }
        } catch (Throwable t) {
            MoonboxLogUtils.error(t.getMessage());
        }
        throw new RuntimeException("getRegistryHost failed");
    }

    private String getGroup(ProviderModel providerModel) {
        String group = null;
        try {
            group = providerModel.getServiceMetadata().getGroup();
        } catch (Throwable t) {
            MoonboxLogUtils.error(t.getMessage());
        }
        return group;
    }

    private String getVersion(ProviderModel providerModel) {
        String version = "1.0.0";
        try {
            version = providerModel.getServiceMetadata().getVersion();
        } catch (Throwable t) {
            MoonboxLogUtils.error(t.getMessage());
        }
        return version;
    }

    private String[] transformClass(Class<?>[] parameterTypes) {
        List<String> paramTypes = Lists.newArrayList();
        if (ArrayUtils.isNotEmpty(parameterTypes)) {
            for (Class<?> clazz : parameterTypes) {
                paramTypes.add(clazz.getCanonicalName());
            }
        }
        return paramTypes.toArray(new String[0]);
    }


    private boolean typeEquals(String[] types1s, String[] types2s) {
        if (Arrays.equals(types1s, types2s)) {
            return true;
        }
        if (types1s == null || types2s == null) {
            return false;
        }
        if (types1s.length != types2s.length) {
            return false;
        }
        for (int i = 0; i < types1s.length; i++) {
            String type1 = types1s[i];
            String type2 = types2s[i];
            if (StringUtils.equalsIgnoreCase(type1, type2)) {
                continue;
            }
            if (type1 != null && type2 != null) {
                if (type1.contains("java.util") && type2.contains("java.util")) {
                    try {
                        Class type1Class = Class.forName(type1);
                        Class type2Class = Class.forName(type2);
                        boolean ret = (type1Class == type2Class) || type1Class.isAssignableFrom(type2Class) || type2Class.isAssignableFrom(type1Class);
                        if (!ret) {
                            return false;
                        }
                        continue;
                    } catch (Exception e) {
                    }
                }
            }
            return false;
        }
        return true;
    }
}
