package com.vivo.internet.moonbox.test;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.service.console.MoonBoxLogService;
import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;
import com.vivo.internet.moonbox.test.base.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * TestConsoleService - {@link TestConsoleService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/10/27 19:12
 */
public class TestConsoleService extends BaseTest {

    @Autowired
    private MoonBoxLogService moonBoxLogService;

    @Test
    public void testLogService() {
        MoonBoxLogVO moonBoxLogVO = MoonBoxLogVO.builder()
                .taskRunId("xx")
                .content("mmmm").build();
        moonBoxLogService.insertRunLog(moonBoxLogVO);
        System.out.println(JSON.toJSONString(moonBoxLogService.taskRunLogList("xx")));;
    }
}
