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
package com.vivo.internet.moonbox.service.console.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.vivo.internet.moonbox.common.api.constants.DeleteStatus;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.InvokeIdentity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateExample;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import com.vivo.internet.moonbox.dal.mapper.RecordTaskTemplateMapper;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.console.RecordTemplateService;
import com.vivo.internet.moonbox.service.console.model.ConsolePageRequest;
import com.vivo.internet.moonbox.service.console.model.TemplateCreateReq;
import com.vivo.internet.moonbox.service.console.model.TemplateUpdateReq;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateDetailVo;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * RecordTemplateServiceImpl - {@link RecordTemplateServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 19:29
 */
@Service
public class RecordTemplateServiceImpl implements RecordTemplateService {

    @Resource
    private RecordTaskTemplateMapper templateMapper;

    @Override
    @SuppressWarnings("unchecked")
    public void createRecordTemplate(TemplateCreateReq templateCreateReq) {
        CheckerSupported.getInstance().check(templateCreateReq);
        templateMapper.insertSelective((RecordTaskTemplateWithBLOBs) ConverterSupported.getInstance().convert(templateCreateReq, RecordTaskTemplateWithBLOBs.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateRecordTemplate(TemplateUpdateReq updateReqReq) {
        CheckerSupported.getInstance().check(updateReqReq);
        templateMapper.updateByPrimaryKeySelective((RecordTaskTemplateWithBLOBs) ConverterSupported.getInstance().convert(updateReqReq, RecordTaskTemplateWithBLOBs.class));
    }

    @Override
    public PageResult<RecordTemplateVo> getTemplateList(ConsolePageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        RecordTaskTemplateExample templateExample = new RecordTaskTemplateExample();
        RecordTaskTemplateExample.Criteria criteria = templateExample.createCriteria()
                .andDeleteStateEqualTo(DeleteStatus.EXIST.getStatus());

        if(StringUtils.isNotBlank(pageRequest.getAppName())){
            criteria.andAppNameEqualTo(pageRequest.getAppName());
        }

        if (StringUtils.isNotBlank(pageRequest.getCondition())) {
            criteria.andTemplateIdLike("%" + pageRequest.getCondition() + "%");
        }
        templateExample.setOrderByClause("update_time desc");

        Page<RecordTaskTemplateWithBLOBs> pageResult = (Page<RecordTaskTemplateWithBLOBs>) templateMapper
                .selectByExampleWithBLOBs(templateExample);

        List<RecordTemplateVo> recordTemplateVoList = pageResult.getResult().stream()
                .map(dbTemplate -> {
                    RecordTemplateVo templateVo = new RecordTemplateVo();
                    BeanUtils.copyProperties(dbTemplate, templateVo);
                    return templateVo;
                })
                .collect(Collectors.toList());
        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(), recordTemplateVoList);
    }

    @Override
    public RecordTemplateDetailVo getDetailByPkId(Long pkId) {
        RecordTaskTemplateWithBLOBs template = templateMapper.selectByPrimaryKey(pkId);
        if (template == null) {
            return null;
        }
        RecordTemplateDetailVo detailVo = new RecordTemplateDetailVo();
        BeanUtils.copyProperties(template, detailVo);
        return detailVo;
    }

    @Override
    public RecordTemplateDetailVo getDetailByTemplateId(String templateId) {
        RecordTaskTemplateExample example = new RecordTaskTemplateExample();
        example.createCriteria().andTemplateIdEqualTo(templateId);
        List<RecordTaskTemplateWithBLOBs> templates = templateMapper.selectByExampleWithBLOBs(example);
        if (templates.size() > 0) {
            RecordTemplateDetailVo detailVo = new RecordTemplateDetailVo();
            BeanUtils.copyProperties(templates.get(0), detailVo);
            return detailVo;
        }
        return null;
    }

    @Override
    public void deleteDetail(Long id) {
        RecordTaskTemplateWithBLOBs recordTaskTemplate = new RecordTaskTemplateWithBLOBs();
        recordTaskTemplate.setId(id);
        recordTaskTemplate.setDeleteState(DeleteStatus.DELETED.getStatus());
        templateMapper.updateByPrimaryKeySelective(recordTaskTemplate);
    }

    @Override
    public List<Map<String, Object>> getPluginsByTemplateId(String templateId) {
        List<Map<String, Object>> pluginVos = Arrays.stream(InvokeIdentity.values()).map(invokeType -> {
            Map<String, Object> pluginVoMap = new HashMap<>(4);
            if (StringUtils.isNotBlank(templateId)) {
                pluginVoMap.put("name", invokeType.getIdentity());
                pluginVoMap.put("checked", false);
            } else {
                pluginVoMap.put("name", invokeType.getIdentity());
                pluginVoMap.put("checked", true);
            }
            return pluginVoMap;
        }).collect(Collectors.toList());

        if (StringUtils.isBlank(templateId)) {
            return pluginVos;
        }

        RecordTemplateDetailVo templateDetailVo = getDetailByTemplateId(templateId);
        RecordAgentConfig recordAgentConfig = JSON.parseObject(templateDetailVo.getTemplateConfig(), RecordAgentConfig.class);
        for (String invocationPlugin : recordAgentConfig.getSubInvocationPlugins()) {
            for (Map<String, Object> plugin : pluginVos) {
                if (StringUtils.equals((String) plugin.get("name"), invocationPlugin)) {
                    plugin.put("checked", true);
                    break;
                }
            }
        }
        return pluginVos;
    }
}
