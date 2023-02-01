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
      <el-form :inline="true" :model="from" class="demo-ruleForm">
        <el-form-item label="应用名称">
          <el-input v-model="from.appName" size="small" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button size="small" type="primary" @click="search" icon="el-icon-search">查询</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-dropdown class="fr" @command="addField">
        <el-button size="small" type="success" icon="el-icon-arrow-down">新增</el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item :command="1">System.currentTimeMillis()时间回放mock</el-dropdown-item>
          <el-dropdown-item :command="2">java类方法回放Mock</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-table
        :data="configList"
        :header-cell-style="tableHeaderStyle"
        v-loading="loading"
        size="small"
        border>
        <el-table-column label="应用名称" prop="appName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="mock类型" prop="mockTypeMsg" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="300px"/>
        <el-table-column label="创建人" prop="createUser" align="center" :formatter="formatTableTDBlank"/>
        <el-table-column label="更新人" prop="updateUser" align="center" :formatter="formatTableTDBlank"/>
        <el-table-column label="创建时间" prop="createTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="更新时间" prop="updateTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="操作" prop="alertStatus" align="center" width="120px">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="updateConfig(scope.row)">编辑</el-button>
            <el-button type="text" size="small" class="red" @click="deleteConfig(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="block fr">
        <el-pagination
          @current-change="currentChange"
          @size-change="sizeChange"
          :current-page="currentPage"
          :page-sizes="[10,50,100]"
          :page-size="pageSize"
          :total="total"
          layout="total,sizes,prev,pager,next,jumper">
        </el-pagination>
      </div>
    </el-card>
    <el-dialog width="500px" :visible.sync="dialogVisible" :title="dialogTitle">
      <el-form class="dialog-ruleForm" label-width="100px">
        <el-form-item label="应用名称：">
          <el-input v-model="appName" size="small" :disabled="!addFlag" placeholder="请输入"></el-input>
        </el-form-item>
        <div v-for="(item, index) in sysTimeMockClasses" :key="index">
          <el-form-item :label="dialoglabel" label-width="100px">
            <el-input v-model="sysTimeMockClasses[index]" @input="inputChange(index)" size="small" placeholder="请输入"></el-input> &nbsp;
            <i class="delete" @click="deleteIndex(index)"></i>
            <div v-if="mockType==2" class="example">示例：com.jske.Test2#method3</div>
          </el-form-item>
        </div>
        <el-form-item label-width="100px">
          <div class="addBtn" @click="addIndex()">
            <i class="el-icon-plus"></i>添加字段
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submitForm" >保 存</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import API from "../../api"
export default {
  data() {
    return {
      from: {
        appName: ''
      },
      addFlag:false,
      id:'',
      mockType: 1,
      appName:'',
      sysTimeMockClasses:[''],
      contentJson:'',
      safeKey: "",
      desc: "",
      dialogVisible: false,
      loading: false,
      configList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialoglabel: '',
      dialogTitle: ''
    }
  },
  watch: {
    mockType: {
      handler (val) {
        if (val === 1) {
          this.dialoglabel = '类名：'
          this.dialogTitle = 'System.currentTimeMillis()时间回放mock'
        }
        if (val === 2) {
          this.dialoglabel = 'java方法：'
          this.dialogTitle = 'java类方法回放Mock'
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      let params = {
        ...this.from,
        pageNum: this.currentPage,
        pageSize: this.pageSize
      }
      this.loading = true
      API.mock.getList(params).then((res) => {
        this.loading = false
        if (res.code === "SUCCESS") {
          if (res.data.result !== null) {
            this.configList = res.data.result
          } else {
            this.configList = []
          }
          this.total = res.data.total
        } else {
          this.$message.error("获取数据失败:" + res.msg)
        }
      })
    },
    currentChange(val) {
      this.currentPage = val
      this.search()
    },
    sizeChange(val) {
      this.pageSize = val
      this.search()
    },
    deleteConfig (id) {
      this.$confirm("您确定要删除该条数据吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
      .then(() => {
        let params = {};
        this.loading = true;
        params.id = id;
        API.mock.delete(params).then((res) => {
          this.loading = false;
          if (res.code === "SUCCESS") {
            this.$message.success("删除成功");
            this.search();
          } else {
            this.$message.error("删除失败:" + res.msg);
          }
        })
      })
      .catch(() => {
        this.$message({
          type: "info",
          message: "已取消删除"
        })
      })
    },
    addField(val) {
      this.mockType = val
      this.appName = ''
      this.dialogVisible = true
      this.addFlag = true
    },
    inputChange (index) {
      this.sysTimeMockClasses[index] = this.sysTimeMockClasses[index].replace(/\s/g, '')
    },
    updateConfig (row) {
      this.addFlag = false
      this.dialogVisible = true
      this.mockType = row.mockType
      this.appName = row.appName
      this.id = row.id
      const strToObj = JSON.parse(row.contentJson)
      if (row.mockType === 1) {
        this.sysTimeMockClasses = strToObj.sysTimeMockClasses
      }
      if (row.mockType === 2) {
        this.sysTimeMockClasses = []
        strToObj.forEach(item1 => {
          item1.methodList.forEach(item2 => {
            this.sysTimeMockClasses.push(`${item1.className}#${item2}`)
          })
        })
      }
    },
    addIndex() {
      const hasNone = this.sysTimeMockClasses.some(item => !item)
      if (hasNone) {
        this.$message.warning('请填写空白的字段，再添加新的')
      } else {
        this.sysTimeMockClasses.push('')        
      }
    },
    deleteIndex(index) {
      if (this.sysTimeMockClasses.length > 1) {
        this.sysTimeMockClasses.splice(index, 1) 
      } else {
        this.$message.warning('至少配置一条内容!')
      }
    },
    submitForm() {
      if (this.sysTimeMockClasses.length == 0) return this.$message.warning('至少配置一条内容!')
      for (let index = 0; index < this.sysTimeMockClasses.length; index++) {
        if (this.sysTimeMockClasses[index] == '') return this.$message.warning('不能配置空内容！')
        if (this.mockType === 2) {
          let strArr = this.sysTimeMockClasses[index].split('#')
          if (strArr.length !== 2) return this.$message.warning('java方法：有且只有一个#')
          let noValue = strArr.some(item => !item)
          if (noValue) return this.$message.warning('java方法：#两边必须有字符')
        }
      }

      let params = {}
      let obj = {}
      obj.sysTimeMockClasses = JSON.parse(JSON.stringify(this.sysTimeMockClasses))
      if (this.mockType === 1) {
        params.contentJson = JSON.stringify(obj)
      }
      if (this.mockType === 2) {
        params.contentJson = []
        obj.sysTimeMockClasses.forEach(item => {
          const arr = item.split('#')
          const index = params.contentJson.findIndex(item1 => item1.className === arr[0])
          if (index > -1) {
            params.contentJson[index].methodList.push(arr[1])
          } else {
            params.contentJson.push({
              className: arr[0],
              methodList: [arr[1]]
            })
          }
        })
        params.contentJson = JSON.stringify(params.contentJson)
      }
      
      params.appName = this.appName
      params.mockType = this.mockType
      if (this.addFlag) {
        API.mock.addConfig(params).then((res) => {
          if (res.code === 'SUCCESS') {
            this.$message.success('添加成功')
            this.dialogVisible = false
            this.search()
          } else {
            this.$message.error('添加失败:' + res.msg)
          }
        })
      } else {
        params.id = this.id
          API.mock.update(params).then((res) => {
          if (res.code === 'SUCCESS') {
            this.$message.success('修改成功')
            this.dialogVisible = false
            this.search()
          } else {
            this.$message.error('修改失败:' + res.msg)
          }
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dialog-ruleForm {
  /deep/ .el-form-item__content {
    .delete {
      cursor: pointer;
      &::before {
        content: "\e78d";
        font-family: element-icons!important;
        font-style: normal;
        color: #f56c6c;
      }
      &:hover {
        &::before {
          content: "\e79d";
          font-family: element-icons!important;
          font-style: normal;
          color: #f56c6c;
        }
      }
    }
    .el-icon-remove-outline {
      color: #f56c6c;
      &:hover {
        background-color: #f56c6c;
        color: #fff;
      }
    }
    .example {
      font-size: 12px;
      line-height: 14px;
      color: #ccc;
    }
    .addBtn {
      width: 300px;
      height: 35px;
      line-height: 35px;
      border: 1px dashed #415fff;
      color: #415fff;
      border-radius: 6px;
      text-align: center;
      cursor: pointer;
      &:hover {
        border: 1px solid #415fff;
        background-color: #415fff;
        color: #fff;
      }
    }
  }
}
</style>
