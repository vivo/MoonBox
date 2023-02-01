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
