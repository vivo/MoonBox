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
