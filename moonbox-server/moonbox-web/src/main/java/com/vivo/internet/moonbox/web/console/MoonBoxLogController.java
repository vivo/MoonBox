/**
 * Copyright:©1985-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
package com.vivo.internet.moonbox.web.console;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.service.console.MoonBoxLogService;
import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * LogController - 日志接口
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/10/20 11:30
 */
@RestController
@RequestMapping("/api/log")
public class MoonBoxLogController {

    @Resource
    private MoonBoxLogService moonBoxLogService;

    @GetMapping(value = "list")
    public MoonBoxResult<List<MoonBoxLogVO>> getLogList(@RequestParam("taskRunId")String taskRunId) {
        List<MoonBoxLogVO>moonBoxLogVOS = moonBoxLogService.taskRunLogList(taskRunId);
        return MoonBoxResult.createSuccess(moonBoxLogVOS);
    }
}
