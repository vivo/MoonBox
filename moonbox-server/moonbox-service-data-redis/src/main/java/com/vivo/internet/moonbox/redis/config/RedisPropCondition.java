package com.vivo.internet.moonbox.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.regex.Pattern;

/**
 * 条件
 */
public class RedisPropCondition extends SpringBootCondition implements Condition {

    //IP-PORT校验
    private static final String IP_PORT_PATTERN =
            "^((\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5},)*(\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5}$";

    /**
     * 获取匹配结果
     *
     * @param context 条件上下文
     * @param metadata 注释类型元数据
     * @return 匹配结果
     */
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        // 使用环境变量来决定条件是否满足
        String myProperty = env.getProperty("spring.redis.clusterNodes");
        //IP-PORT校验
        return new ConditionOutcome(Pattern.matches(IP_PORT_PATTERN, myProperty), "");
    }
}
