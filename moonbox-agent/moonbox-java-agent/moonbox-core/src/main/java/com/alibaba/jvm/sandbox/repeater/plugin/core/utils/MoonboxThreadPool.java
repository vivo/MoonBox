package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * ThreadPool
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 3:20 下午
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoonboxThreadPool {

    public static ExecutorService MOONBOX_THREAD_POOL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1,
            4 * Runtime.getRuntime().availableProcessors(),
            60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(4096),
            new BasicThreadFactory.Builder().namingPattern("moonbox-thread-pool-%d").build(),
            new MoonboxPolicy());

    static class MoonboxPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                executor.getQueue().poll();
                executor.execute(r);
            }
        }
    }

}