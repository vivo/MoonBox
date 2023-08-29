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
  <el-card class="box-card" v-loading="detailLoading">
    <el-tabs v-model="activeName">
      <el-tab-pane label="回放详情" name="first">
        <el-form class="demo-ruleForm">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>基础信息</h3>
            </div>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="协议类型：">
                  {{ type }}
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="traceId：">
                  {{ traceId }}
                </el-form-item>
              </el-col>
              <el-col :span="6">
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
                <el-form :inline="true">
                  <el-form-item label="接口uri：">
                    <div style="word-break: break-all;">{{ uri }}</div>
                  </el-form-item>
                </el-form>
              </el-col>
            </el-row>
          </el-card>
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <h3>对比结果</h3>
            </div>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="状态：" class="red">
                  {{ replayCode }}
                </el-form-item>
              </el-col>
              <el-col :span="8" class="red" v-if="replayMessage">
                <el-form-item label="失败原因：">
                  {{ replayMessage }}
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
          <el-card class="box-card" v-if="replayStatus == 0 || replayStatus == 4">
            <div slot="header" class="clearfix">
              <h3>原始响应内容 / 回放响应内容</h3>
            </div>
            <vace-diff :leftContent="acediffLeft" :rightContent="acediffRight" :elName="'acediff'"></vace-diff>
          </el-card>
          <div v-if="responseDiffs.length > 0 ">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <h3>响应结果差异对比</h3>
              </div>
              <el-table :data="responseDiffs" max-height="500" class="responseDiffsTable" border>
                <el-table-column label="对比路径" prop="path" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"></el-table-column>
                <el-table-column label="原始响应结果数据" prop="originData" align="center" :formatter="formatTableTDBlank">
                  <template slot-scope="scope">
                    <el-tooltip placement="top" effect="dark">
                      <div slot="content">{{scope.row.originData}}</div>
                      <span>{{scope.row.originData}}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="回放响应结果数据" prop="currentData" align="center" :formatter="formatTableTDBlank">
                  <template slot-scope="scope">
                    <el-tooltip placement="top" effect="dark">
                      <div slot="content">{{scope.row.currentData}}</div>
                      <span>{{scope.row.currentData}}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="差异说明" prop="diffType" align="center" show-overflow-tooltip :formatter="formatTableTDBlank">
                </el-table-column>
                <el-table-column label="操作" align="center">
                  <template slot-scope="scope">
                    <el-tooltip placement="right" style="margin-left: 1%">
                      <div slot="content" class="tips-content text-tips-content">
                        <div class="rule-box">
                          忽略后下一次回放不会对比该路径。
                        </div>
                      </div>
                      <el-button type="text" size="mini" @click="getIgnorePath(scope.row.path, 2)">忽略对比
                      </el-button>
                    </el-tooltip>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
          <el-card>
            <div slot="header" class="clearfix">
              <h3>子调用对比结果</h3>
            </div>
            <el-form inline>
              <el-form-item style="marginRight: 50px;">
                <el-input v-model="inputSearch" size="small" placeholder="请输入接口过滤" clearable></el-input>
              </el-form-item>
              <el-form-item label="结果：">
                <el-radio-group v-model="subCallComparisonResultRadio" size="small" @change="getDetail">
                  <el-radio-button :label="-1">全部</el-radio-button>
                  <el-radio-button :label="1">失败</el-radio-button>
                  <el-radio-button :label="3">成功</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-form>
            <el-table :data="filterData" border max-height="500">
              <el-table-column label="序号" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.$index }}</span>
                </template>
              </el-table-column>
              <el-table-column label="类型" prop="type" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="接口" prop="currentUri" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="结果" align="center" show-overflow-tooltip :formatter="formatTableTDBlank">
                <template slot-scope="scope">
                  <span v-if="scope.row.replayStatus == 0" class="blue">{{ scope.row.replayStatusErrorCode }}</span>
                  <span v-else-if="scope.row.replayStatus !== 0" class="red">{{ scope.row.replayStatusErrorCode }}</span>
                </template>
              </el-table-column>
              <el-table-column label="结果说明" prop="replayStatusErrorMessage" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="sonDetail(scope.row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-form>
        <el-card class="add-record-bottom-component">
          <el-button size="small" @click="resetForm">关闭</el-button>
        </el-card>
        <!-- 对比详情 dialog -->
        <el-dialog title="对比详情" :visible.sync="dialogVisible" width="90%" top="50px">
          <el-form class="demo-ruleForm">
            <div>
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <h3>对比结果</h3>
                </div>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="状态：">
                      <span v-if="dialogReplayStatus != 0" class="red">对比失败</span>
                      <span v-else class="green">对比成功</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12" v-if="dialogReplayStatus != 0">
                    <el-form-item label="子调用对比结果说明：">
                      <span class="green">{{ dialogReplayMessage }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-card>
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <h3>接口信息</h3>
                </div>
                <el-form-item label="接口uri：">
                  {{ dialogUri }}
                </el-form-item>
              </el-card>
              <el-card class="box-card" v-if="this.dialogReplayStatus == 3">
                <div slot="header">
                  <h3>请求参数</h3>
                </div>
                <el-table :data="currentArgs" border>
                  <el-table-column label="序号" align="center" width="50px">
                    <template slot-scope="scope">
                      <span>{{ scope.$index }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="请求参数" prop="type" align="center" show-overflow-tooltip :formatter="formatTableTDBlank">
                    <template slot-scope="scope">
                      <span>{{ scope.row }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
              <el-card class="box-card" v-if="dialogReplayStatus != 3">
                <div slot="header" class="clearfix">
                  <h3>入参对比</h3>
                </div>
                <el-table :data="diaLogParameterArray" max-height="500">
                  <el-table-column label="原始参数值" prop="value" align="center" :formatter="formatTableTDBlank">
                    <template slot-scope="scope">
                      <el-tooltip placement="top" effect="dark">
                        <div slot="content">{{scope.row.value}}</div>
                        <span class="pointer" @click="showCompare(scope.row.value, scope.row.mockValue)">{{ scope.row.value }}</span>
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <el-table-column label="回放参数值" prop="mockValue" align="center" :formatter="formatTableTDBlank">
                    <template slot-scope="scope">
                      <el-tooltip placement="top" effect="dark">
                        <div slot="content">{{scope.row.mockValue}}</div>
                        <span class="pointer" @click="showCompare(scope.row.value, scope.row.mockValue)">{{ scope.row.mockValue }}</span>
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <el-table-column label="原始参数类型" prop="type" align="center" show-overflow-tooltip width="120px" :formatter="formatTableTDBlank"/>
                  <el-table-column label="入参对比详情" align="center" width="120">
                    <template slot-scope="scope">
                      <el-button type="text" @click="showCompare(scope.row.value3, scope.row.mockValue)">{{ compareFlag ? '隐藏' : '展示'}}</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
              <el-card class="box-card" v-if="compareFlag">
                <div slot="header" class="clearfix">
                  <h3>入参对比详情</h3>
                </div>
                <vace-diff
                  ref="vaceDiff"
                  :leftContent="leftContent"
                  :rightContent="rightContent"
                  :elName="motanFlag ? 'compareFlag1' : 'compareFlag2'"
                  :key="motanFlag ? 'compareFlag1' : 'compareFlag2'">
                </vace-diff>
              </el-card>
              <el-card class="box-card" v-if="dialogReplayStatus != 0 && dialogReplayStatus != 3">
                <div slot="header" class="clearfix">
                  <h3>差异结果</h3>
                </div>
                <el-form-item>
                  <el-table :data="dialogDiff">
                    <el-table-column label="路径" prop="path" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    <el-table-column label="原始参数" prop="originData" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    <el-table-column label="回放参数" prop="currentData" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    <el-table-column label="差异说明" prop="diffType" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    <el-table-column label="操作" align="center">
                      <template slot-scope="scope">
                        <el-tooltip placement="right" style="margin-left: 1%">
                          <div slot="content" class="tips-content text-tips-content">
                            <div class="rule-box">
                              忽略后下一次回放不会对比该路径。
                            </div>
                          </div>
                          <el-button type="text" size="mini" @click="getIgnorePath(scope.row.path, 3)">忽略对比</el-button>
                        </el-tooltip>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-form-item>
              </el-card>
              <el-card v-if="dialogReplayStatus != 3">
                <div slot="header">
                  <h3>MOCK返回结果</h3>
                </div>
                <json-viewer :value="dialogResponse" :expand-depth="5" boxed copyable sort></json-viewer>
              </el-card>
              <el-card>
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
        <el-dialog :visible.sync="httpFlag" width="60%">
          <el-form class="demo-ruleForm">
            <!-- 接口信息 -->
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
            <el-card class="box-card" v-if="this.dialogReplayStatus == 3">
              <div slot="header">
                <h3>请求参数</h3>
              </div>
              <el-table :data="currentArgs" border>
                <el-table-column label="序号" align="center">
                  <template slot-scope="scope">
                    <span>{{ scope.$index }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="请求参数" prop="type" align="center" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <span>{{ scope.row }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
            <el-card>
              <div slot="header">
                <h3>对比结果</h3>
              </div>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="子调用对比结果：" class="red" v-if="dialogReplayStatus != 0">
                    对比失败
                  </el-form-item>
                  <el-form-item label="子调用对比结果：" class="green" v-if="dialogReplayStatus == 0">
                    对比成功
                  </el-form-item>
                </el-col>
                <el-col :span="8" class="red" v-if="dialogReplayStatus != 0">
                  <el-form-item label="子调用对比结果说明：">
                    {{ dialogReplayMessage }}
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
            <el-card class="box-card" v-if="dialogReplayStatus != 3">
              <div slot="header" class="clearfix">
                <h3>入参对比</h3>
              </div>
              <el-form-item>
                <h4>headers对比</h4>
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-table :data="unionArray" border max-height="500">
                      <el-table-column label="原始 key" prop="key1" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="原始 value" prop="value1" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放 key" prop="key2" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放 value" prop="value2" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    </el-table>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <h4>params对比</h4>
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-table :data="paramArray" max-height="500">
                      <el-table-column label="原始 key" prop="key1" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="原始 value" prop="value1" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放 key" prop="key2" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放 value" prop="value2" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                    </el-table>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item>
                <h4>请求body对比</h4>
                <vace-diff :left-content="body" :right-content="replayBody" :elName="'bodyCompare'"></vace-diff>
              </el-form-item>
            </el-card>
            <el-card class="box-card" v-if="dialogReplayStatus != 0 && dialogReplayStatus != 3 ">
              <div slot="header" class="clearfix">
                <h3>差异结果</h3>
              </div>
              <el-form-item>
                <el-table :data="dialogDiff">
                  <el-table-column label="路径" prop="path" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                  <el-table-column label="原始参数" prop="originData" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                  <el-table-column label="回放参数" prop="currentData" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                  <el-table-column label="差异说明" prop="diffType" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                  <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                      <el-tooltip placement="right" style="margin-left: 1%">
                        <div slot="content" class="tips-content text-tips-content">
                          <div class="rule-box">
                            忽略后下一次回放不会对比该路径。
                          </div>
                        </div>
                        <el-button type="text" size="mini" @click="getIgnorePath(scope.row.path, 3)">忽略对比
                        </el-button>
                      </el-tooltip>
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </el-card>
            <el-card v-if="dialogReplayStatus != 3">
              <div slot="header">
                <h3>MOCK返回结果</h3>
              </div>
              <json-viewer :value="dialogResponse" :expand-depth=5 boxed copyable sort></json-viewer>
            </el-card>
              <!-- 调用堆栈卡片 -->
            <el-card>
              <div slot="header">
                <h3>调用堆栈</h3>
              </div>
              <div v-for="(e, i) in stackTraces" :key="i">
                {{e}}
              </div>
            </el-card>
          </el-form>
        </el-dialog>
        <el-dialog title="请选择忽略的路径" :visible.sync="dialogIgnoreVisible" width="600px">
          <el-radio-group v-model="radioValue">
            <el-radio v-for="opt in options" :key="opt" :label="opt">
              {{opt}}
            </el-radio>
          </el-radio-group>
          <span slot="footer" class="dialog-footer">
            <el-button size="small" @click="dialogIgnoreVisible = false">取 消</el-button>
            <el-button size="small" type="primary" @click="executeIgnore('change')">确 定</el-button>
          </span>
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane label="motan流量详情" name="third">
        <v-motan-flow:traceId="recordTraceId" :taskRunId="recordTaskRunId" :closeType="2" :key="recordTraceId">
        </v-motan-flow>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
<script>
import API from '@/api'
import JsonViewer from 'vue-json-viewer'
import vmotanFlow from '../components/motanFlowDetail'
import vaceDiff from './acediff'
export default {
  components: {
    JsonViewer,
    vaceDiff,
    vmotanFlow
  },
  data() {
    return {
      detailLoading: false,
      inputSearch: '',
      params: {},
      options: [],
      radioValue: '',
      dialogIgnoreVisible: false,
      acediffLeft: null,
      acediffRight: null,
      activeName: 'first',
      replayCode: '',
      replayMessage: '',
      dialogVisible: false,
      taskRunId: '',
      type: '',
      originEntranceInvocation: {},
      replayStatus: '',
      host: '',
      uri: '',
      traceId: '',
      response: '',
      replayResponse: '',
      mockInvocations: [],
      responseDiffs: [],
      dialogUri: '',
      diaLogParameterArray: [],
      dialogParamsMap: {},
      replayParamsMap: {},
      dialogHeaders: {},
      replayHeaders: {},
      body: null,
      replayBody: null,
      dialogResponse: {},
      dialogReplayMessage: '',
      dialogReplayStatus: '',
      dialogDiff: [],
      motanFlag: false,
      httpFlag: false,
      otherFlag: false,
      compareFlag: false,
      recordTraceId: '',
      recordTaskRunId: '',
      unionArray: [],
      paramArray: [],
      leftContent: '',
      rightContent: '',
      stackTraces: '',
      currentArgs: [],
      subCallComparisonResultRadio: 1
    }
  },
  computed: {
    filterData() {
      let filterData = JSON.parse(JSON.stringify(this.mockInvocations)) || []
      if (this.inputSearch.trim()) {
        let that = this
        filterData = filterData.filter(item => item.currentUri.indexOf(that.inputSearch.trim()) !== -1)
      }
      return filterData
    }
  },
  created() {
    this.replayTraceId = this.$route.params.replayTraceId
    this.replayTaskRunId = this.$route.params.replayTaskRunId
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.detailLoading = true
      let params = {
        traceId: this.replayTraceId,
        replayTaskRunId: this.replayTaskRunId,
      }
      API.playback.getReplayDataDetail(params).then((res) => {
        if (res.code === 'SUCCESS') {
          this.host = res.data.host
          this.traceId = res.data.traceId
          this.recordTraceId = res.data.recordTraceId
          this.recordTaskRunId = res.data.recordTaskRunId
          this.replayStatus = res.data.replayStatus
          this.originEntranceInvocation = res.data.originEntranceInvocation
          this.replayResponse = JSON.stringify(res.data.replayResponse, null, 4)
          this.type = this.originEntranceInvocation.type
          this.uri = this.originEntranceInvocation.uri
          this.response = JSON.stringify(
            this.originEntranceInvocation.response,
            null,
            4
          )
          this.mockInvocations = res.data.mockInvocations || []
          this.replayCode = res.data.replayCode
          this.replayMessage = res.data.replayMessage
          if (res.data.responseDiffs != null) {
            this.responseDiffs = res.data.responseDiffs
          }
          // 为diff插件赋值
          this.acediffLeft = this.response || ''
          this.acediffRight = this.replayResponse || ''
          this.detailLoading = false
        } else {
          this.$message.error('获取motan详情失败:' + res.msg)
          this.detailLoading = false
        }
      }, err => {
        this.detailLoading = false
        this.$message.error('获取motan详情失败:' + err.msg)
      })
    },
    getIgnorePath(path, diffScope) {
      API.playback.guessUserWant({ path }).then((res) => {
        if (res.code === 'SUCCESS') {
          this.paths = res.data
          this.params = {
            appName: this.appName,
            fieldPath: this.uri,
            diffScope
          }
          if (diffScope === 3) {
            this.uri = this.dialogUri
          }
          if (res.data.length == 0) {
            params.fieldPath = path
            if (diffScope === 2) {
              this.executeIgnore() 
            }
          } else {
            this.dialogIgnoreVisible = true
            this.options = res.data
            if (this.options.length != 0) {
              this.radioValue = this.options[0]
            }
          }
        } else {
          this.$message.error('获取对比数据失败:' + res.msg)
        }
      })
    },
    executeIgnore(type) {
      if (type === 'change') {
        this.params.diffUri = this.uri
        this.params.fieldPath = this.radioValue
      }
      API.playback.ignoreReplayConfigPath(this.params).then((res) => {
        if (res.code === 'SUCCESS') {
          this.$message.success('忽略成功')
          this.dialogIgnoreVisible = false
        } else {
          this.$message.error('忽略对比失败:' + res.msg)
        }
      })
    },
    resetForm() {
      this.$store.dispatch('page/close', {
        tagName: this.$route.name,
        notNext: true,
      })
      this.routerPush({ name: 'playback', params: {}, query: {} })
    },
    // eslint-disable-next-line complexity
    sonDetail(row) {
      this.motanFlag = false
      this.otherFlag = false
      this.dialogReplayStatus = row.replayStatus
      this.dialogReplayMessage = row.replayStatusErrorMessage
      this.dialogUri = ''
      this.dialogResponse = {}
      this.dialogDiff = []
      this.dialogDiff = row.diffs
      if (row.replayStatus == 3) {
        this.currentArgs = row.currentArgs
      }
      try {
        if (row.originData != null) {
          if (row.originData.response != null) {
            this.dialogResponse = JSON.parse(row.originData.response)
          }
        }
      } catch (e) {
        this.dialogResponse = row.originData.response
      }
      if (row.type.toLowerCase() === 'motan') {
        this.dialogVisible = true
        this.diaLogParameterArray = []
        this.motanFlag = true
        this.dialogUri = row.uri
        if (row.originData !== null) {
          if (row.originData.request == null) {
            for (let i = 0; i < row.currentArgs.length; i++) {
              let obj = {}
              obj.value = null
              obj.type = null
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          } else if (row.currentArgs == null) {
            for (let i = 0; i < row.originData.request.length; i++) {
              let obj = {}
              obj.value = JSON.stringify(row.originData.request[i], null, 4)
              obj.type = row.originData.parameterTypes[i]
              obj.mockValue = null
              this.diaLogParameterArray.push(obj)
            }
          } else if (
            row.currentArgs != null &&
            row.originData.request != null
          ) {
            for (let i = 0; i < row.originData.request.length; i++) {
              let obj = {}
              obj.value = JSON.stringify(row.originData.request[i], null, 4)
              obj.type = row.originData.parameterTypes[i]
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          }
        } else {
          if (row.currentArgs != null) {
            for (let i = 0; i < row.currentArgs.length; i++) {
              let obj = {}
              obj.value = null
              obj.type = null
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          }
        }
      } else if (row.type.toLowerCase() === 'http' || row.type.toLowerCase() === 'okhttp' || row.type.toLowerCase() === 'apache-http-client') {
        this.httpFlag = true
        this.unionArray = []
        this.paramArray = []
        this.httpFlag = true
        this.dialogUri = row.uri
        if (row.originData != null) {
          this.dialogHeaders = row.originData.headers
        }
        this.replayHeaders = row.replayHeaders
        if (JSON.stringify(this.dialogHeaders) !== '{}') {
          for (let key in this.dialogHeaders) {
            let obj = {}
            obj.key = key
            obj.value = this.dialogHeaders[key]
            if (
              this.replayHeaders === undefined ||
              this.replayHeaders[key] === undefined
            ) {
              this.unionArray.push({
                key1: obj.key,
                value1: obj.value,
                key2: '-',
                value2: '-',
              })
            } else {
              this.unionArray.push({
                key1: obj.key,
                value1: obj.value,
                key2: obj.key,
                value2: obj.value,
              })
            }
          }
        }
        if (JSON.stringify(this.replayHeaders) !== '{}') {
          for (let key in this.replayHeaders) {
            let obj = {}
            obj.key = key
            obj.value = this.replayHeaders[key]
            if (
              this.dialogHeaders === undefined ||
              this.dialogHeaders[key] === undefined
            ) {
              this.unionArray.push({
                key1: '-',
                value1: '-',
                key2: obj.key,
                value2: obj.value,
              })
            }
          }
        }
        if (row.originData != null) {
          this.dialogParamsMap = row.originData.paramsMap
        }
        this.replayParamsMap = row.replayParamsMap
        if (JSON.stringify(this.dialogParamsMap) !== '{}') {
          for (let key in this.dialogParamsMap) {
            let obj = {}
            obj.key = key
            obj.value = this.dialogParamsMap[key]
            if (
              this.replayParamsMap === undefined ||
              this.replayParamsMap[key] === undefined
            ) {
              this.paramArray.push({
                key1: obj.key,
                value1: obj.value,
                key2: '-',
                value2: '-',
              })
            } else {
              this.paramArray.push({
                key1: obj.key,
                value1: obj.value,
                key2: obj.key,
                value2: obj.value,
              })
            }
          }
        }
        if (JSON.stringify(this.replayParamsMap) !== '{}') {
          for (let key in this.replayParamsMap) {
            let obj = {}
            obj.key = key
            obj.value = this.replayParamsMap[key]
            if (
              this.dialogParamsMap === undefined ||
              this.dialogParamsMap[key] === undefined
            ) {
              this.paramArray.push({
                key1: '-',
                value1: '-',
                key2: obj.key,
                value2: obj.value,
              })
            }
          }
        }
        if (row.originData != null) {
          if (typeof row.originData.body == 'object') {
            this.body = JSON.stringify(row.originData.body, null, 4) || ''
          }
        }
        if (typeof row.replayBody == 'object') {
          this.replayBody = JSON.stringify(row.replayBody, null, 4) || ''
        }
        if (row.originData != null) {
          if (typeof row.originData.body == 'string') {
            this.body = row.originData.body || ''
          }
        }
        if (typeof row.replayBody == 'string') {
          this.replayBody = row.replayBody || ''
        }
      } else {
        this.dialogVisible = true
        this.diaLogParameterArray = []
        this.otherFlag = true
        this.dialogUri = row.uri
        if (row.originData !== null) {
          if (row.originData.request == null) {
            for (let i = 0; i < row.currentArgs.length; i++) {
              let obj = {}
              obj.value = null
              obj.type = null
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          } else if (row.currentArgs == null) {
            for (let i = 0; i < row.originData.request.length; i++) {
              let obj = {}
              obj.value = JSON.stringify(row.originData.request[i], null, 4)
              obj.type = row.originData.parameterTypes[i]
              obj.mockValue = null
              this.diaLogParameterArray.push(obj)
            }
          } else if (
            row.currentArgs != null &&
            row.originData.request != null
          ) {
            for (let i = 0; i < row.originData.request.length; i++) {
              let obj = {}
              obj.value = JSON.stringify(row.originData.request[i], null, 4)
              obj.type = row.originData.parameterTypes[i]
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          }
        } else {
          if (row.currentArgs != null) {
            for (let i = 0; i < row.currentArgs.length; i++) {
              let obj = {}
              obj.value = null
              obj.type = null
              obj.mockValue = JSON.stringify(row.currentArgs[i], null, 4)
              this.diaLogParameterArray.push(obj)
            }
          }
        }
      }
      this.stackTraces = row.stackTraces.split(/\n/)
    },
    showCompare(value, mockValue) {
      this.compareFlag = !this.compareFlag
      if (this.compareFlag) {
        this.$nextTick(() => {
          this.leftContent = value || ''
          this.rightContent = mockValue || ''
        })
      } else {
        this.leftContent = ''
        this.rightContent = ''
      }
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
</style>
