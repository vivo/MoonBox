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
import com.vivo.internet.moonbox.service.console.model.MockClassAddReq;
import com.vivo.internet.moonbox.service.console.model.MockClassEditReq;
import com.vivo.internet.moonbox.service.console.vo.ReplayMockClassVo;

import lombok.Data;

/**
 * ReplayMockClassService - {@link ReplayMockClassService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/2 9:53
 */
public interface ReplayMockClassService {


    @Data
    class MockClassPageRequest {
        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;


        /**
         * app应用名称
         */
        private String   appName;

    }

    /**
     * getReplayMockClassList
     * @param mockClassPageRequest page param
     * @return replay mock class list
     */
    PageResult<ReplayMockClassVo> getReplayMockClassList(MockClassPageRequest mockClassPageRequest);


    /**
     * addMockClassList
     * @param mockClassAddReq add param
     * @return replay mock class list
     */
    void addMockClass(MockClassAddReq mockClassAddReq);


    /**
     * editMockClassList
     * @param mockClassEditReq edit param
     * @return replay mock class list
     */
    void editMockClass(MockClassEditReq mockClassEditReq);


    /**
     * deleteMockClass
     * @param id delete id
     * @return replay mock class list
     */
    void deleteMockClass(Long id);
}
