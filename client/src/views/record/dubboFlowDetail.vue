<template>
  <div>
    <v-dubbo-flow :traceId="traceId" :taskRunId="taskRunId" :closeType="1" :key="traceId"></v-dubbo-flow>
  </div>
</template>

<script>
import vDubboFlow from '../components/dubboFlowDetail'
export default {
  components: {
    vDubboFlow
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
