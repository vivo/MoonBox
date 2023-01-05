package com.vivo.jvm.sandbox.moonbox.plugin.shuffle;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import java.util.List;

/**
 * ShuffleInvocationProcessor - Shuffle
 */
public class ShuffleInvocationProcessor extends DefaultInvocationProcessor {

    public ShuffleInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return true;
    }

    @Override
    public Object assembleResponse(Event event) {
        //shuffle插件要认请求参数，这个方法没有返回值。但是在请求的时候不能及时序列化这个结果
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            return beforeEvent.argumentArray;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        List replayRequestList = (List) event.argumentArray[0];
        replayRequestList.clear();
        Object[] resp = (Object[]) invocation.getResponse();
        List respList = (List) resp[0];
        replayRequestList.addAll(respList);
        return null;
    }
}
