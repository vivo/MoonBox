package com.vivo.internet.moonbox.service.agent.config.service;

import javax.servlet.http.HttpServletResponse;

/**
 * AgentFileDownLoadService - {@link AgentFileDownLoadService}
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/29 20:44
 */
public interface AgentFileDownLoadService {

    /**
     * downLoadFile
     * @param fileName 文件名称
     * @param httpServletResponse httpServletResponse
     */
    void downLoadFile(HttpServletResponse httpServletResponse,String fileName);
}