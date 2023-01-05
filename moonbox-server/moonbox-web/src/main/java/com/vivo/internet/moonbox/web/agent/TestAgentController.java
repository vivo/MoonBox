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
