import fetch from '../utils/fetch'

export default {
  getAgentFiles () {
    return fetch({
      url: '/api/console-agent/fileLists',
      method: 'get'
    })
  }
}
