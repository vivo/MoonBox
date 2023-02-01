/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.vivo.internet.moonbox.common.api.model.RecordModel;

/**
 * {@link RepeatInterceptor} 回放器执行中的拦截器；给用户自定义回放过程干预的机会
 * <p>
 *
 * @author zhaoyb1990
 */
public interface RepeatInterceptor {

    /**
     * 开启回放之前
     *
     * @param recordModel 录制记录
     */
    void beforeInvoke(RecordModel recordModel);

    /**
     * 返回回放结果之前
     *
     * @param recordModel   录制记录
     * @param response 调用结果
     */
    void beforeReturn(RecordModel recordModel, Object response);

    /**
     * 是否符合invoke拦截条件
     *
     * @param recordModel 录制记录
     * @return 是否符合
     */
    boolean matchingInvoke(RecordModel recordModel);

    /**
     * 是否符合return拦截条件
     *
     * @param recordModel   录制记录
     * @param response 调用结果
     * @return 是否符合
     */
    boolean matchingReturn(RecordModel recordModel, Object response);
}
