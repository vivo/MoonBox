<template>
  <div class="menu-wrapper" v-if="isShowMenu">
    <el-menu-item :index="resolvePath(menu.url)" @click="pushRouter(menu.url, menu)">
      <i :class="[menu.icon, 'menuIcon']"></i>
      <span class="menuTitle" slot="title">{{menu.title}}</span>
    </el-menu-item>
  </div>
</template>

<script>
import path from 'path'
import { mapState } from 'vuex'
import Utils from 'utils'
export default {
  name: 'SidebarItem',
  props: ['menu', 'index'],
  data () {
    return {
      flag: false,
    }
  },
  computed: {
    ...mapState(['page']),
    isShowMenu () {
      if (this.menu) {
        if (this.menu.isFavorite && !this.menu.childNums) {
          return false
        }
        return true
      }
      return false
    }
  },
  methods: {
    pushRouter (url, menu) {
      this.routerPush({ name: menu.name, params: {}, query: {} })
      Utils.clearLocalStorage(url)
    },
    deleteSameTab (url) {
      // 防止点击相同tab时删除
      let currentRouteName = url.split('/')[1]
      let currentPageName = this.page.current.split('/')[1]
      if (currentPageName === currentRouteName) {
        return
      }
      for (let i = this.page.opened.length-1; i >= 0; i--) {
        if (this.page.opened[i].name === currentRouteName) {
          this.page.opened.splice(i, 1)
        }
      }
    },
    getString (index) {
      return index.toString()
    },
    resolvePath (routePath) {
      if (Utils.tools.isExternal(routePath)) {
        return routePath
      }
      return path.resolve(this.basePath, routePath)
    }
  }
}
</script>

<style lang="scss" scoped>
.menu-wrapper {
  height: 64px;
  cursor: pointer;
  user-select: none;
  &:has(.is-active) {
    border-right: 3px solid #415fff;
  }
  .el-menu-item {
    height: 64px;
    line-height: 64px;
  }
}
</style>
