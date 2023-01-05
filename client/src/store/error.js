export default {
  namespaced: true,
  state: {
    msg: '',
    show: false
  },
  mutations: {
    setError (state, payload) {
      state.msg = payload.msg
      state.show = payload.show
    }
  }
}
