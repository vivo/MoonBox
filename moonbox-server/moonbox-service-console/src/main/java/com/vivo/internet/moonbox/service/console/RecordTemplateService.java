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
import com.vivo.internet.moonbox.service.console.model.ConsolePageRequest;
import com.vivo.internet.moonbox.service.console.model.TemplateCreateReq;
import com.vivo.internet.moonbox.service.console.model.TemplateUpdateReq;
import com.vivo.internet.moonbox.service.console.vo.PluginVo;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateDetailVo;
import com.vivo.internet.moonbox.service.console.vo.RecordTemplateVo;

import java.util.List;
import java.util.Map;

/**
 * RecordTemplateService - {@link RecordTemplateService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 19:28
 */
public interface RecordTemplateService {

    /**
     * create record template
     *
     * @param templateCreateReq templateCreateReq
     */
    void createRecordTemplate(TemplateCreateReq templateCreateReq);


    /**
     * update record template
     *
     * @param updateReqReq updateReqReq
     */
    void updateRecordTemplate(TemplateUpdateReq updateReqReq);

    /**
     * get template list
     * @param pageRequest query request
     * @return page query result
     */
    PageResult<RecordTemplateVo> getTemplateList(ConsolePageRequest pageRequest);


    /**
     * get template detail
     * @param id pkId
     * @return detail vo
     */
    RecordTemplateDetailVo getDetailByPkId(Long id);

    /**
     * get template detail
     * @param templateId templateId
     * @return detail vo
     */
    RecordTemplateDetailVo getDetailByTemplateId(String templateId);


    /**
     * delete template detail
     * @param id pkId
     */
    void deleteDetail(Long id);


    /**
     * 查询plugin信息
     * @param templateId 模板Id
     * @return plugin信息
     */
    List<Map<String,Object>> getPluginsByTemplateId(String templateId);

}
