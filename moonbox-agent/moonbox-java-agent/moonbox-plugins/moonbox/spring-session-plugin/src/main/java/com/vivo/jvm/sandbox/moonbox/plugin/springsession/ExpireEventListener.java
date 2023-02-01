/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.springsession;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

public class ExpireEventListener extends DefaultEventListener {

    public ExpireEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    public void onEvent(Event event) throws Throwable {
        try {
            // 只在回放的时候生效
            if (Event.Type.BEFORE.equals(event.type) && MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
                doBefore((BeforeEvent) event);
            }
        }
        catch (ProcessControlException pe) {
            throw pe;
        }
        catch (Throwable throwable) {
            MoonboxLogUtils.error("[Error-0000]-uncaught exception occurred when dispatch spring session event,type={},event={}", invokeType, event, throwable);
            MoonboxContext.getInstance().exceptionOverflow(throwable);
        }
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        if (ExpirePlugin.NEX_CACHE_HANDLER.equals(event.javaClassName) && ExpirePlugin.METHOD_IS_EXPIRED.equals(event.javaMethodName)) {
            // 如果参数为空，则不用处理
            if (event.argumentArray != null && event.argumentArray[0] == null) {
                return ;
            }
        }

        if (ExpirePlugin.METHOD_IS_EXPIRED.equals(event.javaMethodName)) {
            ProcessControlException.throwReturnImmediately(false);
        }
        if (ExpirePlugin.METHOD_SESSION_SAVE.equals(event.javaMethodName)) {
            ProcessControlException.throwReturnImmediately(null);
        }
    }

}
