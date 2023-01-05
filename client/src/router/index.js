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
