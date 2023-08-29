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
package com.vivo.internet.moonbox.service.data.es.record.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.dto.PageRequest;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.service.data.es.config.AbstractElasticService;
import com.vivo.internet.moonbox.service.data.es.record.helper.RecordWrapperEntityConverter;
import com.vivo.internet.moonbox.service.data.es.record.model.EsRecordEntity;
import com.vivo.internet.moonbox.service.data.model.record.RecordCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordDataListQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountQuery;
import com.vivo.internet.moonbox.service.data.model.record.RecordUriCountResult;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * RecordDataServiceImpl - 流量录制存储-es实现类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 16:48
 */
@SuppressWarnings("deprecation")
@Service
@Slf4j
public class RecordDataServiceImpl extends AbstractElasticService implements RecordDataService, InitializingBean {

    @Value("classpath:static/record_mapping.json")
    private Resource resource;

    private static final String INDEX_NAME = "idx_repeater_record";

    @Override
    public List<RecordUriCountResult> getRecordUriCountList(RecordUriCountQuery query) {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).indicesOptions(IndicesOptions.lenientExpandOpen())
                .preference("_local");

        List<CompositeValuesSourceBuilder<?>> aggBuilder = Lists.newArrayList();
        aggBuilder.add(new TermsValuesSourceBuilder("uri").field(EsRecordEntity.Fields.entranceDesc));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(0)
                .query(QueryBuilders.boolQuery().filter(
                        QueryBuilders.termQuery(EsRecordEntity.Fields.recordTaskRunId, query.getRecordTaskRunId())))
                .aggregation(new CompositeAggregationBuilder("myBuckets", aggBuilder).size(1000));
        searchRequest.source(sourceBuilder);

        // 获取聚合查询结果
        SearchResponse r = search(searchRequest);
        ParsedComposite parsedComposite = r.getAggregations().get("myBuckets");
        if (CollectionUtils.isEmpty(parsedComposite.getBuckets())) {
            return Lists.newArrayList();
        }

