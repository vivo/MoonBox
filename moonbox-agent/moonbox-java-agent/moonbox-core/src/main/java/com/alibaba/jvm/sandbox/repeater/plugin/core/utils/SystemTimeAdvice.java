package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.List;

/**
 * SystemTimeAdvice
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/9 10:50 上午
 */
@Slf4j
public final class SystemTimeAdvice {

    private final ModuleEventWatcher watcher;

    private static final String JAVA_SYS_DATE_CLASS = "java.util.Date";

    private SystemTimeAdvice(ModuleEventWatcher watcher) {
        this.watcher = watcher;
    }

    public static SystemTimeAdvice watcher(ModuleEventWatcher watcher) {
        return new SystemTimeAdvice(watcher);
    }

    public synchronized void watch(List<String> sysMockTimeClasses, List<String> enablePlugins) {
        //对于java date time默认不勾选，除非用户选择了date时间插件。
        if (!enablePlugins.contains(InvokeType.LOCAL_DATE_TIME.getInvokeName())) {
            return;
        }
        if (!sysMockTimeClasses.contains(JAVA_SYS_DATE_CLASS)) {
            sysMockTimeClasses.add(JAVA_SYS_DATE_CLASS);
        }
        EventWatchBuilder watchBuilder = new EventWatchBuilder(watcher);
        for (int i = 0; i < sysMockTimeClasses.size() - 1; i++) {
            watchBuilder.onClass(sysMockTimeClasses.get(i)).includeBootstrap()
                    .onAnyBehavior();
        }
        Class systemTimeThreadHolder = null;
        try {
            ClassLoader loader = watcher.getClass().getClassLoader();
            //将时间标识传递给sandbox。主要sandbox基于GPL协议，本项目无法引入源码api修改采用这种方式将标识传递给sandbox修改用户指定的class类。具体实现可以查看vivo开源官网sandbox fork分支
            systemTimeThreadHolder = loader.loadClass("com.alibaba.jvm.sandbox.core.util.SystemTimeThreadHolder");
            MethodUtils.invokeStaticMethod(systemTimeThreadHolder, "setFlag", Boolean.TRUE);
            //有些项目存在这种获取时间方式：System.currentTimeMillis()，业务流程里面有和时间逻辑，回放录制时间相差太大导致失败，因此平台考虑对该类进行字节码替换保证录制和回放时间基本一致.具体实现可以查看vivo开源官网sandbox fork分支
            watchBuilder.onClass(sysMockTimeClasses.get(sysMockTimeClasses.size() - 1)).isIncludeBootstrap(true).onAnyBehavior().onWatch(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (systemTimeThreadHolder != null) {
                    MethodUtils.invokeStaticMethod(systemTimeThreadHolder,
                            "removeFlag");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }
}