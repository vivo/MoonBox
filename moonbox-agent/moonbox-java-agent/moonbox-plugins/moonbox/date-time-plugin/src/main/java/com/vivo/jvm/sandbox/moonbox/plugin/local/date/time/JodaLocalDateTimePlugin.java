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
package com.vivo.jvm.sandbox.moonbox.plugin.local.date.time;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SysTimeUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.joda.time.LocalDateTime;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * JodaLocalDateTimePlugin
 */
@MetaInfServices(InvokePlugin.class)
public class JodaLocalDateTimePlugin extends AbstractInvokePluginAdapter {
    /**
     * java.time.LocalDateTime 暂时只增强 public static LocalDateTime now(Clock clock) 方法
     * 由于是系统中的方法（jdk 内置方法），因此需要设置 IsIncludeBootstrap 属性
     */
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern mp = EnhanceModel.MethodPattern.builder()
                .methodName("now")
                .build();
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("org.joda.time.LocalDateTime")
                .methodPatterns(new EnhanceModel.MethodPattern[]{mp})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected JodaLocalDateTimeListener getEventListener(InvocationListener listener) {
        return new JodaLocalDateTimeListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return null;
    }

    @Override
    public InvokeType getType() {
        return InvokeType.LOCAL_DATE_TIME;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return MoonboxContext.getInstance().isRepeatMode() && super.enable(config);
    }

    @Override
    public String identity() {
        return InvokeType.LOCAL_DATE_TIME.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    public static class JodaLocalDateTimeListener extends DefaultEventListener {

        public JodaLocalDateTimeListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
            super(invokeType, entrance, listener, processor);
        }

        /**
         * 事件是否可以通过
         * <p>
         * 降级之后只有回放流量可以通过
         *
         * @param event 事件
         * @return 是否通过
         */
        @Override
        protected boolean access(Event event) throws Exception {
            if (MoonboxContext.getInstance().isRepeatMode() && event instanceof BeforeEvent && MoonboxRepeatCache.isRepeatFlow()) {
                long time = SysTimeUtils.getTime();
                LocalDateTime dateTime = new LocalDateTime(time);
                ProcessControlException.throwReturnImmediately(dateTime);
            }
            return super.access(event);
        }
    }
}
