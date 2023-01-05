import fetch from '../utils/fetch'
import qs from 'qs'

export default {
  getList (params) {
    return fetch({
      url: '/api/replay/mockClass/list',
      method: 'get',
      params
    })
  },
  addConfig (params) {
    return fetch({
      url: '/api/replay/mockClass/add',
      method: 'post',
      params
    })
  },
  delete (params) {
    return fetch({
      url: '/api/replay/mockClass/delete',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  update (params) {
    return fetch({
      url: '/api/replay/mockClass/edit',
      method: 'post',
      params
    })
  }
}
