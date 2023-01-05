<template>
  <div>
    <v-http-flow :traceId="traceId" :taskRunId="taskRunId" :closeType="1" :key="traceId"></v-http-flow>
  </div>
</template>

<script>
import vHttpFlow from '../components/httpFlowDetail'
export default {
  components: {
    vHttpFlow
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