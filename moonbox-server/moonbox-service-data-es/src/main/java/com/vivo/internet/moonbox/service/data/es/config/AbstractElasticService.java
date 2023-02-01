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
package com.vivo.internet.moonbox.service.data.es.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractElasticService - es公共服务类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/2 17:33
 */
@SuppressWarnings("deprecation")
@Slf4j
public abstract class AbstractElasticService {

    @Autowired
    protected RestHighLevelClient restHighLevelClient;

    /**
     * 创建 ES 索引
     *
     * @param indexName
     *            索引
     * @param resource
     *            文档属性集合
     * @return 返回 true，表示创建成功
     */
    protected void initIndex(String indexName, Resource resource) {
        try {
            if (!existsIndex(indexName)) {
                createIndex(indexName, resource);
            }
            putMapping(indexName, resource);
        } catch (IOException e) {
            log.error("[ elasticsearch ] init index exists error! indexName:{}", indexName, e);
            throw new RuntimeException("[ elasticsearch ] >> initIndex exception ");
        }
    }

    /**
     * 根据es scrollId查询数据
     *
     * @param scrollId
     *            scrollId
     * @return {@link SearchResponse}
     */
    protected SearchResponse searchByScrollId(String scrollId) {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueMinutes(1L));

        try {
            return restHighLevelClient.searchScroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            return null;
        }
    }

    /**
     * 执行search方法
     *
     * @param searchRequest
     *            searchRequest
     * @return {@link SearchResponse}
     */
    protected SearchResponse search(SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断某个索引是否存在
     *
     * @param indexName
     *            indexName
     * @return {@link boolean}
     */
    private boolean existsIndex(String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            request.local(false);
            request.humanReadable(true);
            request.includeDefaults(false);
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ elasticsearch ] query  index exists error! indexName:{} ", indexName, e);
            throw new RuntimeException("[ elasticsearch ] query  index exists error! exception:{}", e);
        }
    }

    /**
     * 创建索引
     *
     * @param indexName
     *            indexName
     * @return
     */
    private void createIndex(String indexName, Resource resource) throws IOException {
        JSONObject configJson = getResourceJson(resource);
        Assert.notNull(configJson, "create index error!indexName=" + indexName + ",plz check config json");
        // 获取分片索引配置
        JSONObject settings = configJson.getJSONObject("settings").getJSONObject("index");
        int shardsNum = settings.getInteger("number_of_shards");
        int replicasNum = settings.getInteger("number_of_replicas");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject().startObject("settings").field("number_of_shards", shardsNum)
                .field("number_of_replicas", replicasNum).endObject().endObject();
        CreateIndexRequest request = new CreateIndexRequest(indexName).source(builder);
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        Assert.isTrue(response.isAcknowledged(), "create index error,indexName=" + indexName);
    }

    /**
     * 创建索引
     *
     * @param indexName
     *            indexName
     * @return
     */
    private void putMapping(String indexName, Resource resource) throws IOException {
        JSONObject configJson = getResourceJson(resource);
        Assert.notNull(configJson, "create index error!indexName=" + indexName + ",plz check config json");

        // 获取mapping配置
        JSONObject properties = configJson.getJSONObject("mappings").getJSONObject("_doc").getJSONObject("properties");
        log.info("[ elasticsearch ]  index:{} already exists!", indexName);
        PutMappingRequest putMappingRequest = new PutMappingRequest(indexName);
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject().field("properties", properties).endObject();
        putMappingRequest.source(builder);
        AcknowledgedResponse response = restHighLevelClient.indices().putMapping(putMappingRequest,
                RequestOptions.DEFAULT);
        Assert.isTrue(response.isAcknowledged(), "create index error,indexName=" + indexName);
    }

    /**
     * 读取resource资源文件
     *
     * @param resource
     *            resource
     * @return {@link JSONObject}
     */
    private JSONObject getResourceJson(Resource resource) {
        String mappingJson = null;
        try (InputStream is = resource.getInputStream()) {
            mappingJson = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[ elasticsearch ]  ES mappingResource error", e);
        }
        return StringUtils.isBlank(mappingJson) ? null : JSON.parseObject(mappingJson);
    }
}