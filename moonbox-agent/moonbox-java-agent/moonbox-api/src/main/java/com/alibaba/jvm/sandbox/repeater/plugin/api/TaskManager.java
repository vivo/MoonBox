package com.alibaba.jvm.sandbox.repeater.plugin.api;

/**
 * Task - 任务接口
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 10:28 上午
 */
public interface TaskManager {

    void init();

    void start();

    void shutdown();
}