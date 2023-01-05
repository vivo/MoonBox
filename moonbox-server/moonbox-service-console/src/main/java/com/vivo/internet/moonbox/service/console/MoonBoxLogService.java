package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;

import java.util.List;

/**
 * MoonBoxLogService - {@link MoonBoxLogService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/10/28 15:42
 */
public interface MoonBoxLogService {

    /**
     * get log list
     * @param taskRunId query request
     * @return page query result
     */
    List<MoonBoxLogVO> taskRunLogList(String taskRunId);


    /**
     * add task run log
     * @param moonBoxLogVO add request
     */
    void insertRunLog(MoonBoxLogVO moonBoxLogVO);
}
