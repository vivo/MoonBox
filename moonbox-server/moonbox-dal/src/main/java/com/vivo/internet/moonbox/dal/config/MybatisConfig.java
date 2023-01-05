package com.vivo.internet.moonbox.dal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xu.kai<br>
 * @version V1.0
 * @Description: mybatis插件配置
 * @date Date : 2022年08月15日 20:29
 */
@Configuration
@MapperScan(value = "com.vivo.internet.moonbox.dal.mapper")
public class MybatisConfig {

}
