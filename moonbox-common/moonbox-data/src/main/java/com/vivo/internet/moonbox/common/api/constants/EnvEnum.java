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
 * EnvEnum - {@link EnvEnum}
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/11/17 16:16
 */
public enum EnvEnum {

    LOCAL("local"),

    //指定是哪些环境流量
    DEV("dev"),

    UNKNOWN("unknown");

    private final String env;

    EnvEnum(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }

    /**
     *
     * @param envName env
     * @return env
     */
    public static EnvEnum getEnvType(String envName) {
        for (EnvEnum env : EnvEnum.values()) {
            if (env.getEnv().equalsIgnoreCase(envName)) {
                return env;
            }
        }
        return EnvEnum.UNKNOWN;
    }
}
