package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;

/**
 * AgentDistributionService - Distribute agent to user's machine
 *
 * @author weiteng.xu
 * @version 1.0
 * @since 2022/8/19 2:53 下午
 */
public interface  AgentDistributionService {
     /**
      * 拉起agent
      * @param taskRunInfo 请求
      * @return 启动结果
      * @throws Exception
      */
     String startAgent(TaskRunInfoWithBLOBs taskRunInfo) throws Exception;
}
