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
import com.vivo.internet.moonbox.service.console.RecordTemplateService;
import com.vivo.internet.moonbox.service.console.model.ConsolePageRequest;
import com.vivo.internet.moonbox.service.console.model.TemplateCreateReq;
import com.vivo.internet.moonbox.service.console.model.TemplateUpdateReq;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateDetailVo;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 录制模板接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:02
 */
@RequestMapping("/api/record/templateManage")
@RestController
public class RecordTemplateController {

    @Resource
    private RecordTemplateService templateService;

    /**
     * 创建录制模板
     *
     * @param templateCreateReq recordTemplate
     * @return {@link MoonBoxResult<Void>} recordTemplate create result
     */
    @PostMapping(value = "create")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<Void> createRecordTemplate(@RequestBody TemplateCreateReq templateCreateReq) {
        templateCreateReq.setCreateUser(UserUtils.getLoginName());
        templateService.createRecordTemplate(templateCreateReq);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 分页查询录制模板列表
     *
     * @param pageRequest query param
     * @return {@link MoonBoxResult<PageResult<RecordTemplateVo>>} record count list
     */
    @GetMapping(value = "list")
    public MoonBoxResult<PageResult<RecordTemplateVo>> getTemplateList(ConsolePageRequest pageRequest) {
        PageResult<RecordTemplateVo> pageResult = templateService.getTemplateList(pageRequest);
        return MoonBoxResult.createSuccess(pageResult);
    }

    /**
     * 更新录制模板详情
     *
     * @param updateTemplate 更新参数
     * @return {@link MoonBoxResult<Void>} 更新结果
     */
    @PostMapping(value = "update")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<Void> updateTemplate(@RequestBody TemplateUpdateReq updateTemplate) {
        updateTemplate.setUpdateUser(UserUtils.getLoginName());
        templateService.updateRecordTemplate(updateTemplate);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 查询录制模板详情
     *
     * @param pkId 主键id
     * @return 录制模板详情
     */
    @GetMapping(value = "detail")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<RecordTemplateDetailVo> templateDetail(@RequestParam("id") Long pkId) {
        RecordTemplateDetailVo detailVo = templateService.getDetailByPkId(pkId);
        return MoonBoxResult.createSuccess(detailVo);
    }

    /**
     * 删除录制模板信息
     *
     * @param pkId 主键id
     * @return {@link MoonBoxResult<Void>} 删除结果
     */
    @PostMapping(value = "delete")
    @SuppressWarnings("unchecked")
    public MoonBoxResult<Void> deleteTemplate(@RequestParam("id") Long pkId) {
        templateService.deleteDetail(pkId);
        return MoonBoxResult.createSuccess(null);
    }

    /**
     * 获取任务支持的子调用类型
     *
     * @return 子调用列表
     */
    @GetMapping(value = "supportInvokeTypes")
    public MoonBoxResult<List<Map<String, Object>>> invokeTypes(@RequestParam(value = "templateId", required = false) String templateId) {
        return MoonBoxResult.createSuccess(templateService.getPluginsByTemplateId(templateId));
    }
}