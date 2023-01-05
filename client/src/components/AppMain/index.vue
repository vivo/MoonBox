<template>
  <el-container class="app-main">
    <el-main id="pagecontent">
      <transition name="fade-transform" mode="out-in"> </transition>
      <keep-alive :include="keepAlive">
        <router-view />
      </keep-alive>
    </el-main>
  </el-container>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'AppMain',
  computed: {
    key() {
      return this.$route.fullPath
    },
    ...mapState('page', {
      keepAlive: (state) => state.keepAlive,
    })
  },
}
</script>

<style scoped lang="scss">
.app-main {
  position: relative;
  overflow: hidden;
  width: 100%;
  .el-main {
    position: relative;
    background: #eee;
    padding: 0;
  }
}
</style>
