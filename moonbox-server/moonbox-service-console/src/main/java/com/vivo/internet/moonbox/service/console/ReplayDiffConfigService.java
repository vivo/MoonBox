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
package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffCreateReq;
import com.vivo.internet.moonbox.service.console.model.ReplayDiffEditReq;
import com.vivo.internet.moonbox.service.console.vo.ReplayDiffConfigVo;

import lombok.Data;

/**
 * ReplayDiffConfigService - {@link ReplayDiffConfigService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/2 11:26
 */
public interface ReplayDiffConfigService {


    @Data
    class ReplayDiffConfigPageRequest {
        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        /**
         * 属性名称
         */
        private String   condition;


        /**
         * app应用名称
         */
        private String   appName;

    }

    /**
     * get replay diff config list
     * @param pageRequest page param
     * @return replay config list
     */
    PageResult<ReplayDiffConfigVo> getReplayDiffConfigList(ReplayDiffConfigPageRequest pageRequest);


    /**
     * add replay diff config
     * @param replayDiffCreateReq add param
     * @return replay mock class list
     */
    void addDiffConfig(ReplayDiffCreateReq replayDiffCreateReq);


    /**
     * delete config by id
     * @param id primary key
     * @return replay mock class list
     */
    void deleteConfig(Long id);


    /**
     * update config by id
     * @param editReq edit req
     * @return replay mock class list
     */
    void updateConfig(ReplayDiffEditReq editReq);
}
