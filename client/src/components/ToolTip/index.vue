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
  <el-tooltip :placement="placement" :style="iStyle">
    <div slot="content">
      <slot name="inner"/>
    </div>
    <i :class="icon" />
  </el-tooltip>
</template>

<script>
export default {
  props: {
    placement: {
      type: String,
      default: 'top',
      validator: function (val) {
        return [
        'top-start',
        'top',
        'top-end',
        'left-start',
        'left',
        'left-end',
        'right-start',
        'right',
        'right-end',
        'bottom-start',
        'bottom',
        'bottom-end'
        ].some(item => item === val)
      }
    },
    icon: {
      type: String,
      default: 'el-icon-question'
    },
    iStyle: {
      type: String
    }
  }
}
</script>

<style lang="scss" scoped></style>
