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
        <el-form-item label="接口uri">
          <el-input v-model="from.condition" size="small" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button size="small" type="primary" @click="search" icon="el-icon-search">查询</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-button class="fr" icon="el-icon-plus" type="success" size="small" @click="deal">新增</el-button>
      <el-table
        :data="configList"
        :header-cell-style="tableHeaderStyle"
        v-loading="loading"
        size="small"
        border>
        <el-table-column label="应用名称" prop="appName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="忽略接口" prop="diffUri" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="忽略路径" prop="fieldPath" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="作用范围" prop="diffScopeMsg" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="创建人" prop="createUser" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="更新人" prop="updateUser" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="创建时间" prop="createTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="更新时间" prop="updateTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="操作" prop="alertStatus" align="center" width="120px">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="deal(scope.row)">编辑</el-button>
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
      <el-form class="dialogForm" ref="dialogForm" :model="dialogForm" :rules="dialogFormRules" label-width="120px">
        <el-form-item label="应用名称：" prop="appName">
          <el-input v-model="dialogForm.appName" size="small" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="忽略接口：" prop="diffUri">
          <el-input v-model="dialogForm.diffUri" size="small" placeholder="请输入" type="textarea" :rows="5"></el-input>
        </el-form-item>
        <el-form-item label="忽略路径：" prop="fieldPath">
          <el-input v-model="dialogForm.fieldPath" size="small" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="接口类型：" prop="diffScope">
          <el-select v-model="dialogForm.diffScope" size="small" placeholder="请选择">
            <el-option label="入口url" :value="2"></el-option>
            <el-option label="子调用url" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submitForm">保 存</el-button>
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
        appName: '',
        condition: ''
      },
      dialogForm: {
        id: '',
        appName: '',
        diffUri: '',
        fieldPath: '',
        diffScope: 2
      },
      dialogFormRules: {
        appName: [
          { required: true, message: '请输入应用名称', trigger: 'blur' }
        ],
        diffUri: [
          { required: true, message: '请输入忽略接口', trigger: 'blur' }
        ],
        fieldPath: [
          { required: true, message: '请输入忽略路径', trigger: 'blur' }
        ],
        diffScope: [
          { required: true, message: '请选择接口类型', trigger: 'change' }
        ]
      },
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
      API.diff.getList(params).then((res) => {
        this.loading = false
        if (res.code === "SUCCESS") {
          this.configList = res.data.result || []
          this.total = res.data.total || 0
        } else {
          this.$message.error("获取数据失败:" + res.msg)
        }
      })
    },
    deleteConfig (id) {
      this.$confirm("您确定要删除该条数据吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
      .then(() => {
        this.loading = true
        API.diff.delete({ id }).then((res) => {
          this.loading = false;
          if (res.code === "SUCCESS") {
            this.$message.success("删除成功")
            this.search()
          } else {
            this.$message.error("删除失败:" + res.msg)
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
    deal(row) {
      this.dialogVisible = true
      if (row.diffUri) {
        this.dialogTitle = '编辑'
        this.dialogForm = JSON.parse(JSON.stringify(row))
      } else {
        this.dialogTitle = '新增'
        this.dialogForm = {
          id: '',
          appName: '',
          diffUri: '',
          fieldPath: '',
          diffScope: 2
        }
      }
      this.$nextTick(() => {
        this.$refs.dialogForm.clearValidate()
      })
    },
    submitForm() {
      this.$refs.dialogForm.validate(valid => {
        if (valid) {
          let { id, appName, diffUri, fieldPath, diffScope } = this.dialogForm
          let params = { id, appName, diffUri, fieldPath, diffScope }
          let saveApi = API.diff.update
          if (!params.id) {
            saveApi = API.diff.addConfig
            delete params.id
          }
          saveApi(params).then((res) => {
            if (res.code === "SUCCESS") {
              this.$message.success("添加成功")
              this.dialogVisible = false
              this.search()
            } else {
              this.$message.error("添加失败:" + res.msg)
            }
          })
        } else {
          return false
        }
      })
    },
    currentChange(val) {
      this.currentPage = val;
      this.search();
    },
    sizeChange(val) {
      this.pageSize = val;
      this.search();
    }
  }
}
</script>
