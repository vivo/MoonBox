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
package com.vivo.internet.moonbox.service.common.constants;

/**
 * CommonConstants - Common Constants
 *
 * @author weiteng.xu
 * @version 1.0
 * @since 2022/8/19 2:48 下午
 */
public class CommonConstants {
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String USER_CODE = "password";
    public static final String TIMEOUT = "timeout";

    public static final String DEFAULT_SFTP_HOST = "127.0.0.1";
    public static final String DEFAULT_SFTP_PORT = "22";
    // default: 60 seconds
    public static final String DEFAULT_SFTP_TIMEOUT = "60000";
    public static final String DEFAULT_SANDBOX_FILE_NAME = "sandbox-agent";
    public static final String DEFAULT_MOONBOX_FILE_NAME = "moonbox-agent";
}
