package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.jvm.sandbox.repeater.plugin.Comparable;
import com.alibaba.jvm.sandbox.repeater.plugin.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.plugin.CompareResult;
import com.alibaba.jvm.sandbox.repeater.plugin.Difference;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.UriMatchUtils;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.util.ComparableGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractReflectCompareStrategy
 *
 * @author yanjiang.liu
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
public abstract class AbstractReflectCompareStrategy extends AbstractMockStrategy {

    private static final RepeaterConfig REPEATER_CONFIG = MoonboxContext.getInstance().getConfig();

    @Override
    public StrategyType type() {
        return StrategyType.OBJECT_DFF;
    }

    @Override
    protected SelectResult select(MockRequest request) {

        final List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (CollectionUtils.isEmpty(subInvocations)
                && CollectionUtils.isEmpty(request.getRecordModel().getRemoveSubInvocations())) {
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }
        List<Invocation> target = selectTargets(request);
        if (CollectionUtils.isEmpty(target)) {
            log.error("can't find any sub invocation, strategy={}, identity={}", type().name(), request.getIdentity().getUri());
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }

        InvokeType invokeType = request.getType();
        Map<Integer, Pair<Invocation, List<Difference>>> invocationMap = Maps.newHashMap();
        // step2: 反射对比；默认忽略时间戳
        for (Invocation invocation : target) {
            Comparable comparable = generateComparator(request);
            CompareResult result;
            try {
                result = comparable.compare(getCompareParamFromOrigin(invocation, request),
                        getCompareParamFromCurrent(invocation, request));
            } catch (Throwable e) {
                log.error("compare error", e);
                return SelectResult.builder().match(false).invocation(invocation)
                        .cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
            }
            if (!result.hasDifference()) {
                log.info("find target invocation by {},index={},identity={}", type().name(), request.getIndex(),
                        request.getIdentity().getUri());
                try {
                    Iterator<Invocation> ite = subInvocations.iterator();
                    while (ite.hasNext()) {
                        if (invocation.equals(ite.next())) {
                            {
                                ite.remove();
                                // 将数据添加到删除列表里面，如果只是新增了相同的流程，后续可以继续做比对使用
                                invocation.setReplayAlreadyMatch(Boolean.TRUE);
                                request.getRecordModel().getRemoveSubInvocations().add(invocation);
                            }
                            break;
                        }
                    }
                    return SelectResult.builder().match(true).invocation(invocation)
                            .cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            Pair<Invocation, List<Difference>> pair = ImmutablePair.of(invocation, result.getDifferences());
            invocationMap.put(result.getDifferences().size(), pair);
        }

        // 如果是按照key对比的没有找到对应的key直接报错未匹配子调用而不是入参对比异常
        if (InvokeType.isKeyInvocation(invokeType)) {
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }

        // 如果没有找到，返回差异最少的一条
        List<Integer> scores = new ArrayList<>(invocationMap.keySet());
        scores.sort((o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            return o1 - o2 > 0 ? 1 : -1;
        });

        for (Integer diffCount : scores) {
            Pair<Invocation, List<Difference>> pair = invocationMap.get(diffCount);
            Invocation invocation = pair.getLeft();
            if (invocation == null || invocation.getReplayAlreadyMatch()) {
                continue;
            }
            invocation.setDiffs(pair.getRight());
            return SelectResult.builder().match(false).invocation(invocation)
                    .cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }
        log.error("find only alreadyMatch invocation but has difference strategy={}, identity={}", type().name(),
                request.getIdentity().getUri());
        return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
    }

    protected List<Invocation> selectTargets(MockRequest request) {
        List<Invocation> targets = Lists.newArrayList();
        List<Invocation> subInvocations = Lists.newArrayList(request.getRecordModel().getSubInvocations());
        // step1:URI匹配,目前做精确匹配，后续可能需要考虑替换
        String requestUri = request.getIdentity().getUri();
        for (Invocation invocation : subInvocations) {
            String invocationUri = invocation.getIdentity().getUri();
            if (StringUtils.equals(invocationUri, requestUri)) {
                targets.add(invocation);
            }
        }
        // 根据index排序
        targets.sort(Comparator.comparingInt(Invocation :: getIndex));
        // 吧部分删除的添加到最后
        if (request.getRecordModel().getRemoveSubInvocations() != null) {
            List<Invocation> invocations = request.getRecordModel().getRemoveSubInvocations();
            for (Invocation invocation : invocations) {
                if (StringUtils.equals(invocation.getIdentity().getUri(), requestUri)) {
                    targets.add(invocation);
                }
            }
        }
        return targets;
    }

    protected Object[] getCompareParamFromOrigin(Invocation invocation, MockRequest request) {
        if (invocation.isProtobufRequestFlag()) {
            return invocation.getProtobufReqeustJsons();
        }
        return doGetCompareParamFromOrigin(invocation, invocation.getRequest(), request);
    }

    protected Object[] getCompareParamFromCurrent(Invocation invocation, MockRequest request) {
        if (invocation.isProtobufRequestFlag()) {
            return request.getArgumentArray();
        }
        return doGetCompareParamFromCurrent(invocation, request.getArgumentArray(), request);
    }

    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        return origin;
    }

    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        return current;
    }

