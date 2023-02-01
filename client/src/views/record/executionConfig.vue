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
  <div v-loading="loading">
    <el-form ref="formData" :model="formData" :rules="formDataRules" label-position="right" size="mini" label-width="150px">
      <el-card>
        <div slot="header">
          <h4>基本信息</h4>
        </div>
        <el-form-item label="请输入描述：" prop="runDesc">
          <el-input v-model="formData.runDesc" :disabled="disabledFlag" size="small"></el-input>
        </el-form-item>
      </el-card>
      <el-card>
        <div slot="header">
          <h4>执行机器</h4>
        </div>
        <env-mechine ref="envMechine" :formData="formData.form" :detailFlag="disabledFlag"></env-mechine>
      </el-card>
    </el-form>
    <el-card class="add-record-bottom-component">
      <el-button size="small" type="primary" @click="execution">执行</el-button>
      <el-button size="small" @click="resetForm">取消</el-button>
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
      loading: false,
      type: '',
      taskRunId: '',
      disabledFlag: false,
      formData: {
        runDesc: '',
        form: null
      },
      formDataRules: {
        runDesc: [
          { required: true, message: '请输入描述', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    this.taskRunId = this.$route.params.taskRunId
    this.type = this.$route.params.type
    this.templateId = this.$route.params.templateId
    if (this.type == 'detail') {
      this.disabledFlag = true
      this.getDetail()
    }
  },
  methods: {
    getDetail() {
      this.loading = true
      API.record.getTaskRunDetail({ taskRunId: this.taskRunId }).then((res) => {
        if (res.code === 'SUCCESS') {
          res.data.host.runEnv = res.data.runEnv
          this.formData.form = res.data.host
          this.formData.runDesc = res.data.runDesc
          this.loading = false
        } else {
          this.loading = false
          this.$message.error('获取详情失败:' + res.msg)
        }
      }, err => {
        this.loading = false
        this.$message.error('获取详情失败:' + err.msg)
      })
    },
    changeEnv() {
      this.$refs.formData.clearValidate()
    },
    execution() {
      this.$refs.formData.validate(valid => {
        if (valid) {
          const envMechineValid = this.$refs.envMechine.envMechineValid()
          if (!envMechineValid) return
          const { runEnv, hostIp, sftpPort, passWord, userName } = this.$refs.envMechine.form
          let params = {
            templateId: this.templateId,
            runDesc: this.formData.runDesc,
            runEnv: runEnv,
            runHosts: {
              hostIp: hostIp,
              sftpPort: sftpPort,
              passWord: passWord,
              userName: userName
            }
          }
          this.loading = true
          API.record.run(params).then((res) => {
            this.loading = false
            if (res.code === 'SUCCESS') {
              this.resetForm()
            } else {
              this.$message.error('操作失败:' + res.msg)
            }
          }, err => {
            this.loading = false
            this.$message.error('获取详情失败:' + err.msg)
          })
        } else {
          return false
        }
      })
    },
    resetForm() {
      this.$store.dispatch('page/close', {
        tagName: this.$route.name,
        notNext: true,
      })
      this.routerPush({ name: 'record', params: {}, query: {} })
    }
  }
}
</script>
