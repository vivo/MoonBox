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
      <el-form :inline="true" v-model="playbackForm" class="demo-ruleForm">
        <el-form-item label="回放任务执行编码">
          <el-input v-model="playbackForm.replayTaskRunId" @clear="search_handler1(1, 'clear')" clearable size="small" placeholder="请输入" style="width: 300px;"></el-input>
        </el-form-item>
        <el-form-item label="录制任务执行编码">
          <el-input v-model="playbackForm.recordTaskRunId" @clear="search_handler1(1, 'clear')" clearable size="small" placeholder="请输入" style="width: 300px;"></el-input>
        </el-form-item>
        <el-form-item label="应用名称">
          <el-input v-model="playbackForm.appName" size="small" style="width: 300px" placeholder="请输入" :clearable="true" @clear="search_handler1(1, 'clear')"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button size="small" type="primary" @click="search_handler1(1)" icon="el-icon-search">查询</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="content">
      <!-- 第一层表格：width: 99.9%避免滚动条出现、隐藏导致表格闪动问题 -->
      <el-table
        @expand-change="expandClick1(arguments[0], arguments[1], 1)"
        :expand-row-keys="playbackForm.expands1"
        :data="playbackForm.tableData1"
        :header-cell-style="tableHeaderStyle"
        v-loading="loading"
        ref="table1"
        row-key="taskRunId"
        size="small"
        border>
        <el-table-column type="expand">
          <template slot-scope="prop">
            <el-form :inline="true" class="demo-form-inline">
              <el-form-item label="接口uri">
                <el-input v-model="playbackForm.search2_ipt" @clear="search_handler2(prop.row, 1)" size="small" clearable></el-input>
              </el-form-item>
              <el-form-item label="回放结果">
                <el-select v-model="playbackForm.runStatus" @clear="search_handler2(prop.row, 1)" placeholder="请选择" size="small" clearable>
                  <el-option label="全部成功" :value="1"></el-option>
                  <el-option label="部分成功" :value="2"></el-option>
                  <el-option label="全部失败" :value="3"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button size="small" type="primary" @click="search_handler2(prop.row, 1)" icon="el-icon-search">查询</el-button>
              </el-form-item>
            </el-form>
            <!-- 第二层表格 -->
            <el-table
              @expand-change="expandClick2(arguments[0], arguments[1], 1)"
              :expand-row-keys="playbackForm.expands2"
              :data="playbackForm.tableData2"
              :header-cell-style="tableHeaderStyle"
              ref="table2"
              row-key="replayUri"
              size="small"
              border>
              <el-table-column type="expand">
                <template slot-scope="props">
                  <el-form :inline="true" class="demo-form-inline">
                    <el-form-item label="traceId">
                      <el-input v-model="playbackForm.search3_ipt" @clear="search_handler3(props.row, 1)" size="small" :clearable="true"></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-button size="small" type="primary" @click="search_handler3(props.row, 1)" icon="el-icon-search">查询</el-button>
                    </el-form-item>
                  </el-form>
                  <!--第三层表格-->
                  <el-table
                    :data="playbackForm.tableData3"
                    :header-cell-style="tableHeaderStyle"
                    ref="table3"
                    size="small"
                    border>
                    <template>
                      <el-table-column label="回放traceId" prop="replayTraceId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="原始traceId" prop="recordTraceId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放时间" prop="replayTime" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="回放结果" prop="replayCode" align="center" width="100">
                        <template slot-scope="scope">
                          <span :class="[ scope.row.replayStatus === 0 ? 'green' : 'red' ]">{{ scope.row.replayCode }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="结果描述" prop="replayMessage" align="center" width="140" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                      <el-table-column label="操作" align="center" width="100">
                        <template slot-scope="scope">
                          <el-button type="text" @click="flowDetail(scope.row, props.row.invokeType)" size="mini">详情</el-button>
                        </template>
                      </el-table-column>
                    </template>
                  </el-table>
                  <div class="block fr">
                    <el-pagination
                      v-if="showPage3"
                      @current-change="currentChange3(props.row, arguments[0])"
                      @size-change="sizeChange3(props.row, arguments[0])"
                      :current-page.sync="playbackForm.currentPage3"
                      :page-sizes="[6,10,20]"
                      :page-size.sync="playbackForm.pageSize3"
                      :total="playbackForm.total3"
                      layout="total,sizes,prev,pager,next,jumper">
                    </el-pagination>
                  </div>
                </template>
              </el-table-column>
              <template>
                <el-table-column label="回放接口" prop="replayUri" align="left" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                <el-table-column label="接口协议" prop="invokeType" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                <el-table-column label="接口回放结果" align="center" :formatter="formatTableTDBlank">
                  <template slot-scope="scope">
                    <span size="mini" v-if="scope.row.successCount === scope.row.replayCount" class="blue">全部成功</span>
                    <span size="mini" v-if="scope.row.successCount !==0 && scope.row.successCount < scope.row.replayCount" class="orange">部分成功</span>
                    <span size="mini" v-if="scope.row.successCount === 0" class="red">全部失败</span>
                  </template>
                </el-table-column>
                <el-table-column label="成功/回放数据总量" align="center" :formatter="formatTableTDBlank">
                  <template slot-scope="scope">
                    <span>{{ scope.row.successCount ? scope.row.successCount : 0 }} / {{ scope.row.replayCount ? scope.row.replayCount : 0 }}</span>
                  </template>
                </el-table-column>
              </template>
            </el-table>
            <div class="block fr">
              <el-pagination
                v-if="showPage2"
                @current-change="currentChange2(prop.row, arguments[0])"
                @size-change="sizeChange2(prop.row, arguments[0])"
                :current-page.sync="playbackForm.currentPage2"
                :page-sizes="[6,10,20]"
                :page-size.sync="playbackForm.pageSize2"
                :total="playbackForm.total2"
                layout="total,sizes,prev,pager,next,jumper">
              </el-pagination>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="回放任务执行编码" prop="taskRunId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="录制任务执行编码" prop="recordTaskRunId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank">
          <template slot-scope="scope">
            <a href="javascript:;" @click="goRecord(scope.row)" style="text-decoration: underline">{{ scope.row.recordTaskRunId }}</a>
          </template>
        </el-table-column>
        <el-table-column label="录制模板编码" prop="recordTemplateId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column v-if="showColume('runEnv')" label="回放执行环境" prop="runEnv" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="70" min-width="70"/>
        <el-table-column v-if="showColume('appName')" label="应用名称" prop="appName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="120"/>
        <el-table-column v-if="showColume('agentStatusMsg')" label="agent状态" prop="agentStatusMsg" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="100"></el-table-column>
        <el-table-column v-if="showColume('runStatusMsg')" prop="runStatusMsg" label="执行状态" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="90">
          <template slot-scope="scope">
            <span v-if="scope.row.runStatus == 2" class="blue">{{scope.row.runStatusMsg}}</span>
            <span v-if="scope.row.runStatus == 3" class="green">{{scope.row.runStatusMsg}}</span>
            <span v-if="scope.row.runStatus == 4" class="red">{{scope.row.runStatusMsg}}</span>
            <span v-if="scope.row.runStatus == 6" class="orange">{{scope.row.runStatusMsg}}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="showColume('runDesc')" label="描述" prop="runDesc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank">
          <template slot-scope="scope">
            <span v-if="scope.row.runDesc">{{scope.row.runDesc}}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column v-if="showColume('taskStartTime')" label="开始时间" prop="taskStartTime" align="center" width="90" :formatter="formatTableTDBlank"/>
        <el-table-column v-if="showColume('taskEndTime')" label="完成时间" prop="taskEndTime" align="center" width="90" :formatter="formatTableTDBlank"/>
        <el-table-column label="更新人" prop="updateUser" align="center" width="80px" :formatter="formatTableTDBlank">
          <template slot-scope="scope">
            <div>{{ scope.row.updateUser }}</div>
            <div v-if="scope.row.updateUserName">{{ scope.row.updateUserName }}</div>
          </template>
        </el-table-column>
        <el-table-column align="center" width="350px">
          <template slot="header">
            <span @click="drawer = true" class="handlerColume">
              <span>操作</span>
              <i class="el-icon-setting"></i>
            </span>
          </template>
          <template slot-scope="scope">
            <el-button @click="runDetail(scope.row)" type="text" size="mini" >详情</el-button>
            <el-button @click="logSearch(scope.row.taskRunId)" type="text" size="mini">日志</el-button>
            <el-button @click="heartbeatSearch(scope.row.taskRunId)" v-if="scope.row.agentStatus === 1" type="text" size="mini">心跳列表</el-button>
            <el-button @click="stop(scope.row.taskRunId)" v-if="scope.row.runStatus === 1 || scope.row.runStatus === 2 || scope.row.runStatus === 3 || scope.row.runStatus === 9" type="text" size="mini" class="red">停止</el-button>
            <el-button @click="execute(scope.row.taskRunId)" v-if="scope.row.runStatus === 9" type="text" size="mini">执行</el-button>
            <el-button @click="replayFail(scope.row)" v-if="scope.row.runStatus === 4 || scope.row.runStatus === 5 || scope.row.runStatus === 6" type="text" size="mini">失败回放</el-button>
            <el-button @click="rePlayback(scope.row)" v-if="scope.row.runStatus === 4 || scope.row.runStatus === 5 || scope.row.runStatus === 6" type="text" size="mini">重新回放</el-button>
            <el-button @click="deleteTask(scope.row.taskRunId)" v-if="scope.row.runStatus === 4 || scope.row.runStatus === 5 || scope.row.runStatus === 6" type="text" size="mini" class="red">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="block fr">
        <el-pagination
          @current-change="currentChange1"
          @size-change="sizeChange1"
          :current-page="playbackForm.currentPage1"
          :page-sizes="[10,50,100]"
          :page-size="playbackForm.pageSize1"
          :total="playbackForm.total1"
          layout="total,sizes,prev,pager,next,jumper">
        </el-pagination>
      </div>
    </el-card>

    <el-dialog :visible.sync="dialogVisible" width="400px" title="心跳列表" >
      <el-table :data="heartbeatList" size="small">
        <el-table-column prop="ip" label="ip" align="center" :formatter="formatTableTDBlank"></el-table-column>
        <el-table-column prop="lastHeartbeatTime" label="最后心跳时间" align="center" :formatter="formatTableTDBlank"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="logDialogVisible" title="日志" width="800px" top="50px">
      <el-table :data="logList" size="small" class="logTable">
        <el-table-column prop="content" label="内容详情" align="left" header-align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column prop="createTime" label="时间" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="150"/>
      </el-table>
      <span slot="footer" class="dialog-footer">
      </span>
    </el-dialog>

    <el-drawer
      title="请勾选表格展示的列"
      :visible.sync="drawer"
      :with-header="false"
      :show-close="false"
      size="230">
      <div>
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAllColumes" @change="checkAllColHandler" class="checkAllColumes">全选</el-checkbox>
        <el-checkbox-group v-model="checkColumes" @change="checkColHandler">
          <el-checkbox
            v-for="item in columeList"
            :key="item.prop"
            :label="item.prop">
            {{ item.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import API from '../../api'
import Utils from '@/utils'
export default {
  data () {
    return {
      loading: false,
      playbackForm: {
        // 表格1
        recordTaskRunId: '',
        replayTaskRunId: '',
        appName: '',
        tableData1: [],
        expands1: [],
        currentPage1: 1,
        pageSize1: 10,
        total1: 0,
        // 表格2
        search2_ipt: '',
        runStatus: '',
        tableData2: [],
        expands2: [],
        currentPage2: 1,
        pageSize2: 10,
        total2: 0,
        // 表格3
        search3_ipt: '',
        tableData3: [],
        currentPage3: 1,
        pageSize3: 10,
        total3: 0,
      },
      showPage2: false,
      showPage3: false,
      dialogVisible: false,
      heartbeatList: [],
      logDialogVisible: false,
      logList: [],
      checkColumes: ['runEnv', 'appName', 'agentStatusMsg', 'runStatusMsg', 'runDesc', 'taskStartTime', 'taskEndTime'],
      columeList: [
        { label: '回放执行环境', prop: 'runEnv' },
        { label: '应用名称', prop: 'appName' },
        { label: 'agent状态', prop: 'agentStatusMsg' },
        { label: '执行状态', prop: 'runStatusMsg' },
        { label: '描述', prop: 'runDesc' },
        { label: '开始时间', prop: 'taskStartTime' },
        { label: '完成时间', prop: 'taskEndTime' }
      ],
      checkAllColumes: true,
      isIndeterminate: false,
      drawer: false,
    }
  },
  watch: {
    playbackForm: {
      handler(val) {
        this.$route.name == 'playback' && this.$store.dispatch('localstorage/pageSet', {instance: this})
      },
      deep: true
    }
  },
  created () {
    const localStorage = Utils.getLocalStorage(this.$route.path)
    if (localStorage && localStorage.playbackForm && Object.keys(localStorage.playbackForm).length) {
      this.playbackForm = localStorage.playbackForm
    }
    
    // 处理路由中通过query传参
    if (Object.keys(this.$route.query).length) {
      this.dealRouteQuery()
    }

    this.search_handler1()
  },
  methods: {
    dealRouteQuery () {
      // 新增的参数直接加入结构集合，将参数赋值给对应的筛选条件
      const { taskRunId, timerTaskId, runStartTime, runEndTime, recordTaskRunId } = this.$route.query
      this.playbackForm.search1_ipt = taskRunId || ''
      this.playbackForm.expands1 = taskRunId ? [taskRunId] : []
      this.playbackForm.timerTaskId = timerTaskId || ''
      this.playbackForm.runStartTime = runStartTime || ''
      this.playbackForm.runEndTime = runEndTime || ''
      this.playbackForm.recordTaskRunId = recordTaskRunId || ''
    },

    // 查询1
    search_handler1 (num, clear) {
      // 如果点击清除筛选条件，清空路由携带条件
      if (clear === 'clear') {
        this.routerPush({
          name: 'playback',
          params: {},
          query: {}
        })
      }
      let params = {
        pageNum: num === 1 ? num : this.playbackForm.currentPage1,
        pageSize: this.playbackForm.pageSize1,
        recordTaskRunId: this.playbackForm.recordTaskRunId,
        replayTaskRunId: this.playbackForm.replayTaskRunId,
        appName: this.playbackForm.appName
      }
      this.playbackForm.tableData1 = []
      this.loading = true
      API.playback.getExecutionList(params).then(res => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          if (res.data.result !== null) {
            this.playbackForm.tableData1 = res.data.result
          }
          // 如果路由拼接了taskRunId，将搜索到的数据行展开
          if (this.$route.query.taskRunId && this.playbackForm.tableData1.length) {
            this.playbackForm.expands1 = [this.$route.query.taskRunId]
            this.expandClick1(this.playbackForm.tableData1[0], this.playbackForm.expands1)
          }
          // 如果路由没有拼接了taskRunId，且expands1从缓存取到了数据，将对应的数据行展开
          if (!this.$route.query.taskRunId && this.playbackForm.expands1.length) {
            this.playbackForm.tableData1.forEach(item => {
              if (item.taskRunId === this.playbackForm.expands1[0]) {
                this.expandClick1(item, this.playbackForm.expands1) 
              }
            })
          }

          this.playbackForm.total1 = res.data.total
        } else {
          this.$message.error('获取执行历史列表失败:' + res.msg)
        }
      })
    },
    // 展开1
    expandClick1 (row, flag, resetPageNum) {
      if (flag.length) {
        if (resetPageNum) {
          this.playbackForm.search2_ipt = ''
          this.playbackForm.runStatus = ''
        }
        this.playbackForm.expands1 = [row.taskRunId]
        this.search_handler2(row, resetPageNum)
      } else {
        this.playbackForm.expands1 = []
      }
    },
    // 查询2
    search_handler2 (row, resetPageNum) {
    // 加载数据
      this.loading = true
      let params = {
        replayTaskRunId: row.taskRunId,
        pageNum: resetPageNum || this.playbackForm.currentPage2,
        pageSize: this.playbackForm.pageSize2
      }
      if (this.playbackForm.search2_ipt) {
        params.uriCondition = this.playbackForm.search2_ipt
      }
      if (this.playbackForm.runStatus) {
        params.status = this.playbackForm.runStatus
      }
      API.playback.getUriReplayListJava(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          let data = []
          data = res.data && res.data.result || []
          if (data && data.length) {
            this.showPage2 = false
            data.forEach(item => {
              if (item.replayUri === this.playbackForm.expands2[0]) {
                this.expandClick2(item, this.playbackForm.expands2, undefined) 
              }
            })
            this.showPage2 = true
          }
          this.playbackForm.tableData2 = data
          this.playbackForm.total2 = res.data && res.data.total
        } else {
          this.$message.error('获取列表失败:' + res.msg)
        }
      })
    },
    // 展开2
    expandClick2 (row, flag, resetPageNum) {
      if (flag.length) {
        if (resetPageNum) {
          this.playbackForm.search3_slt = '1'
          this.playbackForm.search3_ipt = ''
        }
        this.playbackForm.expands2 = [row.replayUri]
        this.search_handler3(row, resetPageNum)
      } else {
        this.playbackForm.expands2 = []
      }
    },
    // 查询3
    search_handler3 (row, resetPageNum) {
      let params = {
        pageNum: resetPageNum || this.playbackForm.currentPage3,
        pageSize: this.playbackForm.pageSize3,
        replayUri: row.replayUri,
        replayTaskRunId: row.replayTaskRunId,
        traceIdCondition: this.playbackForm.search3_ipt
      }
      this.loading = true
      API.playback.getUriReplayDataList(params).then((res) => {
        this.loading = false
        this.playbackForm.tableData3 = []
        if (res.code === 'SUCCESS') {
          if (res.data.result !== null) {
            this.showPage3 = false
            this.playbackForm.tableData3 = res.data.result
            this.showPage3 = true
          }
          this.playbackForm.total3 = res.data.total
        } else {
          this.$message.error('获取列表失败:' + res.msg)
        }
      })
    },
    runDetail ({ recordTaskRunId, taskRunId }) {
      let params = {
        type: 'detail',
        recordTaskRunId,
        replayTaskRunId: taskRunId
      }
      this.routerPush({
        name: `addReplay`,
        params,
        query: {titleName: `回放详情`},
      })
    },
    logSearch (taskRunId) {
      this.logDialogVisible = true
      API.playback.getLogList({ taskRunId }).then((res) => {
        if (res.code === 'SUCCESS') {
          this.logList = res.data || []
        } else {
          this.$message.error('获取日志列表失败:' + res.msg)
        }
      })
    },
    goRecord ({ taskId, recordTaskRunId }) {
      this.routerPush({
        name: 'record',
        query: {taskId, recordTaskRunId },
        params: {},
        fullPath: '/record/index'
      })
    },
    heartbeatSearch (taskRunId) {
      API.playback.getHeartbeatList({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.dialogVisible = true
          this.heartbeatList = res.data || []
        } else {
          this.$message.error('获取心跳列表失败:' + res.msg)
        }
      })
    },
    stop (taskRunId) {
      this.loading = true
      API.playback.stop({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('操作成功')
          this.search_handler1(1)
        } else {
          this.$message.error('操作失败:' + res.msg)
        }
      })
    },
    execute(taskRunId) {
      let params = {}
      params.taskRunId = taskRunId
      API.playback.execute(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('操作成功')
          this.search_handler1(1)
        } else {
          this.$message.error('操作失败:' + res.msg)
        }
      })
    },
    replayFail ({ taskRunId }) {
      API.playback.failPlayFail({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('失败回放成功')
          this.search_handler1(1)
        } else {
          this.$message.error('失败回放失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error('失败回放失败：' + err.msg)
      })
    },
    rePlayback ({ taskRunId }) {
      API.playback.rePlayback({ taskRunId }).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.$message.success('重新回放成功')
          this.search_handler1(1)
        } else {
          this.$message.error('重新回放失败：' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error('重新回放失败：' + err.msg)
      })
    },
    deleteTask(taskRunId) {
      this.$confirm('您确定要删除该条回放吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.loading = true
        API.playback.deleteTask({ taskRunId }).then((res) => {
          this.loading = false
          if (res.code === 'SUCCESS') {
            this.$message.success('删除成功')
            this.search_handler1(1)
            this.$forceUpdate()
          } else {
            this.$message.error('回放任务删除失败：' + res.msg)
          }
        }, err => {
          this.loading = false
          this.$message.error('回放任务删除失败：' + err.msg)
        })
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消删除', })
      })
    },
    flowDetail({ replayTaskRunId, replayTraceId }, invokeType) {
      this.routerPush({
        name: `playback${invokeType}Detail`,
        params: {
          replayTaskRunId,
          replayTraceId,
        }
      })
    },
    currentChange1 (val) {
      this.playbackForm.currentPage1 = val
      this.playbackForm.expands1 = []
      this.playbackForm.expands2 = []
      this.search_handler1()
    },
    sizeChange1 (val) {
      this.playbackForm.pageSize1 = val
      this.playbackForm.expands1 = []
      this.playbackForm.expands2 = []
      this.search_handler1(1)
    },
    currentChange2 (row, val) {
      this.playbackForm.currentPage2 = val
      this.playbackForm.expands2 = []
      this.search_handler2(row)
    },
    sizeChange2 (row, val) {
      this.playbackForm.pageSize2 = val
      this.playbackForm.expands2 = []
      this.search_handler2(row, 1)
    },
    currentChange3(row, val) {
      if (this.playbackForm.currentPage3 * this.playbackForm.pageSize3 >= 10000) {
        this.$message.warning('暂时不支持深度分页, 请使用搜索功能')
        return
      }
      this.playbackForm.currentPage3 = val
      this.search_handler3(row, undefined)
    },
    sizeChange3(row, val) {
      this.playbackForm.pageSize3 = val
      this.search_handler3(row, 1)
    },
    checkColHandler (val) {
      let checkedCount = val.length
      this.checkAllColumes = checkedCount === this.columeList.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columeList.length
      // 重新渲染表格，解决tbody于thead错位问题
      let data = JSON.parse(JSON.stringify(this.playbackForm.tableData1))
      this.playbackForm.tableData1 = []
      this.$nextTick(() => {
        this.playbackForm.tableData1 = data
      })
    },
    checkAllColHandler (val) {
      let all = this.columeList.map(item => item.prop)
      this.checkColumes = val ? all : []
      this.isIndeterminate = false
      // 重新渲染表格，解决tbody于thead错位问题
      let data = JSON.parse(JSON.stringify(this.playbackForm.tableData1))
      this.playbackForm.tableData1 = []
      this.$nextTick(() => {
        this.playbackForm.tableData1 = data
      })
    },
    showColume (col) {
      return this.checkColumes.some(item => item === col)
    }
  }
}
</script>

<style lang="scss" scoped>
.content {
  position: relative;
  .checkColumes {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 1;
  }
  .el-table .success-row {
    background: #f0f9eb;
  }
  .handlerColume {
    cursor: pointer;
    .el-icon-setting {
      margin-left: 5px;
    }
  }
}

.logTable {
  max-height: calc(70vh);
}

/deep/ .el-drawer {
  .el-drawer__body {
    padding: 0 20px;
    .el-checkbox {
      display: block;
      line-height: 2;
    }
    .checkAllColumes {
      margin-bottom: 20px;
    }
  }
}

</style>
