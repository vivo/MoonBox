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
    <v-dubbo-flow :traceId="traceId" :taskRunId="taskRunId" :closeType="1" :key="traceId"></v-dubbo-flow>
  </div>
</template>

<script>
import vDubboFlow from '../components/javaFlowDetail'
export default {
  components: {
    vDubboFlow,
  },
  data() {
    return {
      traceId: this.$route.params.traceId,
      taskRunId: this.$route.params.taskRunId,
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
.el-textarea.is-disabled .el-textarea__inner{
  background: #f2f6fc;
  opacity: 2;
  color: black;
}
</style>
