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

import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.ReplayDiffConfigService;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffCreateReq;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffEditReq;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.ReplayDiffConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 回放对比配置接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 17:22
 */
@RequestMapping("/api/replay/diff")
@RestController
public class ReplayDiffController {

    @Resource
    private ReplayDiffConfigService replayDiffConfigService;

    /**
     * 获取回放配置列表
     *
     * @param pageRequest request
     * @return {@link MoonBoxResult<PageResult<ReplayDiffConfigVo>> } query result
     */
    @GetMapping(value = "list")
    public MoonBoxResult<PageResult<ReplayDiffConfigVo>> list(ReplayDiffConfigService.ReplayDiffConfigPageRequest pageRequest) {
        PageResult<ReplayDiffConfigVo>result =replayDiffConfigService.getReplayDiffConfigList(pageRequest);
        return MoonBoxResult.createSuccess(result);
    }

    /**
     * 创建回放配置
     *
     * @param createReq
     *            create request
     * @return {@link MoonBoxResult} runResult
     */
    @PostMapping(value = "create")
    public MoonBoxResult<PageResult<Void>> create(@RequestBody ReplayDiffCreateReq createReq) {
        createReq.setCreateUser(UserUtils.getLoginName());
        replayDiffConfigService.addDiffConfig(createReq);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 删除回放配置
     *
     * @param id pkId 主键id
     * @return {@link MoonBoxResult} delete result
     */
    @PostMapping(value = "delete")
    public MoonBoxResult<PageResult<Void>> delete(@RequestParam("id") Long id) {
        replayDiffConfigService.deleteConfig(id);
        return MoonBoxResult.createSuccess(null);
    }


    /**
     * 更新回放配置
     *
     * @param replayDiffEditReq
     *            update req
     * @return {@link MoonBoxResult} update result
     */
    @PostMapping(value = "update")
    public MoonBoxResult<PageResult<Void>> update(@RequestBody ReplayDiffEditReq replayDiffEditReq) {
        replayDiffConfigService.updateConfig(replayDiffEditReq);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 生成忽略路径
     * @param path 路径
     * @return {@link MoonBoxResult} runResult
     */
    @PostMapping(value = "guessUserWant")
    public MoonBoxResult<List<String>> guessUserWant(@RequestParam("path") String path) {
        if (StringUtils.isBlank(path)) {
            return MoonBoxResult.createSuccess(Collections.emptyList());
        }
        String tmpString = path;
        List<String> paths = Lists.newArrayList(path);
        while (true) {
            int pos = tmpString.lastIndexOf('.');
            if (pos < 0) {
                break;
            }
            tmpString = tmpString.substring(0, pos);
            paths.add(tmpString);
        }
        return MoonBoxResult.createSuccess(paths);
    }
}
