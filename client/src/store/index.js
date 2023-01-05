import Vue from 'vue'
import Vuex from 'vuex'
import loading from './loading'
import error from './error'
import sidebar from './sidebar'
import page from './page'
import localstorage from './localstorage'
import menuInfo from './menus'

Vue.use(Vuex)
const store = new Vuex.Store({
  modules: {
    page,
    error,
    sidebar,
    loading,
    menuInfo,
    localstorage
  }
})

export default store
