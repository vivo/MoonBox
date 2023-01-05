<template>
  <el-scrollbar wrap-class="scrollbar-wrapper" id="menu" @dblclick.native="toggleSideBar">
    <el-menu mode="vertical" class="el-menu-vertical" :default-active="$route.path" :collapse="!sidebar.opened">
      <template v-for="(menu, index) in menuInfo.menus">
        <sidebar-item :key="menu.url" :index="index" :menu="menu" :base-path="menu.url"/>
      </template>
    </el-menu>
    <hamburger :toggle-click="toggleSideBar" :is-active="sidebar.opened" />
  </el-scrollbar>
</template>
<script>
import { mapState } from 'vuex'
import SidebarItem from './SidebarItem'
import Hamburger from '@/components/Hamburger'
export default {
  components: {
    SidebarItem,
    Hamburger,
  },
  computed: {
    ...mapState(['menuInfo', 'sidebar']),
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('toggleSideBar')
    }
  }
}
</script>
<style lang="scss" scoped>
.scrollbar-wrapper {
  overflow-x: hidden !important;
  .el-scrollbar__view {
    height: 100%;
    .el-menu {
      height: 100%;
      border: none;
    }
    .el-menu-vertical:not(.el-menu--collapse) {
      width: 180px;
    }
  }
}
</style>
