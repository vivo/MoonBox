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
  <div class="navbar">
    <div v-if="sidebar.opened" class="logo big">
      <div>月光宝盒</div>
    </div>
    <div v-else class="logo small">
      <div>月光</div> 
      <div>宝盒</div>
    </div>
    <div class="tabs">
      <multiPageTabs />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Cookies from 'js-cookie'
import multiPageTabs from '../Tabs'
export default {
  name: 'navbar',
  components: {
    multiPageTabs
  },
  computed: {
    ...mapState(['page', 'sidebar'])
  },
  created () {
    this.initEverything()
  },
  methods: {
    initEverything() {
      window.cookies = Cookies
      this.page.opened = JSON.parse(window.sessionStorage.getItem('openedPage'))
        ? JSON.parse(window.sessionStorage.getItem('openedPage'))
        : this.page.opened
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  overflow: hidden;
  height: 50px;
  .logo {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transition: all .5s;
    height: 50px;
    text-align: center;
    color: #4e1a1a;
    font-weight: 700;
    box-shadow: 4px 0px 6px 0px rgba(0, 0, 0, 0.1);
    font-family: cursive;
  }
  .big {
    width: 180px;
    font-size: 22px;
    line-height: 50px;
  }
  .small {
    width: 64px;
    font-size: 16px;
  }
  .tabs {
    flex: 1;
    margin: 0 10px;
    overflow: hidden;
  }
}
</style>

