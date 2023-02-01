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

import lombok.Getter;

/**
 * ReplayStatus - {@link ReplayStatus}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 11:09
 */
@Getter
public enum  ReplayStatus {

    REPLAY_SUCCESS(0, "回放成功","回放成功"),

    SUB_INVOKE_DIFF_FAILED(1, "回放失败","子调用入参对比失败"),

    REPLAY_EX(2, "回放失败","回放执行异常"),

    SUB_INVOKE_NOT_FOUND(3, "回放失败","子调用未匹配"),

    RESULT_DIFF_FAILED(4, "回放失败","结果对比失败"),

    UNKNOWN_EX(5, "未知异常","回放出现未知异常");

    private final int code;

    private final String errorCode;

    private final String errorMessage;

    ReplayStatus(int code, String errorCode,String errorMessage) {
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ReplayStatus getReplayStatus(Integer type) {
        for (ReplayStatus selectEnum : ReplayStatus.values()) {
            if (selectEnum.code == type) {
                return selectEnum;
            }
        }
        return ReplayStatus.UNKNOWN_EX;
    }
}
