import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

let num = 0
const loading = {
  state: {
    isLoading: true
  },
  actions: {},
  mutations: {
    updateLoadingStatus (state, payload) {
      if (payload.isLoading) {
        num++
        setTimeout(() => {
          if (num > 0) {
            NProgress.start()
          }
        }, 300)
      } else {
        num--
        if (num == 0) {
          NProgress.done()
        }
      }
    }
  }
}

export default loading
