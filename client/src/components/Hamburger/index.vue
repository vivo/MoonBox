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
  <el-tooltip class="item" effect="dark" content="双击侧边栏可以触发展开、折叠" placement="right">
    <div class="handle" @click="toggleClick">
      <i :class="[isActive ? 'el-icon-arrow-left' : 'el-icon-arrow-right', 'arror']"></i>
    </div>
  </el-tooltip>
</template>

<script>
export default {
  name: 'Hamburger',
  props: {
    isActive: {
      type: Boolean,
      default: true
    },
    toggleClick: {
      type: Function,
      default: null
    }
  }
}
</script>

<style lang="scss" scoped>
.handle {
  position: absolute;
  bottom: 0;
  right: 0;
  z-index: 1001;
  width: 100%;
  height: 50px;
  line-height: 50px;
  text-align: center;
  cursor: pointer;
  &:hover {
    transition: .3s all;
    box-shadow: rgba(33, 35, 38, 0.3) 0px 1px 0px, rgba(33, 35, 38, 0.3) 0px 8px 24px, rgba(17, 17, 26, 0.1) 0px 16px 48px;
  }
  .arror {
    font-size: 18px;
    color: #909399;
    background-color: #FFF;
    cursor: pointer;
    &:hover {
      color: #415FFF;
    }
  }
}
</style>
