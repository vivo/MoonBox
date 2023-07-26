/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MotanInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.proxy.CommonClient;
import org.kohsuke.MetaInfServices;


/**
 * motan 的回放器
 * 通过泛化调用的方式在本节点回放
 * @author dinglang
 */
@MetaInfServices(Repeater.class)
public class MotanRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!(invocation instanceof MotanInvocation)) {
            throw new RepeatException("type miss match, required MotanInvocation but found " + invocation.getClass().getSimpleName());
        }
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        MotanInvocation motanInvocation = (MotanInvocation) invocation;
        try {
            //motan中没有类似Dubbo3中的ApplicationModel，可以尝试通过spring容器获取motan provider配置信息
            //Object bean = SpringContextAdapter.getBeanByType(BasicServiceConfigBean);

            RefererConfig<CommonClient> referer = new RefererConfig<CommonClient>();
            // 设置服务端接口
            referer.setInterface(CommonClient.class);
            referer.setServiceInterface(motanInvocation.getInterfaceName());

            // 配置服务的group以及版本号
            referer.setGroup(motanInvocation.getGroup());
            referer.setVersion(motanInvocation.getVersion());
            referer.setRequestTimeout(1000);
            referer.setAsyncInitConnection(false);

            // 配置注册中心直连调用
            RegistryConfig registry = new RegistryConfig();
            registry.setRegProtocol("direct");
            registry.setAddress(motanInvocation.getAddress());
            referer.setRegistry(registry);

            // 配置RPC协议
            ProtocolConfig protocol = new ProtocolConfig();
            protocol.setId(motanInvocation.getProtocol());
            protocol.setName(motanInvocation.getProtocol());
            referer.setProtocol(protocol);

            // 使用服务
            CommonClient client = referer.getRef();
            client.call(motanInvocation.getMethodName(), motanInvocation.getRequest(),Object.class);

            return MoonboxRepeatCache.getMotanResponse(context.getTraceId());
        } catch (Throwable e) {
            MoonboxLogUtils.error("motan回放失败", e);
            return null;
        } finally {
            MoonboxLogUtils.info("generate motan call end");
            MoonboxRepeatCache.removeDubboResponse(context.getTraceId());
            MoonboxRepeatCache.removeRepeatContext(context.getTraceId());
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MOTAN;
    }

    @Override
    public String identity() {
        return "motan";
    }
}
