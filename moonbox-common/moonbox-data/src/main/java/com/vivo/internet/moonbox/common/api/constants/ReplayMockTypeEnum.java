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
 * ReplayMockTypeEnum - {@link ReplayMockTypeEnum}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 16:05
 */
public enum ReplayMockTypeEnum {
    TIME_MOCK_CLASSES(1, "System.currentTimeMillis()时间回放mock"),
    UNIVERSAL_MOCK_CLASSES(2, "java类方法回放Mock");
    private int code;
    private String desc;

    ReplayMockTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ReplayMockTypeEnum getReplayMockTypeEnum(Integer type) {
        for (ReplayMockTypeEnum mockTypeEnum : ReplayMockTypeEnum.values()) {
            if (mockTypeEnum.code == type) {
                return mockTypeEnum;
            }
        }
        return null;
    }
}