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
        <div slot="header" class="header">
          <h3>
            <span>基础信息</span>
            <div class="forCopy">
              <el-input class="forCopyInput" ref="forCopyInput" v-model="curlRequest"></el-input>
              <el-button @click="changeValue" size="small" type="primary">复制为curl命令</el-button>
            </div>
          </h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="traceId：">
              {{ traceId }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="协议类型：">
              {{ type }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采集机器：">
              {{ host }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="流量环境：">
              {{ environment }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      <el-card>
        <div slot="header">
          <h3>接口信息</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接口uri：">
              {{ uri }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实uri：">
              {{ requestURI }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="requestParams">
        <div slot="header" class="clearfix">
          <h3>请求参数</h3>
        </div>
        <el-collapse v-model="paramCollapse" :class="['lever2', {'noBorderBottom': paramCollapse.length}]">
          <el-collapse-item name="1" title="接口headers">
            <el-table :data="headersArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip />
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip />
            </el-table>
          </el-collapse-item>
          <el-collapse-item name="2" title="接口body" v-if="requestBody">
            <json-viewer :value="requestBody" :expanded="true" :expand-depth=5 boxed copyable sort></json-viewer>
          </el-collapse-item>
          <el-collapse-item name="3" title="接口params">
            <el-table :data="paramsArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip />
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <el-card v-if="showResponse">
        <el-collapse v-model="responseCollapse" :class="['lever1', {'noBorderBottom': responseCollapse.length}]">
          <el-collapse-item name="1" title="响应内容">
            <el-form-item>
              <json-viewer :value="response" :expand-depth=5 :expanded="true" boxed copyable sort></json-viewer>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <el-card>
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
        <!-- dubbo接口 -->
        <div v-if="otherFlag">
          <!-- 接口信息卡片 -->
          <el-card>
            <div slot="header">
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
          <!-- 请求参数卡片 -->
          <el-card>
            <div slot="header">
              <h3>请求参数</h3>
            </div>
            <el-form-item>
              <el-row :gutter="20">
                <el-col :span="24">
                  <el-table :data="requestParameterArray" border max-height="500">
                    <el-table-column label="序号" align="center" width="50px">
                      <template slot-scope="scope">
                        <span>{{ scope.$index }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="参数值" prop="parameterValue" align="center" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column label="参数类型" prop="parameterType" align="center" show-overflow-tooltip />
                  </el-table>
                </el-col>
              </el-row>
            </el-form-item>
          </el-card>
          <!-- 响应内容卡片 -->
          <el-card>
            <div slot="header">
              <h3>响应内容</h3>
            </div>
            <el-form-item>
              <json-viewer :value="dialogResponse" :expand-depth=5 boxed copyable sort></json-viewer>
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
        <!-- http接口 -->
        <div v-if="httpFlag">
          <!-- 接口信息卡片 -->
          <el-card>
            <div slot="header">
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
          <!-- 请求参数卡片 -->
          <el-card>
            <div slot="header">
              <h3>请求参数</h3>
            </div>
            <el-form-item>
              <h4>接口headers</h4>
            </el-form-item>
            <el-table :data="dialogHeadersArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip />
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip />
            </el-table>
            <el-form-item>
              <h4>接口params</h4>
            </el-form-item>
            <el-table :data="dialogParamsArray" border max-height="500">
              <el-table-column label="key" prop="key" align="center" show-overflow-tooltip />
              <el-table-column label="value" prop="value" align="center" show-overflow-tooltip />
            </el-table>
          </el-card>

          <!-- 请求body卡片 -->
          <el-card>
            <div slot="header">
              <h3>请求body</h3>
            </div>
            <el-form-item>
              <json-viewer :value="dialogBody" :expand-depth=5 boxed copyable sort></json-viewer>
            </el-form-item>
          </el-card>

          <el-card  shadow="hover">
            <div slot="header">
              <h3>响应内容</h3>
            </div>
            <el-form-item>
              <json-viewer :value="dialogResponse" :expand-depth=5 boxed copyable sort></json-viewer>
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
  name: 'v-http-flow',
  props: ['traceId', 'taskRunId', 'closeType'],
  components: {
    JsonViewer
  },
  data() {
    return {
      curlRequest: '',
      inputSearch: '',
      dialogVisible: false,
      type: '',
      entranceInvocation: {},
      uri: '',
      requestURI: '',
      port: '',
      contentType: '',
      host: '',
      headers: {},
      headersArray: [],
      dialogHeadersArray: [],
      paramsMap: {},
      paramsArray: [],
      requestBody: null,
      dialogParamsArray: [],
      response: {},
      subInvocations: [],
      environment: '',
      dialogUri: '',
      requestParameterArray: [],
      dialogHeaders: {},
      dialogParamsMap: {},
      dialogBody: {},
      dialogResponse: {},
      httpFlag: false,
      otherFlag: false,
      paramCollapse: [],
      responseCollapse: []
    }
  },
  computed: {
    filterData() {
      let filterData = JSON.parse(JSON.stringify(this.subInvocations))
      if (this.inputSearch.trim()) {
          let that = this
          filterData = this.subInvocations.filter(item => item.uri.indexOf(that.inputSearch.trim()) !== -1)
      }
      return filterData
    },
    showResponse () {
      return Object.keys(this.response).length
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    resetForm() {
      this.$store.dispatch('page/close', {
        tagName: this.$route.name,
        notNext: true,
      })
      if (this.closeType == 1) {
        this.routerPush({ name: 'record', params: {}, query: {} })
      } else if (this.closeType == 2) {
        this.routerPush({ name: 'playback', params: {}, query: {} })
      }
    },
    getDetail() {
      if (this.traceId == '' && this.taskRunId == '') return
      let params = {
        traceId: this.traceId,
        taskRunId: this.taskRunId
      }
      API.record.flowDetail(params).then((res) => {
        if (res.code === 'SUCCESS') {
          this.host = res.data.host
          this.curlRequest = res.data.curlRequest
          this.environment = res.data.environment
          this.entranceInvocation = res.data.entranceInvocation
          this.type = this.entranceInvocation.type
          this.uri = this.entranceInvocation.uri
          this.requestURI = this.entranceInvocation.requestURI
          this.port = this.entranceInvocation.port
          this.contentType = this.entranceInvocation.contentType
          this.headers = this.entranceInvocation.headers
          if (JSON.stringify(this.headers) !== '{}') {
            for (let key in this.headers) {
              let obj = {}
              obj.key = key
              obj.value = this.headers[key]
              this.headersArray.push(obj)
            }
          }
          this.paramsMap = this.entranceInvocation.paramsMap
          if (JSON.stringify(this.paramsMap) !== '{}') {
            for (let key in this.paramsMap) {
              let obj = {}
              obj.key = key
              obj.value = this.paramsMap[key]
              this.paramsArray.push(obj)
            }
          }
          if (this.entranceInvocation.body) {
            this.requestBody = this.entranceInvocation.body
          }
          if (this.entranceInvocation.response) {
            this.response = this.entranceInvocation.response
          }
          this.subInvocations = res.data.subInvocations
        } else {
          this.$message.error('获取http详情失败:' + res.msg)
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
        this.dialogHeadersArray = []
        this.dialogParamsArray = []
        this.httpFlag = true
        this.dialogUri = row.uri
        this.dialogHeaders = row.headers
        if (typeof row.body == 'object' && row.body != null) {
          this.dialogBody = row.body
        }
        if (typeof row.body == 'string') {
          this.dialogBody = row.body
        }

        if (JSON.stringify(this.dialogHeaders) !== '{}') {
          for (let key in this.dialogHeaders) {
            this.dialogHeadersArray.push({
              key,
              value: this.dialogHeaders[key]
            })
          }
        }
        this.dialogParamsMap = row.paramsMap
        if (JSON.stringify(this.dialogParamsMap) !== '{}') {
          for (let key in this.dialogParamsMap) {
            this.dialogParamsArray.push({
              key,
              value: this.dialogParamsMap[key]
            })
          }
        }
      } else {
        this.otherFlag = true
        this.requestParameterArray = []
        this.dialogUri = row.uri
        if (row.request && row.request.length) {
          for (let i = 0; i < row.request.length; i++) {
            this.requestParameterArray.push({
              parameterValue: JSON.stringify(row.request[i]),
              parameterType: row.parameterTypes[i]
            })
          }
        }
      }

      this.stackTraces = row.stackTraces.split(/\n/)
    },
    changeValue () {
      if (!this.curlRequest) return this.$message.error('复制失败：curlRequest为null')
      this.$refs.forCopyInput.select()
      document.execCommand('copy')
      this.$message.success('复制成功')
    }
  }
}
</script>
<style lang="scss" scoped>
/deep/ .header {
  position: relative;
  .forCopy {
    position: absolute;
    top: 0;
    right: 0;
    .forCopyInput {
      position: absolute;
      z-index: -1;
      opacity: 0;
    }
  }
}

/deep/.requestParams {
  .el-card__body {
    padding: 0 20px;
  }
}

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
</style>