        return parsedComposite.getBuckets().stream().map(item -> {
            Map<String, Object> keys = item.getKey();
            return RecordUriCountResult.builder().recordUri(keys.get("uri").toString())
                    .recordTaskRunId(query.getRecordTaskRunId()).recordCount(item.getDocCount())
                    .invokeType(InvokeType.getInvokeTypeByUri(keys.get("uri").toString()).getInvokeName()).build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecordCountResult> batchGetRecordCountByRunIds(Set<String> recordTaskRunIds) {

        if (CollectionUtils.isEmpty(recordTaskRunIds)) {
            return Lists.newArrayList();
        }

        // 条件
        BoolQueryBuilder should = QueryBuilders.boolQuery();
        recordTaskRunIds
                .forEach(id -> should.should(QueryBuilders.termQuery(EsRecordEntity.Fields.recordTaskRunId, id)));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(0)
                .query(QueryBuilders.boolQuery().filter(should));
        // 聚合
        List<CompositeValuesSourceBuilder<?>> aggBuilder = Lists.newArrayList();
        aggBuilder.add(new TermsValuesSourceBuilder("uri").field(EsRecordEntity.Fields.recordTaskRunId));
        sourceBuilder.aggregation(new CompositeAggregationBuilder("myBuckets", aggBuilder).size(1000));

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).indicesOptions(IndicesOptions.lenientExpandOpen())
                .preference("_local");
        searchRequest.source(sourceBuilder);

        // 获取聚合查询结果
        SearchResponse r = search(searchRequest);
        ParsedComposite parsedComposite = r.getAggregations().get("myBuckets");
        if (CollectionUtils.isEmpty(parsedComposite.getBuckets())) {
            return Lists.newArrayList();
        }

        return parsedComposite
                .getBuckets().stream().map(item -> RecordCountResult.builder()
                        .recordTaskRunId(item.getKey().get("uri").toString()).recordCount(item.getDocCount()).build())
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<RecordWrapperEntity> getRecordDataList(PageRequest<RecordDataListQuery> req) {

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.filter(QueryBuilders.termQuery(EsRecordEntity.Fields.recordTaskRunId,
                req.getQueryParam().getRecordTaskRunId()));
        if (!StringUtils.isBlank(req.getQueryParam().getRecordUri())) {
            boolBuilder.filter(
                    QueryBuilders.termQuery(EsRecordEntity.Fields.entranceDesc, req.getQueryParam().getRecordUri()));
        }
        if (!StringUtils.isBlank(req.getQueryParam().getTraceId())) {
            boolBuilder
                    .filter(QueryBuilders.termQuery(EsRecordEntity.Fields.traceId, req.getQueryParam().getTraceId()));
        }
        if (!StringUtils.isBlank(req.getQueryParam().getStartTime())) {
            boolBuilder.filter(
                    QueryBuilders.rangeQuery(EsRecordEntity.Fields.date).gte(req.getQueryParam().getStartTime()));
        }
        if (!StringUtils.isBlank(req.getQueryParam().getEndTime())) {
            boolBuilder
                    .filter(QueryBuilders.rangeQuery(EsRecordEntity.Fields.date).lte(req.getQueryParam().getEndTime()));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(req.getPageNum() * req.getPageSize() - req.getPageSize()).size(req.getPageSize())
                .sort(SortBuilders.fieldSort(EsRecordEntity.Fields.date).order(SortOrder.DESC)).query(boolBuilder)
                .fetchSource(null, new String[] { EsRecordEntity.Fields.wrapperRecord });

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).indicesOptions(IndicesOptions.lenientExpandOpen())
                .preference("_local");
        searchRequest.source(sourceBuilder);

        SearchResponse response = search(searchRequest);
        if (response == null || response.getHits() == null) {
            return new PageResult<>(req.getPageNum(), req.getPageSize(), 0, Collections.emptyList());
        }
        List<RecordWrapperEntity> entities = Arrays.stream(response.getHits().getHits()).map(hit -> {
            try {
                EsRecordEntity entity = JSON.parseObject(hit.getSourceAsString(), EsRecordEntity.class);
                return RecordWrapperEntityConverter.build(entity);

            } catch (Exception e) {
                log.error("parse Object error!!! data={}", hit.getSourceAsString(), e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        long total = response.getHits().getTotalHits().value;
        boolean hasNext = !CollectionUtils.isEmpty(entities);
        return new PageResult<>(req.getPageNum(), req.getPageSize(), total, entities, hasNext);
    }

    @Override
    public RecordWrapperEntity getRecordDataDetail(String recordTaskRunId, String recordTraceId) {

        GetRequest getRequest = new GetRequest(INDEX_NAME, recordTraceId);
        try {
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            EsRecordEntity esRecordEntity = JSON.parseObject(getResponse.getSourceAsString(), EsRecordEntity.class);
            return RecordWrapperEntityConverter.build(esRecordEntity);
        } catch (Exception e) {
            log.error("[ elasticsearch ] getRecordDataDetail error!traceId={}", recordTraceId, e);
        }
        return null;
    }

    @Override
    public Boolean deleteData(String recordTaskRunId, String recordTraceId) {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, recordTraceId);
        try {
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.OK;
        } catch (IOException e) {
            log.error("[ elasticsearch ] deleteData error!traceId={}", recordTraceId, e);
        }
        return false;
    }

    @Override
    public boolean saveData(RecordWrapperEntity wrapper) {

        EsRecordEntity entity = new EsRecordEntity();
        entity.setDate(wrapper.getTimestamp());
        entity.setTraceId(wrapper.getTraceId());
        entity.setCost(wrapper.getEntranceInvocation().getEnd() - wrapper.getEntranceInvocation().getStart());
        entity.setRecordTaskRunId(wrapper.getTaskRunId());
        entity.setRecordTaskTemplateId(wrapper.getTemplateId());
        entity.setEntranceDesc(wrapper.getEntranceDesc());
        entity.setInvokeType(wrapper.getEntranceInvocation().getType().name());
        entity.setWrapperRecord(wrapper.getWrapperData());
        entity.setAppName(wrapper.getAppName());
        entity.setEnvironment(wrapper.getEnvironment());
        entity.setRecordHost(wrapper.getHost());
        entity.setResponse(wrapper.getResponse());

        try {
            //TODO 这里会莫名奇妙的把response字段转义
            IndexRequest request = new IndexRequest(INDEX_NAME).id(wrapper.getTraceId())
                    .source(JSON.toJSONString(entity), XContentType.JSON);
            IndexResponse rs = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return null != rs && rs.getResult() == DocWriteResponse.Result.CREATED;
        } catch (IOException e) {
            log.error("save record data error!!!recordTaskRunId={}", wrapper.getTaskRunId(), e);
            return false;
        }
    }

    @Override
    public List<String> queryWrapperData(String recordTaskRunId, List<String> traceIds) {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).preference("_local")
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(EsRecordEntity.Fields.recordTaskRunId, recordTaskRunId))
                        .must(QueryBuilders.termsQuery(EsRecordEntity.Fields.traceId, traceIds)))
                .fetchSource(EsRecordEntity.Fields.wrapperRecord, null).size(4);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (null != response) {
                return Arrays
                        .stream(response.getHits().getHits()).map(documentFields -> (String) documentFields
                                .getSourceAsMap().get(EsRecordEntity.Fields.wrapperRecord))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            log.error("[ elasticsearch ] queryRecordWrapperData error replayTaskRunId={} ", recordTaskRunId, e);
        }
        return Lists.newArrayList();
    }

    @Override
    public PageResult<String> queryRecordWrapperData(String recordTaskRunId, String lastId) {

        SearchResponse response = null;
        // 第一次查询
        if (StringUtils.isBlank(lastId)) {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME).preference("_local")
                    .indicesOptions(IndicesOptions.lenientExpandOpen());
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.boolQuery()
                            .filter(QueryBuilders.termQuery(EsRecordEntity.Fields.recordTaskRunId, recordTaskRunId)))
                    .fetchSource(EsRecordEntity.Fields.wrapperRecord, null).size(4);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            try {
                response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("[ elasticsearch ] queryRecordWrapperData error replayTaskRunId={} ", recordTaskRunId, e);
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
            result.setResult(Arrays.stream(response.getHits().getHits()).map(
                    documentFields -> (String) documentFields.getSourceAsMap().get(EsRecordEntity.Fields.wrapperRecord))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() {
        super.initIndex("idx_repeater_record", resource);
    }
}
