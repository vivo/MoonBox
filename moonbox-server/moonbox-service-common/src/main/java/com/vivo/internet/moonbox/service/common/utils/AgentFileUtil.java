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
