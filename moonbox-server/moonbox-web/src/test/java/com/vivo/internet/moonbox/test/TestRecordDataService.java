package com.vivo.internet.moonbox.test;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordDataListQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;
import com.vivo.internet.moonbox.test.base.BaseTest;

/**
 * TestRecord - 测试用例
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/20 15:53
 */
public class TestRecordDataService extends BaseTest {

    @Autowired
    private RecordDataService recordDataService;

    @Test
    public void testRecordUriCountList() {
        RecordUriCountQuery query = RecordUriCountQuery.builder()
                .recordTaskRunId("rc_id_039937bc028bb1df475c52c649906643").build();
        List<RecordUriCountResult> results = recordDataService.getRecordUriCountList(query);
        Assert.notEmpty(results, "testRecordUriCountList error");
    }

    @Test
    public void testBatchGetRecordCountByRunIds() {
        Set<String> recordTaskRunIds = Sets.newHashSet("rc_id_039937bc028bb1df475c52c649906643");
        List<RecordCountResult> results = recordDataService.batchGetRecordCountByRunIds(recordTaskRunIds);
        Assert.notEmpty(results, "testBatchGetRecordCountByRunIds error");
    }

    @Test
    public void testGetRecordDataDetail() {
        RecordWrapperEntity entity = recordDataService.getRecordDataDetail("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db70",
                "010101006182165935477920010020ed");
        Assert.notNull(entity, "testGetRecordDataDetail error");
    }

    @Test
    public void testGetRecordDataList() {

        PageRequest<RecordDataListQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        RecordDataListQuery query = RecordDataListQuery.builder().build();
        query.setRecordTaskRunId("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db70");
        query.setRecordUri("http:///appstore/video/native/record-browse/");
        query.setStartTime("1563764018000");
        query.setEndTime("1663764018000");
        pageRequest.setQueryParam(query);

        PageResult<RecordWrapperEntity> pageResult = recordDataService.getRecordDataList(pageRequest);
        Assert.notNull(pageResult, "testGetRecordDataList error");
    }

    @Test
    public void testQueryWrapperData() {
        List<String> traceIds = Lists.newArrayList("kimi111", "010101006182165935477920010020ed");
        List<String> wrapperData = recordDataService.queryWrapperData("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db70",
                traceIds);
        Assert.notNull(wrapperData, "testQueryWrapperData error");
    }

    @Test
    public void testQueryRecordWrapperData() {
        PageResult<String> pageResult = recordDataService
                .queryRecordWrapperData("rc_id_039937bc028bb1df475c52c649906643", null);
        Assert.notNull(pageResult, "testQueryWrapperData error");
        PageResult<String> pageResult2 = recordDataService
                .queryRecordWrapperData("rc_id_039937bc028bb1df475c52c649906643", pageResult.getLastId());
        Assert.notNull(pageResult2, "testQueryWrapperData error");
    }
}