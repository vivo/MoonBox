/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone;

import java.io.File;
import java.io.IOException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.PathUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.RecordModel;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link StandaloneBroadcaster} 能够脱机工作，不依赖服务端的实现
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public class StandaloneBroadcaster extends AbstractBroadcaster {

    private static final String RECORD_SUFFIX = "record";

    private static final String REPEAT_SUFFIX = "repeat";

    @Override
    protected void broadcastRecord(RecordModel recordModel) {
        try {
            String body = SerializerWrapper.hessianSerialize(recordModel);
            this.broadcast(body, recordModel.getTraceId(), RECORD_SUFFIX);
            log.info("broadcast success,traceId={},resp={}", recordModel.getTraceId(), "success");
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    protected void broadcastRepeat(RepeatModel recordModel) {
        try {
            String body = SerializerWrapper.hessianSerialize(recordModel);
            this.broadcast(body, recordModel.getRepeatId(), REPEAT_SUFFIX);
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    public RepeaterResult<RecordModel> pullRecord(RepeatMeta meta) {
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            String record = FileUtils.readFileToString(new File(assembleFileName(meta.getTraceId(), RECORD_SUFFIX)), "UTF-8");
            Thread.currentThread().setContextClassLoader(DefaultBroadcaster.class.getClassLoader());
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(record, RecordWrapper.class);
            if (meta.isMock() && CollectionUtils.isNotEmpty(wrapper.getSubInvocations())) {
                for (Invocation invocation : wrapper.getSubInvocations()) {
                    SerializerWrapper.inTimeDeserialize(invocation);
                }
            }
            SerializerWrapper.inTimeDeserialize(wrapper.getEntranceInvocation());
            return RepeaterResult.builder().success(true).message("operate success").data(wrapper.reTransform()).build();
        } catch (Throwable e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        } finally {
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    private void broadcast(String body, String name, String folder) throws IOException {
        FileUtils.writeStringToFile(new File(this.assembleFileName(name, folder)), body, "UTF-8");
    }

    private String assembleFileName(String name, String folder) {
        return PathUtils.getModulePath() + File.separator + "repeater-data" + File.separator + folder + File.separator + name;
    }

}