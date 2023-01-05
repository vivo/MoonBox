import Cookies from 'js-cookie'

export default {
  state: {
    opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true
  },
  mutations: {
    TOGGLE_SIDEBAR: state => {
      state.opened = !state.opened
      if (state.opened) {
        Cookies.set('sidebarStatus', 1)
      } else {
        Cookies.set('sidebarStatus', 0)
      }
    },
    CLOSE_SIDEBAR: (state) => {
      Cookies.set('sidebarStatus', 0)
      state.opened = false
    }
  },
  actions: {
    toggleSideBar ({ commit }) {
      commit('TOGGLE_SIDEBAR')
    },
    closeSideBar ({ commit }) {
      commit('CLOSE_SIDEBAR')
    }
  }
}