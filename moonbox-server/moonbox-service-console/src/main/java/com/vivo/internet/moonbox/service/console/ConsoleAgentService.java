package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.service.console.vo.ActiveHostInfoVo;
import com.vivo.internet.moonbox.service.console.vo.AgentDetailVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ConsoleAgentService - {@link ConsoleAgentService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/2 10:04
 */
public interface ConsoleAgentService {

    /**
     * 获取文件列表
     * @return 文件列表
     */
    List<AgentDetailVo> getFileList();

    /**
     * 上传agent文件
     * @param file 文件
     * @param fileName 文件名称
     * @throws IOException 异常
     */
    void uploadAgentFile(MultipartFile file,String fileName) throws IOException;


    /**
     * agent心跳列表
     * @param taskRunId 任务编码
     * @return agent状态
     */
    List<ActiveHostInfoVo> getActiveHostByTaskRunId(String taskRunId);
}
