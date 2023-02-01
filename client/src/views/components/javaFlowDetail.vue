<!--
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
-->
<template>
  <div>
    <el-form class="demo-ruleForm">
      <el-card>
        <div slot="header" class="clearfix">
          <h3>基础信息</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="traceId：">
              {{ traceId }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="协议类型：">
              {{ type }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="采集机器：">
              {{ host }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>接口信息</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="java方法：">
              {{ uri }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="box-card">
        <el-collapse v-model="paramCollapse" :class="['lever1', {'noBorderBottom': paramCollapse.length}]">
          <el-collapse-item name="1" title="请求参数">
            <el-table :data="requestParameterArray" border max-height="500" class="responseDiffsTable">
              <el-table-column label="序号" align="center" width="50px">
                <template slot-scope="scope">
                  <span>{{ scope.$index }}</span>
                </template>
              </el-table-column>
              <el-table-column label="参数值" prop="parameterValue" align="center">
                <template slot-scope="scope">
                  <el-tooltip placement="top" effect="dark">
                    <div slot="content">{{scope.row.parameterValue}}</div>
                    <div>{{scope.row.parameterValue}}</div>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column label="参数类型" prop="parameterType" align="center">
                <template slot-scope="scope">
                  <el-tooltip placement="top" effect="dark">
                    <div slot="content">{{scope.row.parameterType}}</div>
                    <div>{{scope.row.parameterType}}</div>
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <el-card class="box-card">
        <el-collapse v-model="responseCollapse" :class="['lever1', {'noBorderBottom': responseCollapse.length}]">
          <el-collapse-item name="1" title="响应内容">
            <el-form-item>
              <json-viewer
                :value="response"
                :expand-depth=5
                :expanded="true"
                boxed
                copyable
                sort></json-viewer>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>子调用对比结果</h3>
        </div>
        <el-form-item style="float: left">
          <el-input v-model="inputSearch" size="small" placeholder="请输入接口过滤" clearable></el-input>
        </el-form-item>
        <el-table :data="filterData" border max-height="500">
          <el-table-column label="序号" align="center" width="50px">
            <template slot-scope="scope">
              <span>{{ scope.$index }}</span>
            </template>
          </el-table-column>
          <el-table-column label="子调用类型" prop="type" align="center" show-overflow-tooltip />
            <el-table-column label="子调用信息" prop="uri" align="center" min-width="400" show-overflow-tooltip/>
            <el-table-column label="执行耗时(毫秒)" prop="cost" align="center" show-overflow-tooltip/>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="sonDetail(scope.row)">调用详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-form>
    <el-card class="add-record-bottom-component">
      <el-button size="small" @click="resetForm()">关闭</el-button>
    </el-card>
    <el-dialog title="调用详情" :visible.sync="dialogVisible" width="1000px">
      <el-form class="demo-ruleForm">
        <div v-if="otherFlag">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>接口信息</h3>
            </div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="接口uri：">
                  {{ dialogUri }}
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
          
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>请求参数</h3>
            </div>
            <el-table :data="dialogParameterArray" border max-height="500">
              <el-table-column label="序号" align="center" width="50px">
                <template slot-scope="scope">
                  <span>{{ scope.$index }}</span>
                </template>
              </el-table-column>
              <el-table-column label="参数值" prop="value" align="center" show-overflow-tooltip/>
              <el-table-column label="参数类型" prop="type" align="center" show-overflow-tooltip/>
            </el-table>
          </el-card>
          
          <el-card>
            <div slot="header">
              <h3>响应内容</h3>
            </div>
            <el-form-item>
              <json-viewer
                :value="dialogResponse"
                :expand-depth=5
                boxed
                copyable
                sort></json-viewer>
            </el-form-item>
          </el-card>
          <!-- 调用堆栈卡片 -->
          <el-card shadow="hover">
            <div slot="header">
              <h3>调用堆栈</h3>
            </div>
            <div v-for="(e, i) in stackTraces" :key="i">
              {{e}}
            </div>
          </el-card>
        </div>
        <div v-if="httpFlag">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>接口信息</h3>
            </div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="接口uri：">
                  {{ dialogUri }}
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
          
          <el-card class="box-card">
            <div slot="header" class="clearfix">
                <h3>请求参数</h3>
            </div>
            <h4>接口headers</h4>
            <el-table :data="dialogHeadersArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip/>
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip/>
            </el-table>
            <h4>接口params</h4>
            <el-table :data="dialogParamsArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip/>
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip/>
            </el-table>
          </el-card>
          
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>请求body</h3>
            </div>
            <el-form-item>
              <json-viewer
                :value="dialogBody"
                :expand-depth=5
                boxed
                copyable
                sort></json-viewer>
            </el-form-item>
          </el-card>
          
          <el-card>
            <div slot="header">
              <h3>响应内容</h3>
            </div>
            <el-form-item>
              <json-viewer
                :value="dialogResponse"
                :expand-depth=5
                boxed
                copyable
                sort></json-viewer>
            </el-form-item>
          </el-card>
          <!-- 调用堆栈卡片 -->
          <el-card shadow="hover">
            <div slot="header">
              <h3>调用堆栈</h3>
            </div>
            <div v-for="(e, i) in stackTraces" :key="i">
              {{e}}
            </div>
          </el-card>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer"></span>
    </el-dialog>
  </div>
</template>

<script>
  import API from '@/api'
  import JsonViewer from 'vue-json-viewer'
  export default {
    name: 'v-java-flow',
    components: {
      JsonViewer
    },
    props: ['traceId', 'taskRunId', 'closeType'],
    data() {
      return {
        inputSearch: '',
        dialogVisible: false,
        type: '',
        formLabelWidth: '110px',
        entranceInvocation: {},
        uri: '',
        host: '',
        request: [],
        parameterTypes: [],
        requestParameterArray: [],
        response: {},
        subInvocations: [],
        dialogUri: '',
        dialogRequest: [],
        dialogParameterTypes: [],
        dialogParameterArray: [],
        dialogHeaders: {},
        dialogParamsMap: {},
        dialogHeadersArray: [],
        dialogParamsArray: [],
        paramsKeyArray: [],
        paramsValueArray: [],
        dialogBody: {},
        dialogResponse: {},
        httpFlag: false,
        otherFlag: false,
        stackTraces: '',
        paramCollapse: [],
        responseCollapse: []
      }
    },
    mounted() {
      this.getDetail();
    },
    computed: {
      filterData() {
        let filterData = JSON.parse(JSON.stringify(this.subInvocations))
        if (this.inputSearch.trim()) {
          filterData = this.subInvocations.filter(item => item.uri.indexOf(this.inputSearch.trim()) !== -1)
        }
        return filterData
      }
    },
    methods: {
      resetForm() {
        this.$store.dispatch('page/close', {tagName: this.$route.name, notNext: true});
        if (this.closeType == 1) {
          this.routerPush({name: 'record',params: {}, query: {}})
        } else if (this.closeType == 2) {
          this.routerPush({name: 'playback',params: {}, query: {}})
        }
      },
      getDetail() {
        if (this.traceId == '' && this.taskRunId == '') return
        let params = {
          traceId: this.traceId,
          taskRunId: this.taskRunId
        }
        let that = this
        API.record.flowDetail(params).then((res) => {
          if (res.code === 'SUCCESS') {
            that.host = res.data.host
            that.entranceInvocation = res.data.entranceInvocation
            that.type = that.entranceInvocation.type
            that.uri = that.entranceInvocation.uri
            that.request = that.entranceInvocation.request || []
            that.parameterTypes = that.entranceInvocation.parameterTypes
            for (let i = 0; i < that.request.length; i++) {
              let obj = {}
              obj.parameterValue = JSON.stringify(that.request[i])
              obj.parameterType = that.parameterTypes[i]
              that.requestParameterArray.push(obj)
            }
            if (that.entranceInvocation.response != null) {
              that.response = that.entranceInvocation.response
            }
            that.subInvocations = res.data.subInvocations
          } else {
            this.$message.error('获取dubbo详情失败:' + res.msg)
          }
        })
      },
      sonDetail(row) {
        this.httpFlag = false
        this.otherFlag = false
        this.dialogVisible = true
        this.dialogResponse = {}
        if (row.response) {
          this.dialogResponse = row.response
        }
        this.dialogUri = ''
        if (row.type.toLowerCase() === 'http' || row.type.toLowerCase() === 'okhttp' || row.type.toLowerCase() === 'apache-http-client') {
          this.httpFlag = true
          this.dialogHeadersArray = []
          this.dialogParamsArray = []
          this.dialogUri = row.uri
          this.dialogHeaders = row.headers
          if (typeof row.body == 'object' && row.body != null) {
            this.dialogBody = row.body
          }
          if (typeof row.body == 'string') {
            this.dialogBody = row.body
          }
          if (JSON.stringify(this.dialogHeaders) !== "{}") {
            for (let key in this.dialogHeaders) {
              this.dialogHeadersArray.push({
                key,
                value: this.dialogHeaders[key]
              })
            }
          }
          this.dialogParamsMap = row.paramsMap
          if (JSON.stringify(this.dialogParamsMap) !== "{}") {
            for (let key in this.dialogParamsMap) {
              this.dialogParamsArray.push({
                key,
                value: this.dialogParamsMap[key]
              })
            }
          }

        } else {
          this.dialogParameterArray = []
          this.otherFlag = true
          this.dialogUri = row.uri
          this.dialogRequest = row.request
          this.dialogParameterTypes = row.parameterTypes
          if (this.dialogRequest != null) {
            for (let i = 0; i < this.dialogRequest.length; i++) {
              this.dialogParameterArray.push({
                value: JSON.stringify(this.dialogRequest[i]),
                type: this.dialogParameterTypes[i]
              })
            }
          }
        }
        this.stackTraces = row.stackTraces.split(/\n/)
      }
    }
  }
</script>

<style lang="scss" scoped>
.el-input.is-disabled .el-input__inner {
  background: #f2f6fc;
  opacity: 2;
  color: black;
}
.el-textarea.is-disabled .el-textarea__inner {
  background: #f2f6fc;
  opacity: 2;
  color: black;
}
/deep/ .responseDiffsTable {
  .el-table__body {
    tbody {
      .el-table__row {
        td {
          .cell {
            text-align: center; 
            text-overflow: -o-ellipsis-lastline;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }
        }
      }
    }
  }
}
</style>
