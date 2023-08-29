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
package com.vivo.internet.moonbox.service.data.es.replay.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import com.vivo.internet.moonbox.common.api.constants.ReplayStatus;
import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.service.data.es.config.AbstractElasticService;
import com.vivo.internet.moonbox.service.data.es.replay.hepler.RepeatModelEntityConverter;
import com.vivo.internet.moonbox.service.data.es.replay.model.EsReplayEntity;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayDataListQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.replay.ReplayUriCountResult;
import com.vivo.internet.moonbox.service.data.service.ReplayDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReplayDataServiceImpl - 流量回复存储-es实现类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 16:48
 */
@SuppressWarnings("deprecation")
@Service
@Slf4j
public class ReplayDataServiceImpl extends AbstractElasticService implements ReplayDataService, InitializingBean {

    @Value("classpath:static/replay_mapping.json")
    private Resource resource;

    private static final String INDEX_NAME = "idx_repeater_replay";

    @Override
    public List<ReplayUriCountResult> getReplayUriCountList(ReplayUriCountQuery query) {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).indicesOptions(IndicesOptions.lenientExpandOpen())
                .preference("_local");
        List<CompositeValuesSourceBuilder<?>> aggBuilder = Lists.newArrayList();
        aggBuilder.add(new TermsValuesSourceBuilder("uri").field(EsReplayEntity.Fields.entranceDesc));
        aggBuilder.add(new TermsValuesSourceBuilder("status").field(EsReplayEntity.Fields.replayResultStatus));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(0)
                .query(QueryBuilders.boolQuery().filter(
                        QueryBuilders.termQuery(EsReplayEntity.Fields.replayTaskRunId, query.getReplayTaskRunId())));
        if (StringUtils.isNotBlank(query.getUriMatchCondition())) {
            sourceBuilder.query(QueryBuilders.boolQuery().filter(
                    QueryBuilders.termQuery(EsReplayEntity.Fields.entranceDesc, query.getUriMatchCondition())));
        }
        sourceBuilder.aggregation(new CompositeAggregationBuilder("myBuckets", aggBuilder).size(1000));
        searchRequest.source(sourceBuilder);

        // 获取聚合查询结果
        SearchResponse r = search(searchRequest);
        ParsedComposite parsedComposite = r.getAggregations().get("myBuckets");
        if (CollectionUtils.isEmpty(parsedComposite.getBuckets())) {
            return Lists.newArrayList();
        }

