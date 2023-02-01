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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.vivo.internet.moonbox.common.api.constants.DiffScope;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfigExample;
import com.vivo.internet.moonbox.dal.mapper.ReplayDiffConfigMapper;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.console.ReplayDiffConfigService;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffCreateReq;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffEditReq;
import com.vivo.internet.moonbox.service.console.vo.ReplayDiffConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReplayDiffConfigServiceImpl - {@link ReplayDiffConfigServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/2 14:31
 */
@Service
public class ReplayDiffConfigServiceImpl implements ReplayDiffConfigService {

    @Resource
    private ReplayDiffConfigMapper replayDiffConfigMapper;

    @Override
    public PageResult<ReplayDiffConfigVo> getReplayDiffConfigList(ReplayDiffConfigPageRequest pageRequest) {

        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        ReplayDiffConfigExample diffConfigExample = new ReplayDiffConfigExample();
        ReplayDiffConfigExample.Criteria criteria = diffConfigExample.createCriteria();

        if(StringUtils.isNotBlank(pageRequest.getAppName())){
            criteria.andAppNameEqualTo(pageRequest.getAppName());
        }

        if (StringUtils.isNotBlank(pageRequest.getCondition())) {
            criteria.andDiffUriLike("%" + pageRequest.getCondition() + "%");
        }

        diffConfigExample.setOrderByClause("update_time desc");

        Page<ReplayDiffConfig> pageResult = (Page<ReplayDiffConfig>) replayDiffConfigMapper
                .selectByExampleWithBLOBs(diffConfigExample);

        List<ReplayDiffConfigVo> replayDiffConfigVoList = pageResult.getResult().stream().map(data -> {
            return getConfig(data);
        }).collect(Collectors.toList());
        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(),
                replayDiffConfigVoList);

    }

    @Override
    public void addDiffConfig(ReplayDiffCreateReq replayDiffCreateReq) {
        ReplayDiffConfig replayDiffConfig = (ReplayDiffConfig) ConverterSupported.getInstance()
                .convert(replayDiffCreateReq, ReplayDiffConfig.class);
        replayDiffConfigMapper.insertSelective(replayDiffConfig);
    }

    @Override
    public void deleteConfig(Long id) {
        replayDiffConfigMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateConfig(ReplayDiffEditReq editReq) {
        ReplayDiffConfig replayDiffConfig = new ReplayDiffConfig();
        BeanUtils.copyProperties(editReq, replayDiffConfig);
        replayDiffConfigMapper.updateByPrimaryKeySelective(replayDiffConfig);
    }

    private ReplayDiffConfigVo getConfig(ReplayDiffConfig replayDiffConfig){
        ReplayDiffConfigVo replayDiffConfigVo = new ReplayDiffConfigVo();
        replayDiffConfigVo.setId(replayDiffConfig.getId());
        replayDiffConfigVo.setAppName(replayDiffConfig.getAppName());
        replayDiffConfigVo.setCreateUser(replayDiffConfig.getCreateUser());
        replayDiffConfigVo.setUpdateUser(replayDiffConfig.getUpdateUser());
        replayDiffConfigVo.setCreateTime(replayDiffConfig.getCreateTime());
        replayDiffConfigVo.setUpdateTime(replayDiffConfig.getUpdateTime());
        replayDiffConfigVo.setDiffScope(replayDiffConfig.getDiffScope());

        DiffScope scope =DiffScope.getDiffScope(replayDiffConfig.getDiffScope());
        replayDiffConfigVo.setDiffScopeMsg(scope !=null ?scope.getMessage():"");
        replayDiffConfigVo.setFieldPath(replayDiffConfig.getFieldPath());
        replayDiffConfigVo.setDiffUri(replayDiffConfig.getDiffUri());
        return replayDiffConfigVo;
    }
}
