import fetch from '../utils/fetch'

export default {
  supportEnv () {
    return fetch({
      url: '/api/app/supportEnv',
      method: 'get'
    })
  }
}
