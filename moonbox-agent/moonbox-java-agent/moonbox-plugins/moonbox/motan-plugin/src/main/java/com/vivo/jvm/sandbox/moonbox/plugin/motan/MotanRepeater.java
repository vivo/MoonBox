/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
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
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.RpcContext;
import com.weibo.api.motan.util.MotanClientUtil;
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

        MotanInvocation motanInvocation = (MotanInvocation) invocation;

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
        //这里是回放场景，直接配置成请求本机的方式
        //registry.setAddress(motanInvocation.getAddress());
        registry.setAddress("127.0.0.1:" + motanInvocation.getPort());
        referer.setRegistry(registry);

        // 配置RPC协议
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setId(motanInvocation.getProtocol());
        protocol.setName(motanInvocation.getProtocol());
        referer.setProtocol(protocol);


        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            //切换classloader，否则可能会造成Motan SPI加载失败
            Thread.currentThread().setContextClassLoader(CommonClient.class.getClassLoader());

            // 回放流量时会跨线程，所以需要将traceId传递过来，通过rpcContext的方式来传递
            RpcContext rpcContext = RpcContext.getContext();
            rpcContext.setRpcAttachment(Constants.HEADER_TRACE_ID_X, context.getTraceId());

            // 使用泛化调用方式直接回放流量
            CommonClient client = referer.getRef();
            Request request = MotanClientUtil.buildRequest(motanInvocation.getInterfaceName(), motanInvocation.getMethodName(), motanInvocation.getParamtersDesc(), motanInvocation.getParameters(), null);
            return client.call(request, Object.class);

        } catch (Throwable e) {
            MoonboxLogUtils.error("motan回放失败", e);
        } finally {
            MoonboxLogUtils.info("generate motan call end");
            MoonboxRepeatCache.removeMotanResponse(context.getTraceId());
            MoonboxRepeatCache.removeRepeatContext(context.getTraceId());
            Thread.currentThread().setContextClassLoader(swap);
        }

        return null;
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
