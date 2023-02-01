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
  <el-form ref="form" :model="form" :rules="formDataRules" class="mechine" label-width="160px">
    <el-form-item label="请选择执行环境：">
      <el-radio-group v-model="form.runEnv" @change="changeEnv">
        <el-radio v-if="showEnv('local')" label="local">本地环境</el-radio>
        <el-radio v-if="showEnv('dev')" label="dev">开发环境</el-radio>
      </el-radio-group>
      <tool-tip icon="el-icon-question" iStyle="margin-left: 10px;" placement="right">
        <template v-slot:inner>本地环境：月光宝盒在本地启动调试<br/> 开发环境：月光宝盒在开发环境部署</template>
      </tool-tip>
    </el-form-item>
    <el-form-item label="请输入ip：" prop="hostIp">
      <el-input v-model="form.hostIp" :disabled="detailFlag || form.runEnv == 'local'" size="small"></el-input>
      <tool-tip icon="el-icon-question" iStyle="margin-left: 10px;" placement="right">
        <template v-slot:inner>录制机器ip</template>
      </tool-tip>
    </el-form-item>
    <el-form-item label="请输入端口：" prop="sftpPort">
      <el-input v-model="form.sftpPort" :disabled="detailFlag || form.runEnv == 'local'" size="small"></el-input>
      <tool-tip icon="el-icon-question" iStyle="margin-left: 10px;" placement="right">
        <template v-slot:inner>ssh端口默认22</template>
      </tool-tip>
    </el-form-item>
    <el-form-item label="请输入机器用户名：" prop="userName">
      <el-input v-model="form.userName" :disabled="detailFlag || form.runEnv == 'local'" size="small"></el-input>
      <tool-tip icon="el-icon-question" iStyle="margin-left: 10px;" placement="right">
        <template v-slot:inner>注意该用户名必须要是应用启动对应的账号、否则java agent无法挂载</template>
      </tool-tip>
    </el-form-item>
    <el-form-item label="请输入机器密码：" prop="passWord">
      <el-input v-model="form.passWord" :disabled="detailFlag || form.runEnv == 'local'" size="small"></el-input>
      <tool-tip icon="el-icon-question" iStyle="margin-left: 10px;" placement="right">
        <template v-slot:inner>注意该密码是应用启动对应的账号密码，输入错误无法远程ssh执行脚本</template>
      </tool-tip>
    </el-form-item>
  </el-form>
</template>

<script>
import API from '../../api'
export default {
  props: {
    detailFlag: Boolean,
    formData: Object
  },
  data () {
    return {
      form: {
        runEnv: '',
        hostIp: '',
        sftpPort: '22',
        passWord: '',
        userName: ''
      },
      supportEnv: []
    }
  },
  computed: {
    formDataRules () {
      if (this.form.runEnv === 'local') {
        return {}
      } else if (this.form.runEnv === 'dev') {
        return {
          hostIp: [
            { required: true, message: '请输入ip', trigger: 'blur' }
          ],
          sftpPort: [
            { required: true, message: '请输入端口', trigger: 'blur' }
          ],
          passWord: [
            { required: true, message: '请输入机器用户名', trigger: 'blur' }
          ],
          userName: [
            { required: true, message: '请输入机器密码', trigger: 'blur' }
          ]
        }
      } else {
        return {}
      }
    },
    showEnv() {
      return function (env) {
        if (this.detailFlag) {
          return env === this.form.runEnv
        } else {
          return this.supportEnv.includes(env)
        }
      }
    }
  },
  watch: {
    formData () {
      this.form = this.formData
    }
  },
  created () {
    if (!this.detailFlag) {
      API.envMechine.supportEnv().then(res => {
        if (res.code === 'SUCCESS') {
          this.supportEnv = res.data
          if (this.supportEnv.includes('local') ) {
            this.form.runEnv = 'local'
          } else {
            this.form.runEnv = 'dev'
          }
          this.$nextTick(() => {
            this.changeEnv()
          })
        } else {
          this.$message.error('获取支持的环境失败:' + res.msg)
        }
      }, err => {
        this.$message.error('获取支持的环境失败:' + err.msg)
      })
    }
  },
  methods: {
    changeEnv() {
      this.$refs.form.clearValidate()
    },
    envMechineValid() {
      let valids = true
      this.$refs.form.validate(valid => {
        valids = valid
        return valid
      })
      return valids
    }
  }
}
</script>

<style lang="scss" scoped>
.mechine {
  .el-form-item {
    margin-bottom: 10px;
    .el-input {
      height: 32px;
    }
    /deep/.el-form-item__error {
      padding-top: 0;
    }
  }
}
</style>
