/*
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
 */

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