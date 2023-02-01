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
import Router from 'vue-router'
import routes from './routes'
import store from '../store'
Vue.use(Router)

const router = new Router({
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.length === 0) {
    store.commit('error/setError', {
      show: true,
      msg: '页面飞走了，请检查您访问的页面地址是否正确'
    })
  } else {
    next()
  }
})

export default router
