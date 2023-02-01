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
  <div v-loading="loading" class="addReplay">
    <el-card>
      <div slot="header">
        <h4>基本信息</h4>
      </div>
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item label="描述：">
          <el-input v-model="runDesc" :disabled="detailFlag" size="small"></el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div slot="header">
        <h4>回放数据配置</h4>
      </div>
      <el-form :inline="true" class="demo-form-inline">
      <el-row>
        <el-col :span="20">
          <div style="float:left">
            <el-form-item style="float:left" class="form-item-margin-zero">
              <el-input v-model="queryCondition" style="width: 360px" size="small" :clearable="true" @clear="queryList('click')">
                <el-select v-model="queryConditionType" size="small" slot="prepend" placeholder="请选择" style="width: 100px">
                  <el-option label="接口uri" value="1"></el-option>
                  <el-option label="traceId" value="2"></el-option>
                </el-select>
              </el-input>
            </el-form-item>
            <el-button
              @click="queryList('click')"
              icon="el-icon-search"
              type="primary"
              size="small">查询</el-button>
            <el-button
              v-if="!queryCondition && total===allTotal"
              @click="checkedAll"
              :icon="isAll.value ? 'el-icon-close' : 'el-icon-check'"
              :type="isAll.value ? 'info' : 'success'"
              size="small">{{ isAll.value ? '取消全选' : '全部选择' }}</el-button>
          </div>
        </el-col>
      </el-row>
      </el-form>
      <div class="tableArea">
        <el-checkbox class="checkedAllThisPage" :indeterminate="indeterminate" v-model="checkedAllThisPage" @change="checkedAllThisPageChange"></el-checkbox>
        <el-table size="mini" :data="tableData" :header-cell-style="tableHeaderStyle" :border="true" row-key="traceId">
          <el-table-column prop="checked" width="50px" align="center">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.checked" @change.native="checkboxChange($event, scope.row.traceId)"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column prop="uri" label="接口uri" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column prop="traceId" label="traceId" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column prop="host" label="采集机器" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
          <el-table-column prop="recordTime" label="采集时间" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="180px"/>
          <el-table-column label="详情" align="center" :formatter="formatTableTDBlank" width="100px">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="queryTraceIdDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="block fr">
        <el-pagination
          @current-change="currentChange"
          @size-change="sizeChange"
          :current-page="pageNum"
          :page-sizes="[6,10,20]"
          :page-size="pageSize"
          :total="total"
          layout="total,sizes,prev,pager,next,jumper">
        </el-pagination>
      </div>
    </el-card>
    <!-- 执行机器卡片 -->
    <el-card>
      <div slot="header">
        <h4>执行机器</h4>
      </div>
      <env-mechine ref="envMechine" :formData="formData" :detailFlag="detailFlag"></env-mechine>
    </el-card>
    <!-- 插件区域 -->
    <el-card>
      <div slot="header">
        <h4 style="display: inline-block; marginRight: 10px;">高级选项</h4>
        <el-checkbox v-model="advanceOptionCheckFlag" @change="changeOption('statusOption', arguments[0])"/>
      </div>
      <el-form v-if="statusOption" class="demo-form-inline">
        <el-form-item label-width="160px">
          <template slot="label">
            <el-checkbox v-model="chooseAll" @change="chooseToggle" class="chooseAll" :disabled="detailFlag" style="marginRight: 30px"></el-checkbox>
            <el-tooltip placement="top">
              <div slot="content">
                1、开启插件后， 插件会自动拦截流量请求调用入参和出参情况<br/>
                2、绿色代表入口插件，蓝色代表子调用插件
              </div>
              <i class="el-icon-question"/>
            </el-tooltip>
            请选择插件：
          </template>
          <el-checkbox-group v-model="plugins" :disabled="detailFlag" class="subInvocationPlugins">
            <template v-for="item in pluginsView">
              <el-checkbox
                :key="item.name"
                :label="item.name"
                :class="{ 'isGreen': (item.name == 'http' || item.name == 'dubbo-provider' || item.name == 'java-entrance') && pluginIsChecked(item.name) }">
                <span :class="{ 'isGreen': item.name == 'http' || item.name == 'dubbo-provider' || item.name == 'java-entrance' }">{{ item.name }}</span>
              </el-checkbox>
            </template>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label-width="160px">
          <template slot="label">
            <el-tooltip placement="top" content="sandbox日志会出输出到业务项目日志中心">
              <i class="el-icon-question" />
            </el-tooltip>
            sandbox日志级别：
          </template>
          <el-select v-model="sandboxLogLevel" placeholder="请选择" size="small" :disabled="detailFlag">
            <el-option v-for="item in logOptions" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label-width="160px">
          <template slot="label">
            <el-tooltip placement="top" content="repeater日志会出输出到业务项目日志中心">
              <i class="el-icon-question" />
            </el-tooltip>
            repeater日志级别：
          </template>
          <el-select v-model="repeaterLogLevel" placeholder="请选择" size="small" :disabled="detailFlag">
            <el-option v-for="item in logOptions" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="add-record-bottom-component">
      <el-button type="primary" @click="execution" :disabled="detailFlag" size="small">执行</el-button>
      <el-button @click="resetFormAndClose()" size="small" v-if="type == 1">取消</el-button>
    </el-card>
  </div>
