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
      <el-table
        :data="agentFiles"
        :header-cell-style="tableHeaderStyle"
        v-loading="loading"
        size="small"
        border>
        <el-table-column label="文件名称" prop="fileName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
        <el-table-column label="文件hash" prop="digestHex" align="center" show-overflow-tooltip :formatter="formatTableTDBlank" width="300px"/>
        <el-table-column label="文件描述" prop="desc" align="center" :formatter="formatTableTDBlank"/>
        <el-table-column label="创建人" prop="createUser" align="center" :formatter="formatTableTDBlank"/>
        <el-table-column label="创建时间" prop="createTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="更新人" prop="updateUser" align="center" :formatter="formatTableTDBlank"/>
        <el-table-column label="更新时间" prop="updateTime" align="center" :formatter="formatTableTDBlank" width="160px"/>
        <el-table-column label="操作" prop="alertStatus" align="center" width="120px">
          <template slot-scope="scope">
            <el-upload
              action="/api/console-agent/fileUpload"
              :data="{ fileName: scope.row.fileName }"
              :show-file-list="false"
              :on-success="success"
              :on-error="error"
              :before-upload="before"
              accept=".tar">
              <el-tooltip effect="dark" content="仅支持上传.tar压缩文件" placement="left">
                <el-button type="text" size="small">文件更新</el-button>
              </el-tooltip>
            </el-upload>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import API from "../../api"
export default {
  data () {
    return {
      loading: false,
      agentFiles: []
    }
  },
  created () {
    this.loading = true
    this.query()
  },
  methods: {
    query() {
      API.agentFile.getAgentFiles().then(res => {
        if (res.code === 'SUCCESS') {
          this.agentFiles = res.data
        } else {
          this.$message.error('获取agent数据失败:' + res.msg)
        }
        this.loading = false
      }, err => {
        this.$message.error('获取agent数据失败:' + err.msg)
        this.loading = false
      })
    },
    before() {
      this.loading = true
    },
    success() {
      this.query()
    },
    error(err) {
      this.$message.error('文件上传失败' + err)
      this.loading = false
    }
  }
}
</script>

<style lang="scss" scoped></style>
