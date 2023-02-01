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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.vivo.internet.moonbox.common.api.constants.ReplayMockTypeEnum;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfigExample;
import com.vivo.internet.moonbox.dal.mapper.SpecialMockConfigMapper;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.console.ReplayMockClassService;
import com.vivo.internet.moonbox.service.console.model.MockClassAddReq;
import com.vivo.internet.moonbox.service.console.model.MockClassEditReq;
import com.vivo.internet.moonbox.service.console.vo.ReplayMockClassVo;

/**
 * ReplayMockServiceImpl - {@link ReplayMockServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/2 9:54
 */
@Service
public class ReplayMockServiceImpl implements ReplayMockClassService, InitializingBean {

    @Resource
    private SpecialMockConfigMapper specialMockConfigMapper;

    @Override
    public PageResult<ReplayMockClassVo> getReplayMockClassList(MockClassPageRequest pageRequest) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        SpecialMockConfigExample templateExample = new SpecialMockConfigExample();
        SpecialMockConfigExample.Criteria criteria = templateExample.createCriteria();
        if(StringUtils.isNotBlank(pageRequest.getAppName())){
            criteria.andAppNameEqualTo(pageRequest.getAppName());
        }

        templateExample.setOrderByClause("update_time desc");

        Page<SpecialMockConfig> pageResult = (Page<SpecialMockConfig>) specialMockConfigMapper
                .selectByExampleWithBLOBs(templateExample);

        List<ReplayMockClassVo> replayMockClassVoList = pageResult.getResult().stream().map(mockConfig -> {
            ReplayMockClassVo replayMockClassVo = (ReplayMockClassVo) ConverterSupported.getInstance().convert(mockConfig,ReplayMockClassVo.class);
            return replayMockClassVo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(),
                replayMockClassVoList);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void addMockClass(MockClassAddReq mockClassAddReq) {
        CheckerSupported.getInstance().check(mockClassAddReq);
        SpecialMockConfig specialMockConfig = (SpecialMockConfig) ConverterSupported.getInstance().convert(mockClassAddReq,SpecialMockConfig.class);
        specialMockConfigMapper.insertSelective(specialMockConfig);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void editMockClass(MockClassEditReq mockClassAddReq) {
        SpecialMockConfig specialMockConfig =specialMockConfigMapper.selectByPrimaryKey(mockClassAddReq.getId());
        if(specialMockConfig == null){
            throw new BusiException("没有找到要修改的mock信息");
        }
        mockClassAddReq.setMockType(specialMockConfig.getMockType());
        CheckerSupported.getInstance().check(mockClassAddReq);

        SpecialMockConfig dbUpdate = new SpecialMockConfig();
        dbUpdate.setId(mockClassAddReq.getId());
        dbUpdate.setContentJson(mockClassAddReq.getContentJson());
        specialMockConfigMapper.updateByPrimaryKeySelective(dbUpdate);
    }

    @Override
    public void deleteMockClass(Long id) {
        specialMockConfigMapper.deleteByPrimaryKey(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet(){
            ConverterSupported.getInstance()
                    .regConverter((ConverterSupported.Converter<SpecialMockConfig, ReplayMockClassVo>) replayMockClass -> {
                        ReplayMockClassVo replayMockClassVo = new ReplayMockClassVo();
                        replayMockClassVo.setAppName(replayMockClass.getAppName());
                        replayMockClassVo.setMockType(replayMockClass.getMockType());
                        replayMockClassVo.setContentJson(replayMockClass.getContentJson());
                        replayMockClassVo.setCreateUser(replayMockClass.getCreateUser());
                        replayMockClassVo.setUpdateUser(replayMockClass.getUpdateUser());
                        replayMockClassVo.setCreateTime(replayMockClass.getCreateTime());
                        replayMockClassVo.setUpdateTime(replayMockClass.getUpdateTime());
                        replayMockClassVo.setId(replayMockClass.getId());
                        ReplayMockTypeEnum mockTypeEnum = ReplayMockTypeEnum
                                .getReplayMockTypeEnum(replayMockClass.getMockType());
                        replayMockClassVo.setMockTypeMsg(mockTypeEnum != null ? mockTypeEnum.getDesc() : "");
                        return replayMockClassVo;
                    }, SpecialMockConfig.class, ReplayMockClassVo.class);

        }
    }
