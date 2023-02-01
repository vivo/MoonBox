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

import Vue from 'vue'
import App from '@/App.vue'
import router from './router'
import store from './store/'
import Element from 'element-ui'
import tableHeaderStyle from './assets/styles/tableHeaderStyle'
import './assets/styles/index.scss'
import ToolTip from './components/ToolTip'
import tools from './utils/tools'

Vue.use(Element)
Vue.use(ToolTip)
Vue.prototype.formatTableTDBlank = tools.formatTableTDBlank
Vue.prototype.tableHeaderStyle = tableHeaderStyle
Vue.prototype.routerPush = function (cb) {
  store.dispatch('page/open', cb)
}

// vue-router升级至3.1.0之后push方法需要callback，否则会报错
const originalPush = router.__proto__.push
router.__proto__.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

new Vue({
  router,
  store,
  render: h => h(App),
  created () {
    this.$store.commit('page/init', router.options.routes)
  }
}).$mount('#app')
