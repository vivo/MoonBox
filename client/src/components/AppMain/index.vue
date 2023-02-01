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
