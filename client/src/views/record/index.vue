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
    <el-card class="box-card">
      <!-- 查询和新增部分 -->
      <el-form v-model="recordForm" :inline="true" class="demo-ruleForm">
        <el-form-item label="录制模板编码">
          <el-input v-model="recordForm.templateId" size="small" style="width: 300px" placeholder="支持编码或者名称模糊查询" :clearable="true" @clear="search(1, 'clear')"></el-input>
        </el-form-item>
        <el-form-item label="应用名称">
          <el-input v-model="recordForm.appName" size="small" style="width: 300px" placeholder="请输入" :clearable="true" @clear="search(1, 'clear')"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button size="small" type="primary" @click="search(1)" icon="el-icon-search">查询</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <!-- 表格主体部分 -->
      <el-form>
        <el-button class="fr" size="small" type="success" @click="dealRecord('add')" icon="el-icon-plus">新增模板</el-button>
        <el-table
          @expand-change="rowClick"
          :data="recordForm.recordList"
          :expand-row-keys="recordForm.expands1"
          :header-cell-style="tableHeaderStyle"
          v-loading="loading"
          ref="table1"
          row-key="templateId"
          size="small"
          border>
          <el-table-column type="expand">
            <template slot-scope="props2">
              <el-form inline>
                <el-form-item label="执行编码">
                  <el-input v-model="recordForm.recordTaskRunId" @clear="search2(props2.row, 1)" size="small" clearable></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button-group>
                    <el-button size="small" type="primary" @click="search2(props2.row, 1)" icon="el-icon-search">查询</el-button>
                  </el-button-group>
                </el-form-item>
              </el-form>
              <!-- 第二层表格 -->
              <el-table
                @expand-change="rowClick2(arguments[0], arguments[1])"
                :data="props2.row.executionList"
                :expand-row-keys="recordForm.expands2"
                :header-cell-style="tableHeaderStyle"
                row-key="taskRunId"
                ref="table2"
                size="small"
                border>
                <el-table-column type="expand">
                  <template slot-scope="props3">
                    <!-- 第三层表格查询部分 -->
                    <el-form inline>
                      <el-form-item label="接口uri / java方法">
                        <el-input v-model="recordForm.uriCondition" size="small" :clearable="true" @clear="search3(props3.row, 1)"></el-input>
                      </el-form-item>
                      <el-form-item>
                        <el-button size="small" type="primary" @click="search3(props3.row, 1)" icon="el-icon-search">查询</el-button>
                      </el-form-item>
                    </el-form>
                    <el-table
                      @expand-change="rowClick3(arguments[0], arguments[1], props3.row.taskRunId)"
                      :data="props3.row.tableData3"
                      :expand-row-keys="recordForm.expands3"
                      :header-cell-style="tableHeaderStyle"
                      ref="table3"
                      row-key="recordUri"
                      size="small"
                      border>
                      <el-table-column type="expand">
                        <template slot-scope="props4">
                          <!-- 第四层表格查询部分 -->
                          <el-form inline>
                            <el-form-item>
                              <el-input v-model="recordForm.traceIdCondition" @clear="search4(props4.row, 1, props3.row.taskRunId)" clearable style="width: 360px; verticalAlign: middle;" size="small">
                                <el-select v-model="recordForm.traceIdUriConditionType" slot="prepend" placeholder="请选择" style="width: 100px">
                                  <el-option label="traceId" value="1"></el-option>
                                </el-select>
                              </el-input>
                            </el-form-item>
                            <el-form-item>
                              <el-button size="small" type="primary" @click="search4(props4.row, 1, props3.row.taskRunId)" icon="el-icon-search">查询</el-button>
                            </el-form-item>
                          </el-form>
                          <!-- 第四层表格主体部分 -->
                          <el-table :data="props4.row.tableData4" border :header-cell-style="tableHeaderStyle" size="small">
                            <el-table-column label="traceId" prop="traceId" align="center" width="300px" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                            <el-table-column label="采集机器" prop="host" align="center" :formatter="formatTableTDBlank"/>
                            <el-table-column label="采集时间" prop="recordTime" align="center" :formatter="formatTableTDBlank"/>
                            <el-table-column label="执行耗时(毫秒)" prop="cost" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                            <el-table-column label="操作" align="center" width="120px">
                              <template slot-scope="scope">
                                <el-button type="text" @click="flowDetail(scope.row, props4.row.invokeType)" size="mini">详情</el-button>
                                <el-button type="text" @click="deleteFlow(props4.row, scope.row)" size="mini" class="red">删除</el-button>
                              </template>
                            </el-table-column>
                          </el-table>
                          <div class="block fr" v-if="showPage4">
                            <el-pagination
                              @current-change="currentChange4(props4.row, props3.row.taskRunId)"
                              @size-change="sizeChange4(props4.row, props3.row.taskRunId)"
                              :current-page.sync="recordForm.currentPage4"
                              :page-sizes="[6,10,20]"
                              :page-size.sync="recordForm.pageSize4"
                              :total="recordForm.total4"
                              layout="total,sizes,prev,pager,next,jumper">
                            </el-pagination>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column label="接口uri / java方法" prop="recordUri" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="接口协议" prop="invokeType" align="center" :formatter="formatTableTDBlank"/>
                      <el-table-column label="数据总量" prop="recordCount" align="center" :formatter="formatTableTDBlank"/>
                    </el-table>
                    <div class="block fr" v-if="showPage3">
                      <el-pagination
                        @current-change="currentChange3(props3.row)"
                        @size-change="sizeChange3(props3.row)"
                        :current-page.sync="recordForm.currentPage3"
                        :page-sizes="[6,10,20]"
                        :page-size.sync="recordForm.pageSize3"
                        :total="recordForm.total3"
                        layout="total,sizes,prev,pager,next,jumper">
                      </el-pagination>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="执行编码" prop="taskRunId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                <el-table-column label="执行环境" prop="runEnv" align="center" width="100px" :formatter="formatTableTDBlank"/>
                <el-table-column label="执行状态" prop="runStatusMsg" align="center" width="100px" :formatter="formatTableTDBlank">
                  <template slot-scope="scope">
                    <span v-if="scope.row.runStatus == 2" class="blue">{{scope.row.runStatusMsg}}</span>
                    <span v-if="scope.row.runStatus == 3" class="green">{{scope.row.runStatusMsg}}</span>
                    <span v-if="scope.row.runStatus == 4" class="red">{{scope.row.runStatusMsg}}</span>
                    <span v-if="scope.row.runStatus == 6" class="orange">{{scope.row.runStatusMsg}}</span>
                  </template>
                </el-table-column>
                <el-table-column label="agent状态" prop="agentStatusMsg" align="center" width="100px" :formatter="formatTableTDBlank"/>
                <el-table-column label="描述" prop="runDesc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                <el-table-column label="开始时间" prop="taskStartTime" align="center" width="140px" :formatter="formatTableTDBlank"/>
                <el-table-column label="结束时间" prop="taskEndTime" align="center" width="140px" :formatter="formatTableTDBlank"/>
                <el-table-column label="采集总量" prop="count" align="center" width="90px" :formatter="formatTableTDBlank"></el-table-column>
                <el-table-column label="操作" align="center" width="330px">
                  <template slot-scope="scope">
                    <el-button @click="run(scope.row.taskRunId, props2.row.templateId, 'detail')" type="text" size="mini">详情</el-button>
                    <el-button @click="logSearch(scope.row.taskRunId)" type="text" size="mini" >日志</el-button>
                    <el-button v-if="scope.row.runStatus === 4 || scope.row.runStatus === 5 || scope.row.runStatus === 6" @click="tryTask(scope.row.taskRunId, props2.row)" type="text" size="mini">重新录制</el-button>
                    <el-button v-if="scope.row.count > 0" type="text" size="mini" @click="replay(scope.row, props2.row)">流量回放</el-button>
                    <el-button v-if="scope.row.count > 0" type="text" size="mini" @click="replayList(scope.row.taskRunId)">回放列表</el-button>
                    <el-button v-if="scope.row.agentStatus === 1" @click="heartbeatSearch(scope.row.taskRunId)" type="text" size="mini">心跳列表</el-button>
                    <el-button v-if="scope.row.runStatus === 1 || scope.row.runStatus === 2 || scope.row.runStatus === 3 || scope.row.runStatus === 9" @click="stop(scope.row.taskRunId, props2.row)" type="text" size="mini" class="red">停止</el-button>
                    <el-button v-if="scope.row.runStatus === 4 || scope.row.runStatus === 5 || scope.row.runStatus === 6" @click="deleteTask(scope.row.taskRunId, props2.row)" type="text" size="mini" class="red">刪除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="block fr" v-if="showPage2">
                <el-pagination
                  @current-change="currentChange2(props2.row)"
                  @size-change="sizeChange2(props2.row)"
                  :current-page.sync="recordForm.currentPage2"
                  :page-sizes="[6,10,20]"
                  :page-size.sync="recordForm.pageSize2"
                  :total="recordForm.total2"
                  layout="total,sizes,prev,pager,next,jumper">
                </el-pagination>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="录制模板编码" prop="templateId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column label="模板名称" prop="templateName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column label="应用名称" prop="appName" align="center" width="180" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column label="创建人" prop="createUser" align="center" width="80px" :formatter="formatTableTDBlank">
            <template slot-scope="scope">
              <div>{{ scope.row.createUser }}</div>
              <div v-if="scope.row.createUserName">{{ scope.row.createUserName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" align="center" width="150px" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column label="更新人" prop="updateUser" align="center" width="80px" :formatter="formatTableTDBlank">
            <template slot-scope="scope">
              <div>{{ scope.row.updateUser }}</div>
              <div v-if="scope.row.updateUserName">{{ scope.row.updateUserName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="更新时间" prop="updateTime" align="center" width="150px" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column label="操作" prop="alertStatus" align="center" width="180px" :formatter="formatTableTDBlank">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="dealRecord('detail', scope.row.templateId, scope.row.id)">详情</el-button>
              <el-button type="text" size="mini" @click="dealRecord('edit', scope.row.templateId, scope.row.id)">编辑</el-button>
              <el-button type="text" size="mini" @click="run(null, scope.row.templateId, 'edit')">运行</el-button>
              <el-button type="text" size="mini" @click="deleteRecord(scope.row.id)" class="red">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 第一层表格分页组件 -->
        <div class="block fr">
          <el-pagination
            @current-change="currentChange"
            @size-change="sizeChange"
            :current-page="recordForm.currentPage"
            :page-sizes="[10,50,100]"
            :page-size="recordForm.pageSize"
            :total="recordForm.total"
            layout="total,sizes,prev,pager,next,jumper">
          </el-pagination>
        </div>
      </el-form>
    </el-card>

    <!-- 心跳 dialog -->
    <el-dialog :visible.sync="dialogVisible" title="心跳列表" width="400px">
      <el-table :data="heartbeatList" size="small">
        <el-table-column prop="ip" label="ip" align="center" :formatter="formatTableTDBlank"></el-table-column>
        <el-table-column prop="lastHeartbeatTime" label="最后心跳时间" align="center" :formatter="formatTableTDBlank"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false" size="small">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 日志 dialog -->
    <el-dialog :visible.sync="logDialogVisible" title="日志" width="800px">
      <el-table :data="logList" size="small">
        <el-table-column prop="content" label="内容详情" align="left" header-align="center" show-overflow-tooltip :formatter="formatTableTDBlank"></el-table-column>
        <el-table-column prop="createTime" label="时间" align="right" header-align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="150px"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer"></span>
    </el-dialog>
  </div>
</template>

<script>
import API from '../../api'
import Utils from 'utils'
export default {
  data() {
    return {
      templateId: '',
      taskRunId: '',
      logDialogVisible: false,
      logList: [],
      heartbeatList: [],
      dialogVisible: false,
      executionList: [],
      loading: false,
      recordForm: {
        templateId: '',
        appName: '',
        // 录制任务模板列表数据
        recordList: [],
        expands1: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        // 第2层查询条件
        recordTaskRunId: '',
        expands2: [],
        // 第3层查询条件
        uriCondition: '',
        currentPage2: 1,
        pageSize2: 10,
        total2: 0,
        currentPage3: 1,
        pageSize3: 10,
        total3: 0,
        // 第4层查询条件
        expands3: [],
        traceIdCondition: '',
        // traceId查询条件的类型
        traceIdUriConditionType: '1',
        currentPage4: 1,
        pageSize4: 10,
        total4: 0,
      },
      showPage2: true,
      showPage3: true,
      showPage4: true,
      // 第一个表格正展开的行
      currentExpandRow1: {},
      // 第二个表格正展开的行
      currentExpandRow2: {},
      // 第三个表格正展开的行
      currentExpandRow3: {}
    }
  },
  watch: {
    recordForm: {
      handler() {
        this.$route.name == 'record' && this.$store.dispatch('localstorage/pageSet', {instance: this})
      },
      deep: true
    }
  },
  created () {
    const localStorage = Utils.getLocalStorage(this.$route.path)
    if (localStorage && localStorage.recordForm && Object.keys(localStorage.recordForm).length) {
      this.recordForm = localStorage.recordForm
    }

    this.search('init')
  },
  methods: {
    search(type, clear) {
      // 如果点击清除筛选条件，清空路由携带条件
      if (clear === 'clear') {
        this.routerPush({
          name: 'record',
          params: {},
          query: {}
        })
      }

      if (type === 1) {
        this.recordForm.currentPage = 1
      }
      let params = {
        pageNum: this.recordForm.currentPage,
        pageSize: this.recordForm.pageSize,
        appName: this.recordForm.appName
      }
      if (this.recordForm.templateId !== '') {
        params.condition = this.recordForm.templateId
      }
      this.loading = true
      API.record.getRecordList(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.recordForm.recordList = res.data.result
          this.recordForm.total = res.data.total
          if (this.recordForm.expands1.length && res.data.result.length) {
            this.recordForm.recordList.forEach(item => {
              if (item.templateId === this.recordForm.expands1[0]) {
                this.rowClick(item, type)
              }
            })            
          }
        } else {
          this.$message.error('查询录制任务列表失败：' + res.msg)
        }
      }, err => {
        this.$message.error(err.msg)
        this.loading = false
      })
    },
    currentChange(val) {
      this.recordForm.currentPage = val
      this.search()
    },
    sizeChange(val) {
      this.recordForm.pageSize = val
      this.search(1)
    },
    rowClick(row, type) {
      this.currentExpandRow1 = row
      if (type === 'init') {
        this.recordForm.expands1 = [row.templateId]
        this.search2(row, type)
      } else {
        if (this.recordForm.expands1.indexOf(row.templateId) < 0) {
          this.recordForm.recordTaskRunId = ''
          this.recordForm.expands1 = []
          this.recordForm.expands2 = []
          this.recordForm.currentPage2 = 1
          this.recordForm.pageSize2 = 10
          this.recordForm.expands3 = []
          this.recordForm.currentPage3 = 1
          this.recordForm.pageSize3 = 10
          this.recordForm.expands1.push(row.templateId)
          this.search2(row)
        } else {
          this.recordForm.expands1 = []
        }
      }
    },
    search2(row, type) {
      // 数据加载
      this.loading = true
      if (type === 1) {
        this.recordForm.currentPage2 = 1
      }
      let params = {
        templateId: row.templateId,
        pageNum: this.recordForm.currentPage2,
        pageSize: this.recordForm.pageSize2,
        recordTaskRunId: this.recordForm.recordTaskRunId
      }
      let that = this
      API.record.getExecutionList(params).then((res) => {
        if (res.code === 'SUCCESS') {
          that.showPage2 = false
          row.executionList = res.data.result || []
          that.recordForm.total2 = res.data.total
          that.showPage2 = true
          if (that.recordForm.expands2.length && res.data.result.length) {
            row.executionList.forEach(item => {
              if (item.taskRunId === that.recordForm.expands2[0]) {
                that.rowClick2(item, type)  
              }
            })
          }
        } else {
          that.$message.error('获取执行历史列表失败:' + res.msg)
        }
        that.loading = false
      }, err => {
        that.loading = false
        this.$message.error(err.msg)
      })
    },
    currentChange2(row) {
      this.closeExpandRow2()
      this.search2(row)
    },
    sizeChange2(row) {
      this.closeExpandRow2()
      this.search2(row, 1)
    },
    closeExpandRow2() {
      this.$refs.table2.toggleRowExpansion(this.currentExpandRow2, false)
    },
    rowClick2(row, type) {
      this.currentExpandRow2 = row
      if (type === 'init') {
        this.recordForm.expands2 = [row.taskRunId]
        this.search3(row, type)
      } else {
        if (this.recordForm.expands2.indexOf(row.taskRunId) < 0) {
          this.recordForm.uriCondition = ''
          this.recordForm.expands2 = []
          this.recordForm.expands2.push(row.taskRunId)
          this.recordForm.expands3 = []
          this.recordForm.currentPage3 = 1
          this.recordForm.pageSize3 = 10
          this.search3(row, undefined)
        } else {
          this.recordForm.expands2 = []
        }
      }
    },
    search3(row, type) {
      if (type === 1) {
        this.recordForm.currentPage3 = 1
      }
      let params = {
        taskRunId: row.taskRunId,
        pageNum: this.recordForm.currentPage3,
        pageSize: this.recordForm.pageSize3,
        uriCondition: this.recordForm.uriCondition
      }
      this.loading = true
      API.record.getUriDataCountList(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.showPage3 = false
          row.tableData3 = res.data.result
          this.recordForm.total3 = res.data.total
          this.showPage3 = true
          if (this.recordForm.expands3.length && row.tableData3.length) {
            row.tableData3.forEach(item => {
              if (item.recordUri === this.recordForm.expands3[0]) {
                this.rowClick3(item, type, item.recordTaskRunId)
              }
            })            
          }
        } else {
          this.$message.error('获取列表失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error(err.msg)
      })
    },
    currentChange3(row) {
      this.closeExpandRow3()
      this.search3(row, undefined)
    },
    sizeChange3(row) {
      this.closeExpandRow3()
      this.search3(row, 1)
    },
    closeExpandRow3() {
      this.$refs.table3.toggleRowExpansion(this.currentExpandRow3, false)
    },
    rowClick3(row, type, taskRunId) {
      this.currentExpandRow3 = row
      if (type === 'init') {
        this.recordForm.expands3 = [row.recordUri]
        this.search4(row, undefined, taskRunId)
      } else {
        if (this.recordForm.expands3.indexOf(row.recordUri) < 0) {
          this.recordForm.traceIdCondition = ''
          this.recordForm.traceIdUriConditionType = '1'
          this.recordForm.expands3 = []
          this.recordForm.expands3.push(row.recordUri)
          this.recordForm.currentPage4 = 1
          this.recordForm.pageSize4 = 10
          this.search4(row, undefined, taskRunId)
        } else {
          this.recordForm.expands3 = []
        }
      }
    },
    search4(row, num, taskRunId) {
      if (num === 1) {
        this.recordForm.currentPage4 = 1
      }
      let params = {
        pageNum: this.recordForm.currentPage4,
        pageSize: this.recordForm.pageSize4,
        taskRunId: row.taskRunId || taskRunId,
        traceIdCondition: this.recordForm.traceIdCondition,
        uri: encodeURIComponent(row.recordUri)
      }
      this.loading = true
      API.record.getUriTaskDataList(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.showPage4 = false
          row.tableData4 = res.data.result
          this.recordForm.total4 = res.data.total
          this.showPage4 = true
        } else {
          this.$message.error('获取列表失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error(err.msg)
      })
    },
    execute(row) {
      let params = {
        taskRunId: row.taskRunId
      }
      this.loading = true
      API.record.execute(params).then((res) => {
        if (res.code === 'SUCCESS') {
          this.$message.success('执行成功')
          this.search2(row, 1)
        } else {
          this.$message.error('执行失败：' + res.msg)
        }
        this.loading = false
      }, err => {
        this.loading = false
        this.$message.error(err.msg)
      })
    },
    heartbeatSearch(taskRunId) {
      API.playback.getHeartbeatList({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.dialogVisible = true
          this.heartbeatList = res.data || []
        } else {
          this.$message.error('获取心跳列表失败：' + res.msg)
        }
      }, err => {
        this.$message.error(err.msg)
      })
    },
    logSearch(taskRunId) {
      this.logDialogVisible = true
      API.record.getLogList({ taskRunId }).then((res) => {
        if (res.code === 'SUCCESS') {
          if (res.data.result !== null) {
            this.logList = res.data || []
          } else {
            this.logList = []
          }
        } else {
          this.$message.error('获取日志列表失败：' + res.msg)
        }
      }, err => {
        this.$message.error(err.msg)
      })
    },
    currentChange4(row, taskRunId) {
      if (this.recordForm.currentPage4 * this.recordForm.pageSize4 >= 10000) {
        this.$message.warning('暂时不支持深度分页, 请使用搜索功能')
        return
      }
      this.search4(row, undefined, taskRunId)
    },
    sizeChange4(row, taskRunId) {
      this.search4(row, 1, taskRunId)
    },
    deleteRecord(id) {
      this.$confirm('您确定要删除该条数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.loading = true
          API.record.delete({ id }).then((res) => {
            this.loading = false
            if (res.code === 'SUCCESS') {
              this.$message.success('删除成功')
              this.search()
            } else {
              this.$message.error('删除录制任务失败：' + res.msg)
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          })
        })
    },
    dealRecord(type, templateId = 0, id = 0) {
      let titleName = '新增录制'
      switch (type) {
        case 'edit':
          titleName = '录制编辑'
          break;
        case 'detail':
          titleName = '录制详情'
          break;
      }
      this.routerPush({
        name: 'addRecord',
        params: { type, id, templateId },
        query: { titleName },
      })
    },
    replay({ taskRunId }) {
      let params = {
        type: 'add',
        recordTaskRunId: taskRunId,
      }
      this.routerPush({
        name: `addReplay`,
        params,
        query: { titleName: '新增回放' }
      })
    },
    replayList(taskRunId) {
      this.routerPush({
        name: 'playback',
        query: {
          recordTaskRunId: taskRunId
        },
        params: {}
      })
    },
    // 运行
    run(taskRunId, templateId, type) {
      let titleName = '运行配置'
      if (type === 'detail') {
        titleName = '运行详情'
      }
      this.routerPush({
        name: `executionConfig`,
        params: { taskRunId, templateId, type },
        query: { titleName }
      })
    },
    // 停止
    stop(taskRunId, row) {
      this.loading = true
      API.record.stop({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('操作成功')
          this.search2(row)
        } else {
          this.$message.error('操作失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error(err.msg)
      })
    },

    flowDetail({ traceId, recordTaskRunId }, invokeType) {
      this.routerPush({
        name: `${invokeType}Detail`,
        params: {
          traceId,
          taskRunId: recordTaskRunId
        },
        query: {}
      })
    },

    deleteFlow(row, { traceId, recordTaskRunId }) {
      this.$confirm('您确定要删除该条数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        let params = {
          traceId,
          taskRunId: recordTaskRunId
        }
        this.loading = true
        API.record.deleteFlow(params).then((res) => {
          this.loading = false
          if (res.code === 'SUCCESS') {
            this.$message.success('删除成功')
            this.search4(row, undefined, recordTaskRunId)
          } else {
            this.$message.error('删除流量失败：' + res.msg)
          }
        }, err => {
          this.loading = false
          this.$message.error(err.msg)
        })
      })
      .catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除',
        })
      })
    },
    //删除运行任务
    deleteTask(taskRunId, row) {
      this.$confirm('您确定要删除该条数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.loading = true
        API.record.deleteTask({ taskRunId }).then((res) => {
          this.loading = false
          if (res.code === 'SUCCESS') {
            this.$message.success('删除成功')
            this.search2(row, 1)
            this.$forceUpdate()
          } else {
            this.$message.error('删除流量失败：' + res.msg)
          }
        }, err => {
          this.loading = false
          this.$message.error(err.msg)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除',
        })
      })
    },
    //重新录制任务
    tryTask(taskRunId, row1) {
      this.loading = true
      API.record.tryTask({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('重新录制成功')
          this.search2(row1, 1)
        } else {
          this.$message.error('重新录制失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error(err.msg)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.breadcrumb {
  font-size: 20px;
}

.el-card {
  .app-container {
    padding: 10px;
  }
}

.el-table .success-row {
  background: #f0f9eb;
}

.form-item-margin-zero {
  margin-top: 0;
  margin-bottom: 0;
}

main {
  height: 100%;
  .container {
    height: 100%;
    .errorpage {
      height: 100%;
    }
  }
}
</style>