    private Comparable generateComparator(MockRequest request) {

        List<FieldDiffConfig> diffConfigs = REPEATER_CONFIG.getFieldDiffConfigs();
        if (CollectionUtils.isEmpty(diffConfigs)) {
            return ComparableFactory.instance()
                    .create(com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode.LENIENT_DATES);
        }

        // 判断子调用uri是否命中配置规则
        String subUri = request.getIdentity().getUri();
        subUri = subUri.endsWith("/") ? subUri.substring(0, subUri.length() - 1) : subUri;

        //Map<String, FieldDiffConfig> diffConfigMap = diffConfigs.stream()
        //        .collect(Collectors.toMap(FieldDiffConfig::getUri, a -> a, (k1, k2) -> k1));
        //修复如果子调用如果忽略多个字段时，只有一个子调用字段忽略生效的问题。
        Map<String, List<FieldDiffConfig>> diffConfigMap = Maps.newHashMap();
        diffConfigs.forEach(fieldDiffConfig -> {
            List<FieldDiffConfig> middleFieldDiffConfig = diffConfigMap.computeIfAbsent(fieldDiffConfig.getUri(),
                k -> Lists.newArrayList());
            middleFieldDiffConfig.add(fieldDiffConfig);
        });
        log.info("generate comparator: subUri:{}", request.getIdentity().getUri());
        Pair<Boolean, String> pair = matchUri(diffConfigMap.keySet(), subUri);
        if (!pair.getLeft()) {
            return ComparableFactory.instance()
                    .create(com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode.LENIENT_DATES);
        }
        return ComparableGenerator.generate(Lists.newArrayList(diffConfigMap.get(pair.getRight())),
                com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode.LENIENT_DATES,
                Objects.equals(request.getIdentity().getScheme(), InvokeType.HTTP.name()));
    }

    private Pair<Boolean, String> matchUri(Set<String> uriSet, String uri) {
        if (null == uriSet) {
            return ImmutablePair.of(Boolean.FALSE, null);
        }
        if (uriSet.contains(uri)) {
            return ImmutablePair.of(Boolean.TRUE, uri);
        }

        String[] uriArr = uri.split("/");
        for (String item : uriSet) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (!item.contains("{") || !item.contains("}")) {
                continue;
            }
            String[] itemArr = item.split("/");

            if (itemArr.length != uriArr.length) {
                continue;
            }
            if (UriMatchUtils.match(uriArr, uri)) {
                return ImmutablePair.of(Boolean.TRUE, item);
            }
        }
        return ImmutablePair.of(Boolean.FALSE, null);
    }
}
