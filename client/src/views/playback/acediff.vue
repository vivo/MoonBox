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
    <div class="mask" v-if="isFullScreen"></div>
    <div class="diffToolClass" :class="{ fullScreen: isFullScreen }">
      <div>
        <el-radio-group v-model="scrollSync" size="mini" @change="changeScroll">
          <el-radio-button :label="true">同步滚动</el-radio-button>
          <el-radio-button :label="false">异步滚动</el-radio-button>
        </el-radio-group>
        <el-button type="primary" size="mini" class="fullscreenBtn" @click="toggleShow">
          {{ isFullScreen ? '关闭' : '全屏' }}
        </el-button>
      </div>
      <div :class="elName" :key="elKey"></div>
    </div>
  </div>
</template>

<script>
const ace = require('brace')
ace.Range = ace.acequire('ace/range').Range

import AceDiff from 'ace-diff'
import 'ace-diff/dist/ace-diff.min.css'

export default {
  name: 'v-acediff',
  props: {
    leftContent: {
      type: String,
      default: ''
    },
    rightContent: {
      type: String,
      default: ''
    },
    elName: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      scrollSync: true,
      elKey: '' + Date.now(),
      isFullScreen: false
    }
  },
  watch: {
    leftContent: {
      handler (val) {
        this.init()
      }
    }
  },
  methods: {
    init () {
      // 删除上次渲染的实例，避免重复
      let elName = document.getElementsByClassName(this.elName)[0]
      if (elName) {
        let acediff__wrap = elName.getElementsByClassName('acediff__wrap')[0]
        acediff__wrap && elName.removeChild(acediff__wrap)
      }
      // 为diff插件赋值
      const differ = new AceDiff({
        ace, // You Ace Editor instance
        mode: null,
        theme: null,
        collapse: true,
        element: '.' + this.elName,
        left: {
          content: this.leftContent,
          editable: false,
          copyLinkEnabled: false,
        },
        right: {
          content: this.rightContent,
          editable: false,
          copyLinkEnabled: false,
        },
        classes: {
          diff: 'acediff__diffLine',
          connector: 'acediff__connector',
          newCodeConnectorLinkContent: '&#8594;',
          deletedCodeConnectorLinkContent: '&#8592;',
        }
      })
      if (this.scrollSync) {
        this.scroll(differ)
      }
      return differ
    },
    scroll (differ) {
      let left = differ.editors.left.ace
      let right = differ.editors.right.ace
      document.querySelector('.acediff__left .ace_scrollbar-v').addEventListener('scroll', () => {
        right.session.setScrollTop(left.session.getScrollTop())
      })
      document.querySelector('.acediff__right .ace_scrollbar-v').addEventListener('scroll', () => {
        left.session.setScrollTop(right.session.getScrollTop())
      })
    },
    changeScroll () {
      this.init()
    },
    toggleShow () {
      this.init()
      this.isFullScreen = !this.isFullScreen
    }
  }
}
</script>

<style lang="scss" scoped>
.mask {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: .5;
  background: #000;
  z-index: 1001;
}

.diffToolClass {
  height: 500px;
  position: relative;
  margin-bottom: 30px;
  /deep/ .acediff__wrap {
    top: 28px;
    border: 1px solid #efefef;
  }
  .fullscreenBtn {
    float: right;
  }

  &.fullScreen {
    position: fixed;
    top: 50px;
    bottom: 0;
    left: 0;
    right: 0;
    height: calc(100vh - 80px);
    z-index: 1002;
    background: #fff;
    box-shadow: 0 0 30px rgba(0, 0, 0, .4);
  }
}
</style>
