<template>
  <el-container>
    <el-header height="50px" id="header">
      <navbar />
    </el-header>
    <el-container style="overflow: hidden">
      <el-aside class="navAnimate">
        <sidebar class="menu sidebar-container"></sidebar>
      </el-aside>
      <el-container>
        <app-main />
      </el-container>
    </el-container>
  </el-container>
</template>
<script>
import Navbar from '@/components/Navbar'
import Sidebar from '@/components/Sidebar'
import AppMain from '@/components/AppMain'
import { mapState } from 'vuex'
export default {
  name: 'home',
  components: {
    Navbar,
    Sidebar,
    AppMain
  },
  computed: {
    ...mapState(['page'])
  },
  created() {
    // 解决通过url直接访问时，无tab的情况
    const excit = this.page.opened.some(item => item.name === this.$route.name)
    !excit && this.routerPush(this.$route)
  }
}
</script>

<style scoped>
#header {
  padding: 0;
  box-shadow: rgba(33, 35, 38, 0.3) 0px 10px 10px -10px;
  z-index: 1001;
}
.sidebar-container {
  height: 100%;
}
.el-container {
  height: 100%;
}
.main-container {
  position: relative;
  min-height: 100%;
  transition: margin-left 0.28s;
}
.navAnimate {
  position: relative;
  width: inherit!important;
  z-index: 1000;
  box-shadow: 4px 0px 6px 0px rgba(0, 0, 0, 0.1);
}
</style>
