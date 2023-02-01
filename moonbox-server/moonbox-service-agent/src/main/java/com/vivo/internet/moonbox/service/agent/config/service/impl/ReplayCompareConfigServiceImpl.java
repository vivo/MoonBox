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
package com.vivo.internet.moonbox.service.agent.config.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.constants.DiffScope;
import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfigExample;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfigExample;
import com.vivo.internet.moonbox.dal.mapper.ReplayDiffConfigMapper;
import com.vivo.internet.moonbox.dal.mapper.SpecialMockConfigMapper;
import com.vivo.internet.moonbox.service.agent.config.service.ReplayCompareConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * ReplayCompareConfigServiceImpl - 回放对比配置服务
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/31 10:11
 */
@Service
@Slf4j
public class ReplayCompareConfigServiceImpl implements ReplayCompareConfigService {

    @Resource
    private ReplayDiffConfigMapper diffConfigMapper;
    @Resource
    private SpecialMockConfigMapper specialMockConfigMapper;

    private final Cache<String, List<SpecialMockConfig>> mockConfigCache = CacheBuilder.newBuilder().maximumSize(10000)
            .expireAfterWrite(30, TimeUnit.SECONDS).build();

    @Override
    public List<FieldDiffConfig> querySubReplayConfigs(String appName) {
        return queryFieldDiffConfigs(appName,
                config -> DiffScope.ENTRANCE_URL_SCOPE.getCode() != config.getDiffScope());
    }

    @Override
    public List<FieldDiffConfig> queryEntranceUriConfigs(String appName, String entranceUri) {
        return queryFieldDiffConfigs(appName, config -> {
            boolean scopeApp = DiffScope.APP_SCOPE.getCode() == config.getDiffScope();
            boolean special = DiffScope.ENTRANCE_URL_SCOPE.getCode() == config.getDiffScope()
                    && StringUtils.equals(config.getDiffUri(), entranceUri);
            return scopeApp || special;
        });
    }

    @Override
    public List<SpecialMockConfig> querySpecialMockConfigs(String appName) {

        try {
            return mockConfigCache.get(appName, () -> {
                SpecialMockConfigExample example = new SpecialMockConfigExample();
                example.createCriteria().andAppNameEqualTo(appName);
                return specialMockConfigMapper.selectByExampleWithBLOBs(example);
            });
        } catch (ExecutionException e) {
            log.error("querySpecialMockConfigs error!appName={}", appName, e);
        }
        return Lists.newArrayList();
    }

    /**
     * 查询流量回放-对比差异化配置
     *
     * @param appName
     *            appName
     * @param predicate
     *            predicate
     * @return {@link List< FieldDiffConfig>}
     */
    private List<FieldDiffConfig> queryFieldDiffConfigs(String appName, Predicate<? super ReplayDiffConfig> predicate) {

        List<FieldDiffConfig> result = Lists.newArrayList();
        ReplayDiffConfigExample diffConfigExample = new ReplayDiffConfigExample();
        diffConfigExample.createCriteria().andAppNameEqualTo(appName);
        List<ReplayDiffConfig> configs = diffConfigMapper.selectByExampleWithBLOBs(diffConfigExample);

        configs.stream().filter(predicate).forEach(config -> {
            FieldDiffConfig fieldDiffConfig = FieldDiffConfig.builder().scope(config.getDiffScope())
                    .uri(config.getDiffUri()).fieldPath(config.getFieldPath()).build();
            result.add(fieldDiffConfig);

        });
        return result;
    }
}