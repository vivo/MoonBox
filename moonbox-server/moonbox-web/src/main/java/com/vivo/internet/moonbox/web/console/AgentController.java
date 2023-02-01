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
import com.vivo.internet.moonbox.service.console.ConsoleAgentService;
import com.vivo.internet.moonbox.service.console.vo.ActiveHostInfoVo;
import com.vivo.internet.moonbox.service.console.vo.AgentDetailVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户信息接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:10
 */
@RequestMapping("/api/console-agent")
@RestController
public class AgentController {

    @Resource
    private ConsoleAgentService consoleAgentService;

    /**
     * 上传文件
     *
     * @return 上传结果
     */
    @PostMapping(value = "fileUpload")
    public MoonBoxResult<Void> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("fileName") String fileName) throws Exception {
        consoleAgentService.uploadAgentFile(file, fileName);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 获取应用列表
     *
     * @return 应用列表
     */
    @GetMapping(value = "fileLists")
    public MoonBoxResult<List<AgentDetailVo>> getFileList() {
        return MoonBoxResult.createSuccess(consoleAgentService.getFileList());
    }

    /**
     * 获取活跃机器列表
     * @param taskRunId 任务id
     * @return 活跃机器列表
     */
    @GetMapping(value = "agentActiveHost")
    public MoonBoxResult<List<ActiveHostInfoVo>> agentActiveHost(@RequestParam("taskRunId") String taskRunId) {
        return MoonBoxResult.createSuccess(consoleAgentService.getActiveHostByTaskRunId(taskRunId));
    }
}
