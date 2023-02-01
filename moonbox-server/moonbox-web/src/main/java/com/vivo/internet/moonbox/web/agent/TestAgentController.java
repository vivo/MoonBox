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
package com.vivo.internet.moonbox.web.agent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;

/**
 * RepeaterAgentController - {@link TestAgentController}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 16:10
 */
@RestController
@RequestMapping("/api/agent/test")
public class TestAgentController {

    @Autowired
    private RecordDataService recordDataService;

    /**
     * 
     * @return
     */
    @RequestMapping(value = "test1", method = RequestMethod.POST)
    public MoonBoxResult<List<RecordUriCountResult> > testRecordUriCountList() {
        RecordUriCountQuery query = RecordUriCountQuery.builder()
                .recordTaskRunId("rc_id_0d924ab71cc3ddf41c8cec696c27kimi").build();
        List<RecordUriCountResult> results = recordDataService.getRecordUriCountList(query);
        return MoonBoxResult.createSuccess(results);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "testJava", method = RequestMethod.POST)
    public MoonBoxResult<List<RecordUriCountResult> > testJava() {
        RecordUriCountQuery query = RecordUriCountQuery.builder()
            .recordTaskRunId("rc_id_0d924ab71cc3ddf41c8cec696c27kimi").build();
        List<RecordUriCountResult> results = recordDataService.getRecordUriCountList(query);
        return MoonBoxResult.createSuccess(results);
    }

}