</template>

<script>
import API from '../../api'
import envMechine from '../components/envMechine.vue'
export default {
  components: {
    envMechine
  },
  data() {
    return {
      type: '',
      replayTaskRunId: '',
      recordTaskRunId: '',
      loading: false,
      runDesc: '',
      queryCondition: '',
      queryConditionType: '1',
      detailFlag: false,
      checkedAllThisPage: false,
      indeterminate: false,
      tableData: [],
      checkedList: [],
      cancelCheckList: [],
      checkedNums: 0,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      allTotal: 0,
      formData: null,
      advanceOptionCheckFlag: true,
      statusOption: true,
      chooseAll: true,
      plugins: [],
      sandboxLogLevel: 'OFF',
      repeaterLogLevel: 'OFF',
      logOptions: [
        'TRACE',
        'DEBUG',
        'INFO',
        'WARN',
        'ERROR',
        'FATAL',
        'OFF'
      ],
      pluginsView: [],
      isAll: {
        value: false,
        click: false
      }
    }
  },
  watch: {
    checkedNums: {
      handler (num) {
        if (this.total && this.total === this.allTotal) {
          this.isAll.value = num === this.total
        }
      },
      immediate: true
    },
    plugins: {
      handler (v) {
        this.chooseAll = v.length === this.pluginsView.length
      },
      immediate: true
    }
  },
  computed: {
    pluginIsChecked () {
      return function (plugin) {
        return this.plugins.some(item => item === plugin)
      }
    }
  },
  created() {
    this.replayTaskRunId = this.$route.params.replayTaskRunId
    this.recordTaskRunId = this.$route.params.recordTaskRunId
    this.type = this.$route.params.type
    if (this.type == 'add') {
      this.isAll.value = true
    }
    if (this.type == 'detail') {
      this.detailFlag = true
    }
    this.getPlugins()
    this.queryList('init')
  },

  methods: {
    queryList (type) {
      if (type === 'click') {
        this.pageNum = 1
      }
      this.loading = true
      let params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        taskRunId: this.recordTaskRunId,
      }
      if (this.queryCondition !== '') {
        if (this.queryConditionType == '1') {
          params.uri = this.queryCondition.trim()
        } else {
          params.traceIdCondition = this.queryCondition
        }
      }
      API.playback.getTraceIdListAllByTaskRunId(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
            res.data.result.forEach(item => {
              item.checked = false
            })
            this.tableData = res.data.result
            this.total = res.data.total
            if (type === 'init') {
              this.allTotal = res.data.total
            }
            if (this.type == 'add') {
              this.checkedAll('checkedAll')
            }
            if (this.type == 'rePlayback' || this.type == 'detail') {
              this.getDetail()
            }
        } else {
          this.loading = false
          this.$message.error('获取traceId列表失败')
        }
      }, err => {
        console.log(err)
        this.loading = false
      })
    },

    //详情
    getDetail() {
      let that = this
      API.playback.getTaskRunDetail({ taskRunId: this.replayTaskRunId }).then(res => {
        if (res.code === 'SUCCESS') {
          const { runDesc, runEnv, hosts, subInvocationPlugins, sandboxLogLevel, repeaterLogLevel } = res.data
          that.runDesc = runDesc
          hosts.runEnv = runEnv
          that.formData = hosts
          that.plugins = subInvocationPlugins
          that.sandboxLogLevel = sandboxLogLevel
          that.repeaterLogLevel = repeaterLogLevel
          if (that.queryCondition) {
            that.isAll.value = false
            that.checkedAllThisPage = false
            that.indeterminate = false
            that.checkedNums = 0
            that.checkedList = []
          } else {
            // 如果手动点击了 全选/取消
            if (that.isAll.click) {
              that.tableData.forEach(item => item.checked = that.isAll.value)
              that.checkedNums = that.isAll.value ? that.total : 0
            } else {
              // 接口返回是全选时
              if (res.data.selectType === 0) {
                // 全选按钮处理
                that.checkedNums = that.total - that.cancelCheckList.length
                that.isAll.value = that.cancelCheckList.length ? false : true

                // 多选框处理
                that.tableData.forEach(item1 => {
                  const excit = that.cancelCheckList.includes(item1.traceId)
                  if (!excit) {
                    item1.checked = true
                  }
                })
                let isAllChecked = !that.tableData.some(item => !item.checked)
                this.checkedAllThisPage = isAllChecked
                this.indeterminate = !isAllChecked
              } else {
                // 接口返回非全选时
                Array.isArray(res.data.traceIds) && that.checkedList.push(...res.data.traceIds)
                that.checkedList = Array.from(new Set(that.checkedList))
                that.checkedNums = that.checkedList.length
                that.checkedList.forEach(item => {
                  let index = that.tableData.findIndex(item1 => item1.traceId === item)
                  if (index > -1) {
                    that.tableData[index].checked = true
                  }
                })
                // 表格 有数据
                if (that.tableData.length) {
                  const allchecked = that.tableData.every(item => item.checked)
                  // 所有数据都被勾选了
                  if (allchecked) {
                    this.indeterminate = false
                    this.checkedAllThisPage = true  
                  } else { // 不是 所有数据都被勾选了
                    // 有没有某条被 勾选的
                    const somechecked = that.tableData.some(item => item.checked)
                    // 有勾选的
                    if (somechecked) {
                      this.indeterminate = true
                    } else { // 都没有没勾选
                      this.indeterminate = false
                    }
                    this.checkedAllThisPage = false
                  }
                } else { // 表格 没数据
                  this.indeterminate = false
                  this.checkedAllThisPage = false
                }
              }
            }
          }
        } else {
          that.$message.error('获取详情失败:' + res.msg)
        }
        that.loading = false
      }, err => {
        console.log(err)
        that.loading = false
      })
    },
    checkboxChange (e, traceId) {
      this.isAll.click = false
      // 勾选
      if (e.target.checked) {
        this.checkedNums++
        this.cancelCheckList = this.cancelCheckList.filter(item => item !== traceId)
        this.checkedList.push(traceId)
        // 判断当前页表格中的数据是否存在 checked为false 的情况
        let someFalse = this.tableData.some(item => !item.checked)
        // 如果不存在（所有checked都为true）
        if (!someFalse) {
          this.indeterminate = false
          this.checkedAllThisPage = true
        } else {
          // 否则
          this.checkedAllThisPage = false
          this.indeterminate = true
        }
      } else {
        // 取消
        this.checkedNums--
        this.cancelCheckList.push(traceId)
        this.checkedList = this.checkedList.filter(item => item !== traceId)
        // 判断当前页表格中的数据是否存在 checked为true 的情况
        let someTrue = this.tableData.some(item => item.checked)
        if (!someTrue) {
          this.indeterminate = false
        } else {
          this.indeterminate = true
        }
        this.checkedAllThisPage = false
        // 如果上次操作是全选所有数据的话（this.checkedList=[]），本次取消操作将当前页面中剩余的勾选数据塞入this.checkedList
        if (this.isAll.value) {
          this.tableData.forEach(item => {
            if (item.checked) {
              this.checkedList.push(item.traceId)
            }
          })
        }
      }
    },
    // 当前页 全选框
    checkedAllThisPageChange (e) {
      // this.isAll.value = e
      this.checkedAllThisPage = e
      this.indeterminate = false
      let that = this
      this.tableData.forEach(item1 => {
        // 判断checkedList中是否包含表格中的数据
        let index = that.checkedList.findIndex(item2 => item1.traceId === item2)
        // 勾选时
        if (e) {
          // 如果表格中的数据不在checkedList中，那么就存进去
          if (index === -1) {
            that.checkedList.push(item1.traceId)
          }
          if (!item1.checked) {
            item1.checked = true
            that.checkedNums++
            that.cancelCheckList = that.cancelCheckList.filter(item3 => item3 !== item1.traceId)
          }
        // 取消时
        } else {
          // 如果表格中的数据在checkedList中，那么就删掉它
          if (index !== -1) {
            that.checkedList.splice(index, 1)
          }
          if (item1.checked) {
            item1.checked = false
            that.checkedNums--
            if (!that.cancelCheckList.includes(item1.traceId)) {
              that.cancelCheckList.push(item1.traceId) 
            }
          }
        }
      })
    },
    // 全选 所有
    checkedAll (e) {
      if (e !== 'checkedAll') {
        this.isAll.value = !this.isAll.value
      }
      this.checkedAllThisPage = this.isAll.value
      this.indeterminate = false
      this.isAll.click = true
      this.tableData.forEach(item => item.checked = this.isAll.value)
      this.checkedList = []
      this.checkedNums = this.isAll.value ? this.total : 0
    },
    queryTraceIdDetail({ invokeType, traceId, recordTaskRunId }) {
      this.routerPush({
        name: `${invokeType}Detail`,
        params: { traceId: traceId, taskRunId: recordTaskRunId },
        query: {}
      })
    },
    currentChange (val) {
      this.pageNum = val
      this.queryList()
    },
    sizeChange (val) {
      this.pageSize = val
      this.queryList('click')
    },
    getPlugins() {
      API.record.getPlugins({ taskId: this.taskId }).then((res) => {
        if (res.code === 'SUCCESS') {
          this.pluginsView = res.data
            this.plugins = res.data.map(item => {
              if (item.checked) {
                return item.name
              }
            })
        } else {
          this.$message.error('获取插件失败:' + res.msg)
        }
      })
    },
    execution () {
      if (!this.isAll.value && !this.checkedList.length) return this.$message.warning('请勾选回放数据')
      const envMechineValid = this.$refs.envMechine.envMechineValid()
      if (!envMechineValid) return
      const { runEnv, hostIp, sftpPort, passWord, userName } = this.$refs.envMechine.form
      let params = {
        runDesc: this.runDesc,
        runEnv: runEnv,
        subInvocationPlugins: this.plugins,
        sandboxLogLevel: this.sandboxLogLevel,
        repeaterLogLevel: this.repeaterLogLevel,
        recordTaskRunId: this.recordTaskRunId,
        runHosts: {
          hostIp: hostIp,
          sftpPort: sftpPort,
          passWord: passWord,
          userName: userName
        }
      }

      if (this.isAll.value) {
        params.selectType = 0
      } else {
        params.selectType = 1
        params.traceIds = this.checkedList
      }
      this.loading = true
      API.playback.runPlayback(params).then((res) => {
        this.loading = false
        if (res.code === 'SUCCESS') {
          this.loading = true
          this.$message.success('运行成功')
          this.$store.dispatch('page/close', {
            tagName: this.$route.name,
            notNext: true,
          })

          this.routerPush({
            name: 'playback',
            params: {},
            query: {}
          })
        } else {
          this.$message.error('运行失败:' + res.msg)
        }
      }, err => {
        console.log(err)
        this.loading = false
      })
    },
    resetFormAndClose () {
      this.$store.dispatch('page/close', {
        tagName: this.$route.name,
        notNext: true,
      })
      this.routerPush({ name: 'record', params: {}, query: {} })
    },
    chooseToggle(val) {
      if (val) {
        this.plugins = JSON.parse(JSON.stringify(this.pluginsView))
      } else {
        this.plugins = []
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.addReplay {
  .tableArea {
    position: relative;
    .checkedAllThisPage {
      position: absolute;
      top: 11px;
      left: 19px;
    }
    .el-table .success-row {
      background: #f0f9eb;
    }
  }
  /deep/ .subInvocationPlugins {
    .isGreen{
        color: #67C23A;
      .el-checkbox__inner {
        border-color: #67C23A;
        background: #67C23A;
      }
    }
  }
}
</style>
