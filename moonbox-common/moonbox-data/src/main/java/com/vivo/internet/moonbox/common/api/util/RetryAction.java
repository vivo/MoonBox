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
package com.vivo.internet.moonbox.common.api.util;

import lombok.extern.slf4j.Slf4j;

/**
 * RetryAction - 重試方法
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/10/10 17:45
 */
@Slf4j
public abstract class RetryAction<T> {

    private RetryAction() {
    }

    /**
     * 命名
     */
    private String actionName;

    /**
     * 构造方法
     *
     * @param actionName 命令名称
     */
    protected RetryAction(String actionName) {
        this.actionName = actionName;
    }

    /**
     * 等待200ms再执行execute()逻辑，若返回为空则接着重试，直到次数用尽或返回不为空
     *
     * @param times 重试次数
     * @return
     */
    public T retry(int times) {
        return retry(times, 200);
    }

    public T retry(int times, int interval) {
        T t = null;
        int count = 1;
        do {

            log.info("{} retry times : {}", this.actionName, count);

            try {
                t = execute();
                if (executeSuccess(t)) {
                    break;
                }
            }
            catch (Exception e) {
                log.error("{} retry execute error.", this.actionName, e);
            }
            finally {
                count++;
            }

            if (count <= times) {
                try {
                    Thread.sleep(interval);
                }
                catch (InterruptedException e) {
                    log.warn("{} Interrupted!", this.actionName, e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        while (count <= times);

        return t;
    }

    /**
     * @param
     * @return {@link T}
     */
    protected abstract T execute() throws Exception;

    /**
     * 判断执行成功的条件
     *
     * @param t
     * @return
     */
    protected boolean executeSuccess(T t) {
        return t != null;
    }
}
