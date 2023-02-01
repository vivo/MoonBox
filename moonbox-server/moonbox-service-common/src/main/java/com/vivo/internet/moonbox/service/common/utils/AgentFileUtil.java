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
package com.vivo.internet.moonbox.service.common.utils;

import java.io.File;

/**
 * AgentFileUtil - {@link AgentFileUtil}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/2 15:02
 */
public class AgentFileUtil {

    private static final String AGENT_DOWNLOAD_BASE_DIR = System.getProperty("user.home") + File.separator + "moonbox-agent-download";

    public static String getFilePath(String fileName, String fileDigestAsHexString) {
        return AGENT_DOWNLOAD_BASE_DIR + File.separator + fileName + "-" + fileDigestAsHexString+".tar";
    }

    public static String getAgentDownloadBaseDir() {
        return AGENT_DOWNLOAD_BASE_DIR;
    }
}
