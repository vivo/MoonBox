package com.vivo.internet.moonbox.test;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayDataListQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;
import com.vivo.internet.moonbox.service.data.service.ReplayDataService;
import com.vivo.internet.moonbox.test.base.BaseTest;

/**
 * TestRecord - 测试用例
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/20 15:53
 */
public class TestReplayDataService extends BaseTest {

    @Autowired
    private ReplayDataService replayDataService;

    @Test
    public void testGetReplayDataDetail() {
        RepeatModelEntity entity = replayDataService.getReplayDataDetail("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db72",
                "kimiRepeater1111");
        Assert.notNull(entity, "testGetReplayDataDetail error");

        entity.setTraceId("test" + RandomUtils.nextInt(1, 10000000));
        boolean toSave = replayDataService.saveReplayData(entity);
        Assert.isTrue(toSave, "save data error");
    }

    @Test
    public void testReplayUriCountList() {
        ReplayUriCountQuery query = ReplayUriCountQuery.builder()
                .replayTaskRunId("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db72").build();
        List<ReplayUriCountResult> results = replayDataService.getReplayUriCountList(query);
        Assert.notEmpty(results, "testReplayUriCountList error");
    }

    @Test
    public void testGetReplayDataList() {

        PageRequest<ReplayDataListQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        ReplayDataListQuery query = ReplayDataListQuery.builder().build();
        query.setReplayTaskRunId("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db72");
        query.setReplayUri("http:///appstore/video/native/record-browse/");
        query.setErrorDistinct(true);
        pageRequest.setQueryParam(query);

        PageResult<RepeatModelEntity> page = replayDataService.getReplayDataList(pageRequest);
        Assert.notNull(page, "testGetReplayDataList error");
    }

    @Test
    public void testQueryFailedRepeaters() {
        PageResult<String> pageResult = replayDataService
                .queryFailedRepeaters("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db72", null);
        Assert.notNull(pageResult, "testQueryFailedRepeaters error");
        PageResult<String> pageResult2 = replayDataService
                .queryFailedRepeaters("tid_rid_ffe2a9e3929595dd4ab7bf5ed956db70", pageResult.getLastId());
        Assert.notNull(pageResult2, "testQueryFailedRepeaters error");
    }
}