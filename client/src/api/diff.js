import fetch from '../utils/fetch'
import qs from 'qs'

export default {
  getList (params) {
    return fetch({
      url: '/api/replay/diff/list',
      method: 'get',
      params
    })
  },
  addConfig (params) {
    return fetch({
      url: '/api/replay/diff/create',
      method: 'post',
      params
    })
  },
  delete (params) {
    return fetch({
      url: '/api/replay/diff/delete',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  update (params) {
    return fetch({
      url: '/api/replay/diff/update',
      method: 'post',
      params
    })
  }
}
