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
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.ReplayMockClassService;
import com.vivo.internet.moonbox.service.console.model.MockClassAddReq;
import com.vivo.internet.moonbox.service.console.model.MockClassEditReq;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.ReplayMockClassVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 录制、回放mock配置
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 17:23
 */
@RequestMapping("/api/replay/mockClass")
@RestController
public class ReplayMockClassController {

    @Resource
    private ReplayMockClassService mockClassService;

    /**
     * 录制、回放 mock列表
     *
     * @param pageRequest query param
     * @return {@link MoonBoxResult} replay special list
     */
    @GetMapping(value = "list")
    public MoonBoxResult<PageResult<ReplayMockClassVo>> getMockClassList(ReplayMockClassService.MockClassPageRequest pageRequest) {
        PageResult<ReplayMockClassVo> mockClassVoPageResult =mockClassService.getReplayMockClassList(pageRequest);
        return MoonBoxResult.createSuccess(mockClassVoPageResult);
    }

    /**
     * 新增回放配置
     *
     * @param mockClassAddReq add request
     * @return {@link MoonBoxResult} replay special list
     */
    @PostMapping(value = "add")
    public MoonBoxResult<PageResult<Void>> addMockClass(@RequestBody MockClassAddReq mockClassAddReq) {
        mockClassAddReq.setCreateUser(UserUtils.getLoginName());
        mockClassService.addMockClass(mockClassAddReq);
        return MoonBoxResult.createSuccess(null);
    }


    /**
     * 更新回放配置
     *
     * @param mockClassEditReq edit request
     * @return {@link MoonBoxResult} mock result
     */
    @PostMapping(value = "edit")
    public MoonBoxResult<PageResult<Void>> editClassMock(@RequestBody MockClassEditReq mockClassEditReq) {
        mockClassEditReq.setUpdateUser(UserUtils.getLoginName());
        mockClassService.editMockClass(mockClassEditReq);
        return MoonBoxResult.createSuccess(null);
    }


    /**
     * 删除回放配置
     *
     * @param id 主键id
     * @return {@link MoonBoxResult} replay special list
     */
    @PostMapping(value = "delete")
    public MoonBoxResult<PageResult<Void>> deleteClassMock(@RequestParam("id") Long id) {
        mockClassService.deleteMockClass(id);
        return MoonBoxResult.createSuccess(null);
    }
}
