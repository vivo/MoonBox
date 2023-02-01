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

import axios from 'axios'
import Utils from '../utils'
import qs from 'qs'
// 创建axios实例
const service = axios.create({
  baseURL: '',
  timeout: 20000,
  withCredentials: true,
  crossDomain: true,
  validateStatus: function (status) {
    return true
  },
  acceptError: true,
  headers: {
    'X-Requested-With': 'XMLHttpRequest'
  },
})

// request拦截器
service.interceptors.request.use(
  config => {
    config.headers.appId = window.sessionStorage.getItem('currentNodeId')
    config.headers.parentAppId = window.sessionStorage.getItem('parentId')
    if (!config.params) {
      config.params = {}
    }
    // 请求结束后，是否自动关闭loading
    config.autoCloseLoading = !config.params.noLoading

    // 开启
    if (!config.params.noLoading) {
      Utils.tools.showLoading()
    }
    delete config.params.noLoading
    if (config.params.acceptError) {
      config.acceptError = true
      delete config.params.acceptError
    }
    if (config.method === 'post') {
      // post 参数是否需要序列化
      if (config.params.needStringify) {
        delete config.params.needStringify
        config.data = qs.stringify(config.params)
      } else {
        config.data = config.params
      }
      config.params = {}
    }
    // 生产模式
    if (process.env.NODE_ENV === 'production') {
      config.url = config.prodUrl || (config.prodHost || '') + config.url
    }
    return config
  },
  error => {
    console.log(error)
    Promise.reject(error)
  }
)
// respone拦截器
service.interceptors.response.use(
  response => {
    if (response.config.autoCloseLoading) {
      Utils.tools.hiddenLoading()
    }
    // 默认的正常状态返回码
    if (response.status >= 200 && response.status < 300) {
      const getResData = response.config.getResData
      if (!response.data || (response.data && response.data.code !== 200)) {
        if (response.config.acceptError) {
          return returnData(response, getResData)
        }
        throw returnData(response, getResData)
      } else {
        return returnData(response, getResData)
      }
    } else {
      if (response.config.acceptError) {
        return Promise.resolve({
          code: 'ERROR',
          msg: response.statusText
        })
      }
    }
  },
  error => {
    if (!window.navigator.onLine) {
      error.message = '网络异常，请检查网络设置'
    }
    Utils.tools.hiddenLoading()
    return Promise.reject(error)
  }
)

function returnData(response, isResponse) {
  return isResponse ? response : response.data || {}
}

export default service
