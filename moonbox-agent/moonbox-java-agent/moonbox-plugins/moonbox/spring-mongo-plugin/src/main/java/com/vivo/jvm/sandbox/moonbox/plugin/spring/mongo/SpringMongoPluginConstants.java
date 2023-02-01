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
package com.vivo.jvm.sandbox.moonbox.plugin.spring.mongo;

/**
 * SpringMongoClass
 */
public class SpringMongoPluginConstants {

    static final String BULK_OPERATION_CLASS = "org.springframework.data.mongodb.core.DefaultBulkOperations";

    static final String MONGO_TEMPLATE_CLASS = "org.springframework.data.mongodb.core.MongoTemplate";

    static final String MONGO_TEMPLATE_QUERY_CLASS = "org.springframework.data.mongodb.core.query.Query";

    static final String MONGO_TEMPLATE_UPDATE_DEFINITION_CLASS = "org.springframework.data.mongodb.core.query.UpdateDefinition";

    static final String MONGO_TEMPLATE_FIND_AND_MODIFY_OPTION_CLASS = "org.springframework.data.mongodb.core.FindAndModifyOptions";

    static final String MONGO_TEMPLATE_NEAR_QUERY_CLASS = "org.springframework.data.mongodb.core.query.NearQuery";

    static final String MAPPING_MONGO_CONVERTER = "org.springframework.data.mongodb.core.convert.MappingMongoConverter";

    static final String DO_INSERT_BATCH = "doInsertBatch";

}
