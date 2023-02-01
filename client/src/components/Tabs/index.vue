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
  <div class="multiple-page-control-group" flex v-if="opened.length">
    <el-tabs
      :value="current"
      :closable="true"
      type="card"
      @tab-click="handleClick"
      @edit="handleTabsEdit">
      <el-tab-pane v-for="page in opened" :key="page.name" :label="getLabel(page)" :name="page.name"></el-tab-pane>
    </el-tabs>
    <div class="multiple-page-control-btn" flex-box="0" v-if="opened.length > 1">
      <el-dropdown placement="bottom-start" size="default" @click="closeAll" @command="(command) => handleControlItemClick(command)">
        <i :class="btnClassName" @mouseenter="() => btnClassName='el-icon-delete-solid'" @mouseleave="() => btnClassName='el-icon-delete'"/>
        <el-dropdown-menu slot="dropdown" class="closeAll">
          <el-dropdown-item command="all">
            <i class="el-icon-error mr-10" />
            全部关闭
          </el-dropdown-item>
          <el-dropdown-item command="left">
            <i class="el-icon-arrow-left mr-10" />
            关闭左侧
          </el-dropdown-item>
          <el-dropdown-item command="right">
            <i class="el-icon-arrow-right mr-10" />
            关闭右侧
          </el-dropdown-item>
          <el-dropdown-item command="other">
            <i class="el-icon-close mr-10" />
            关闭其它
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
export default {
  data() {
    return {
      tagName: '/index',
      btnClassName: 'el-icon-delete'
    }
  },
  computed: mapState('page', {
    opened: 'opened',
    current: state => {
      if (state.current) {
        return state.current
      } else if (window.sessionStorage.getItem('currentPage')) {
        return window.sessionStorage.getItem('currentPage')
      } else {
        return 'Dashboard'
      }
    }
  }),
  methods: {
    getLabel(page) {
      if (page.query) {
        if (page.query.flowName) {
          return page.query.flowName
        }
        if (page.query.titleName) {
          return page.query.titleName
        }
      }
      return (page.meta && page.meta.title) || '未命名'
    },
    ...mapActions('page', [
      'refresh',
      'close',
      'closeLeft',
      'closeRight',
      'closeOther',
      'closeAll',
    ]),
    handleControlItemClick(command, tagName = null) {
      const params = {
        pageSelect: tagName,
      }
      switch (command) {
        case 'refresh':
          this.refresh(params)
          break
        case 'left':
          this.closeLeft(params)
          break
        case 'right':
          this.closeRight(params)
          break
        case 'other':
          this.closeOther(params)
          break
        case 'all':
          this.closeAll()
          break
        default:
          this.$message.error('无效的操作')
          break
      }
    },
    handleClick(tab) {
      let to = this.opened.find(item => item.name === (tab.name || tab))
      this.routerPush(to)
    },
    handleTabsEdit(tagName, action) {
      if (action === 'remove') {
        let index = this.opened.findIndex(item => item.name === tagName)
        let current = ''
        if (index > 1) {
          current = this.opened[index - 1].name
        } else {
          if (this.opened.length > 2) {
            current = this.opened[index + 1].name
          } else {
            current = this.opened[0].name
          }
        }
        this.handleClick(current)
        this.close({
          tagName,
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.multiple-page-control-group {
  display: flex;
  align-items: center;
  box-shadow: 0 4px 2px #f0f0f055;
  /deep/.el-tabs {
    overflow: hidden;
    .el-tabs__header {
      border: none;
      margin: 0;
      .el-tabs__nav {
        border: none;
        .el-tabs__item {
          margin: 0;
          border: none;
          .el-tabs__nav-wrap {
            margin-bottom: 0;
          }
        }
      }
    }
    .el-tabs__nav .el-tabs__item:first-child {
      &:hover {
        padding: 0 20px !important;
      }
      .el-icon-close {
        display: none;
      }
    }

    .el-tabs--card > .el-tabs__header .el-tabs__nav {
      border: none;
      .el-tabs__item {
        border-left: none;
        line-height: 38px;
      }
    }
    .el-tabs--card > .el-tabs__header .el-tabs__item.is-active.is-closable {
      background: #fff;
    }
  }
  .multiple-page-control-btn {
    position: relative;
    cursor: pointer;
    .el-icon-delete,
    .el-icon-delete-solid {
      margin: 3px 0 0 30px;
      &:hover {
        color: #415fff;
      }
    }
  }
}
</style>