        Multiset<String> successCount = HashMultiset.create();
        Multiset<String> failCount = HashMultiset.create();
        Set<String> firstAdd = Sets.newHashSet();
        List<ReplayUriCountResult> countResults = parsedComposite.getBuckets().stream().map(item -> {
            Map<String, Object> keys = item.getKey();
            String uri = keys.get("uri").toString();
            int status = NumberUtils.toInt(keys.get("status").toString(), -1);
            if (ReplayStatus.REPLAY_SUCCESS.getCode() == status) {
                successCount.add(uri, (int) item.getDocCount());
            } else {
                failCount.add(uri, (int) item.getDocCount());
            }
            if (firstAdd.add(uri)) {
                return ReplayUriCountResult.builder().replayUri(keys.get("uri").toString())
                        .replayTaskRunId(query.getReplayTaskRunId())
                        .invokeType(InvokeType.getInvokeTypeByUri(keys.get("uri").toString()).getInvokeName()).build();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        countResults.forEach(res -> {
            res.setSuccessCount((long) successCount.count(res.getReplayUri()));
            res.setReplayCount((long) failCount.count(res.getReplayUri()) +res.getSuccessCount());
        });
        return countResults;
    }

    @Override
    public PageResult<RepeatModelEntity> getReplayDataList(PageRequest<ReplayDataListQuery> req) {

        Assert.notNull(req, "pageRequest can not null!req=" + JSON.toJSONString(req));
        Assert.notNull(req.getQueryParam(), "query param cannot  null!param=" + JSON.toJSONString(req.getQueryParam()));
        // 基础查询参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(EsReplayEntity.Fields.replayTaskRunId,
                req.getQueryParam().getReplayTaskRunId()));
        boolQuery.filter(
                QueryBuilders.termQuery(EsReplayEntity.Fields.entranceDesc, req.getQueryParam().getReplayUri()));
        if (StringUtils.isNotBlank(req.getQueryParam().getTraceIdCondition())) {
            boolQuery.filter(QueryBuilders.regexpQuery(EsReplayEntity.Fields.replayTraceId,
                    req.getQueryParam().getTraceIdCondition()));
        }
        sourceBuilder.query(boolQuery);
        // 对对比失败的url进行去重聚合
        if (req.getQueryParam().getErrorDistinct() != null && req.getQueryParam().getErrorDistinct()) {
            CollapseBuilder collapseBuilder = new CollapseBuilder(EsReplayEntity.Fields.compareErrorUri);
            sourceBuilder.collapse(collapseBuilder);
            List<CompositeValuesSourceBuilder<?>> aggBuilder = new ArrayList<>(1);
            aggBuilder.add(new TermsValuesSourceBuilder(EsReplayEntity.Fields.compareErrorUri)
                    .field(EsReplayEntity.Fields.compareErrorUri));
            sourceBuilder.aggregation(new CompositeAggregationBuilder("myBuckets", aggBuilder).size(100));
        }
        // 分页排序参数
        sourceBuilder.sort(SortBuilders.fieldSort(EsReplayEntity.Fields.replayDate).order(SortOrder.DESC));
        sourceBuilder.from(req.getPageNum() * req.getPageSize() - req.getPageSize()).size(req.getPageSize());
        // 查询并组装返回
        SearchResponse r = search(buildSearch(sourceBuilder));
        if (null == r || null == r.getHits()) {
            return new PageResult<>(req.getPageNum(), req.getPageSize(), 0, Collections.emptyList());
        }
        List<RepeatModelEntity> repeatModels = Arrays.stream(r.getHits().getHits()).map(hit -> {
            try {
                EsReplayEntity entity = JSON.parseObject(hit.getSourceAsString(), EsReplayEntity.class);
                return RepeatModelEntityConverter.build(entity);
            } catch (Exception e) {
                log.error("parse Object error!!! data={}", hit.getSourceAsString(), e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        PageResult<RepeatModelEntity> res = new PageResult<>(req.getPageNum(), req.getPageSize(),
                r.getHits().getTotalHits().value, repeatModels);

        // 聚合查询时对接过处理
        if (req.getQueryParam().getErrorDistinct() != null && req.getQueryParam().getErrorDistinct()) {
            ParsedComposite aggregations = r.getAggregations().get("myBuckets");
            Map<String, Long> errorCount = Maps.newConcurrentMap();
            aggregations.getBuckets().stream().filter(buck -> buck.getDocCount() > 0)
                    .forEach(buck -> errorCount.put(String.valueOf(buck.getKey()), buck.getDocCount()));
            repeatModels.forEach(model -> model.setFailureNumber(errorCount.get(model.getSubErrorCurrentUri())));
            res.setTotal(errorCount.keySet().size());
        }
        return res;
    }

    @Override
    public RepeatModelEntity getReplayDataDetail(String replayTaskRunId, String replayTraceId) {

        GetRequest getRequest = new GetRequest(INDEX_NAME, replayTraceId);
        try {
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            //TODO 这里面ESClient返回结果getSourceAsString后是带转义字符的(存进去就带转义字符了)，fastjson反序列化后也带转义字符，会导致对比结果详情页误导
            EsReplayEntity esReplayEntity = JSON.parseObject(getResponse.getSourceAsString(), EsReplayEntity.class);
            //非json串，去转义字符,去""
            //esReplayEntity.setReplayResponse(StringEscapeUtils.unescapeJava(esReplayEntity.getReplayResponse()));
            String str = esReplayEntity.getReplayResponse();
            if (!str.startsWith("{") && !str.endsWith("}")) {
                esReplayEntity.setReplayResponse(str.replace("\"", ""));
            }
            return RepeatModelEntityConverter
                    .build(esReplayEntity);

        } catch (Exception e) {
            log.error("[ elasticsearch ] getReplayEntity error!traceId={}", replayTraceId, e);
        }
        return null;
    }

    @Override
    public Long getReplayFailCount(String replayTaskRunId) {

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.filter(QueryBuilders.termQuery(EsReplayEntity.Fields.replayTaskRunId, replayTaskRunId));
        boolBuilder.mustNot(QueryBuilders.termQuery(EsReplayEntity.Fields.replayResultStatus,
                ReplayStatus.REPLAY_SUCCESS.getCode()));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(1).query(boolBuilder);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).preference("_local")
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        searchRequest.source(sourceBuilder);

        SearchResponse r = search(searchRequest);
        return null != r && null != r.getHits() ? r.getHits().getTotalHits().value : 0L;
    }

    @Override
    public Boolean deleteData(String replayTaskRunId, String replayTraceId) {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, replayTraceId);
        try {
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return RestStatus.OK == response.status();
        } catch (IOException e) {
            log.error("[ elasticsearch ] delete replay Data error!traceId={}", replayTraceId, e);
        }
        return false;
    }

    @Override
    public boolean saveReplayData(RepeatModelEntity entity) {

        EsReplayEntity esEntity = new EsReplayEntity();
        esEntity.setReplayDate(System.currentTimeMillis());
        esEntity.setReplayTraceId(entity.getTraceId());
        esEntity.setCost(entity.getCost());
        esEntity.setReplayTaskRunId(entity.getTaskRunId());
        esEntity.setMockInvocation(JSON.toJSONString(entity.getMockInvocations()));
        esEntity.setRecordTaskRunId(entity.getRecordTaskRunId());
        esEntity.setRecordTaskTemplateId(entity.getRecordTaskTemplateId());
        esEntity.setEntranceDesc(entity.getEntranceDesc());
        esEntity.setInvokeType(entity.getInvokeType());
        esEntity.setAppName(entity.getAppName());
        esEntity.setRecordTraceId(entity.getRecordTraceId());
        esEntity.setEnvironment(entity.getEnvironment());
        esEntity.setCompareErrorUri(entity.getSubErrorCurrentUri());
        esEntity.setReplayResponse(JSON.toJSONString(entity.getResponse()));
        esEntity.setResponseDiffResult(entity.getDiffResult());
        esEntity.setReplayHost(entity.getHost());
        esEntity.setReplayResultStatus(entity.getStatus());

        try {
            IndexRequest request = new IndexRequest(INDEX_NAME).id(esEntity.getReplayTraceId())
                    .source(JSON.toJSONString(esEntity), XContentType.JSON);
            IndexResponse rs = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return null != rs && rs.getResult() == DocWriteResponse.Result.CREATED;
        } catch (IOException e) {
            log.error("save replay data error!!! replayTaskRunId={} replayTraceId={}", entity.getTaskRunId(),
                    esEntity.getReplayTraceId(), e);
            return false;
        }
    }

    @Override
    public PageResult<String> queryFailedRepeaters(String replayTaskRunId, String lastId) {

        SearchResponse response = null;
        // 第一次查询
        if (StringUtils.isBlank(lastId)) {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME)
                    .indicesOptions(IndicesOptions.lenientExpandOpen()).preference("_local");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.boolQuery()
                            .filter(QueryBuilders.termQuery(EsReplayEntity.Fields.replayTaskRunId, replayTaskRunId))
                            .mustNot(QueryBuilders.termQuery(EsReplayEntity.Fields.replayResultStatus,
                                    ReplayStatus.REPLAY_SUCCESS.getCode())))
                    .fetchSource(EsReplayEntity.Fields.recordTraceId, null).size(4);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            try {
                response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("[ elasticsearch ] queryFailedRepeaters error replayTaskRunId={} ", replayTaskRunId, e);
            }
        }
        // 根据scrollId查询
        else {
            response = searchByScrollId(lastId);
        }

        PageResult<String> result = new PageResult<>();
        if (null != response) {
            result.setLastId(response.getScrollId());
            result.setHasNext(null != response.getHits().getHits() && response.getHits().getHits().length > 0);
            result.setResult(Arrays.stream(response.getHits().getHits())
                    .map(documentFields -> (String) documentFields.getSourceAsMap().get("recordTraceId"))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * 公共build方法
     *
     * @param sourceBuilder
     *            sourceBuilder
     * @return {@link SearchRequest}
     */
    private SearchRequest buildSearch(SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).indicesOptions(IndicesOptions.lenientExpandOpen())
                .preference("_local");
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    @Override
    public void afterPropertiesSet() {
        super.initIndex(INDEX_NAME, resource);
    }
}
