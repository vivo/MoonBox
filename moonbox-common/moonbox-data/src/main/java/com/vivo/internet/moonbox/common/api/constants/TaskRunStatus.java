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
package com.vivo.internet.moonbox.common.api.constants;

/**
 * TaskRunStatus - {@link TaskRunStatus}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:47
 */
public enum  TaskRunStatus {

    /**
     * agent start run
     */
    START_RUN(2, "启动中"),

    /**
     * running
     */
    RUNNING(3, "运行中"),

    /**
     * stop
     */
    STOP_RUN(4, "停止"),

    /**
     * run failed
     */
    FAILED(6, "运行失败");

    private final int code;

    private final String message;

    TaskRunStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static TaskRunStatus getTaskRunStatus(Integer type) {
        for (TaskRunStatus taskRunStatus : TaskRunStatus.values()) {
            if (taskRunStatus.code == type) {
                return taskRunStatus;
            }
        }
        return TaskRunStatus.FAILED;
    }
}
