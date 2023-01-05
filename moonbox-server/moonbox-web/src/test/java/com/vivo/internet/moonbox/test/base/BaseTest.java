package com.vivo.internet.moonbox.test.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.vivo.internet.moonbox.web.MoonBoxApplication;

/**
 * BaseTest - 基础测试类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/20 15:39
 */
@SpringBootTest(classes = MoonBoxApplication.class)
@Slf4j
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @BeforeClass
    public void beforeTest() {
        log.info("before test");
    }

    @AfterClass
    public void afterTest() {
        log.info("after test");
    }
}