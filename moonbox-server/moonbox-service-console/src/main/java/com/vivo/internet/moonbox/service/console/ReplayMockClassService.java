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
