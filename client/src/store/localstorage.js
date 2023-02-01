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

import util from '@/utils'
import router from '@/router'
import { cloneDeep } from 'lodash'

/**
 * @description 检查路径是否存在 不存在的话初始化
 * @param {Object} param localstorageName {String} 数据库名称
 * @param {Object} param path {String} 路径
 * @param {Object} param user {Boolean} 区分用户
 * @param {Object} param validator {Function} 数据校验钩子 返回 true 表示验证通过
 * @param {Object} param defaultValue {*} 初始化默认值
 * @returns {String} 可以直接使用的路径
 */
function pathInit ({ localstorageName = 'database', path = '', user = true, validator = () => true, defaultValue = '' }) {
  const uuid = util.cookies.get('uuid') || 'ghost-uuid'
  const currentPath = `${localstorageName}.${user ? `user.${uuid}` : 'public'}${path ? `.${path}` : ''}`
  const value = util.localstorage.get(currentPath).value()
  if (!(value !== undefined && validator(value))) {
    util.localstorage.set(currentPath, defaultValue).write()
  }
  return currentPath
}

export default {
  namespaced: true,
  actions: {
    /**
     * @description 将数据存储到指定位置 | 路径不存在会自动初始化
     * @description 效果类似于取值 localstorageName.path = value
     * @param {Object} param localstorageName {String} 数据库名称
     * @param {Object} param path {String} 存储路径
     * @param {Object} param value {*} 需要存储的值
     * @param {Object} param user {Boolean} 是否区分用户
     */
    set (context, { localstorageName = 'database', path = '', value = '', user = false }) {
      util.localstorage
        .set(
          pathInit({
            localstorageName,
            path,
            user
          }),
          value
        )
        .write()
    },
    /**
     * @description 获取数据
     * @description 效果类似于取值 localstorageName.path || defaultValue
     * @param {Object} param localstorageName {String} 数据库名称
     * @param {Object} param path {String} 存储路径
     * @param {Object} param defaultValue {*} 取值失败的默认值
     * @param {Object} param user {Boolean} 是否区分用户
     */
    get (context, { localstorageName = 'database', path = '', defaultValue = '', user = false }) {
      return new Promise(resolve => {
        resolve(
          cloneDeep(
            util.localstorage
              .get(
                pathInit({
                  localstorageName,
                  path,
                  user,
                  defaultValue
                })
              )
              .value()
          )
        )
      })
    },
    /**
     * @description 获取存储数据库对象
     * @param {Object} context context
     * @param {Object} param user {Boolean} 是否区分用户
     */
    database (context, { user = false } = {}) {
      return new Promise(resolve => {
        resolve(
          util.localstorage.get(
            pathInit({
              localstorageName: 'database',
              path: '',
              user,
              defaultValue: {}
            })
          )
        )
      })
    },
    /**
     * @description 清空存储数据库对象
     * @param {Object} context context
     * @param {Object} param user {Boolean} 是否区分用户
     */
    databaseClear (context, { user = false } = {}) {
      return new Promise(resolve => {
        resolve(
          util.localstorage.get(
            pathInit({
              localstorageName: 'database',
              path: '',
              user,
              validator: () => false,
              defaultValue: {}
            })
          )
        )
      })
    },
    /**
     * @description 获取存储数据库对象 [ 区分页面 ]
     * @param {Object} context context
     * @param {Object} param basis {String} 页面区分依据 [ name | path | fullPath ]
     * @param {Object} param user {Boolean} 是否区分用户
     */
    databasePage (context, { basis = 'fullPath', user = false } = {}) {
      return new Promise(resolve => {
        resolve(
          util.localstorage.get(
            pathInit({
              localstorageName: 'database',
              path: `$page.${router.app.$route[basis]}`,
              user,
              defaultValue: {}
            })
          )
        )
      })
    },
    /**
     * @description 清空存储数据库对象 [ 区分页面 ]
     * @param {Object} context context
     * @param {Object} param basis {String} 页面区分依据 [ name | path | fullPath ]
     * @param {Object} param user {Boolean} 是否区分用户
     */
    databasePageClear (context, { basis = 'fullPath', user = false } = {}) {
      return new Promise(resolve => {
        resolve(
          util.localstorage.get(
            pathInit({
              localstorageName: 'database',
              path: `$page.${router.app.$route[basis]}`,
              user,
              validator: () => false,
              defaultValue: {}
            })
          )
        )
      })
    },
    /**
     * @description 快速将页面当前的数据 ( $data ) 持久化
     * @param {Object} context context
     * @param {Object} param instance {Object} vue 实例
     * @param {Object} param basis {String} 页面区分依据 [ name | path | fullPath ]
     * @param {Object} param user {Boolean} 是否区分用户
     */
    pageSet (context, { instance, basis = 'fullPath', user = false }) {
      return new Promise(resolve => {
        resolve(
          util.localstorage.get(
            pathInit({
              localstorageName: 'database',
              path: `$page.${router.app.$route[basis]}.$data`,
              user,
              validator: () => false,
              defaultValue: cloneDeep(instance.$data)
            })
          )
        )
      })
    },
    /**
     * @description 快速获取页面快速持久化的数据
     * @param {Object} context context
     * @param {Object} param instance {Object} vue 实例
     * @param {Object} param basis {String} 页面区分依据 [ name | path | fullPath ]
     * @param {Object} param user {Boolean} 是否区分用户
     */
    pageGet (context, { instance, basis = 'fullPath', user = false }) {
      return new Promise(resolve => {
        resolve(
          cloneDeep(
            util.localstorage
              .get(
                pathInit({
                  localstorageName: 'database',
                  path: `$page.${router.app.$route[basis]}.$data`,
                  user,
                  defaultValue: {}
                })
              )
              .value()
          )
        )
      })
    },
    /**
     * @description 清空页面快照
     * @param {Object} context context
     * @param {Object} param basis {String} 页面区分依据 [ name | path | fullPath ]
     * @param {Object} param user {Boolean} 是否区分用户
     */
    pageClear (context, { fullPath = '', basis = 'fullPath', user = false }) {
      util.localstorage.get(
        pathInit({
          localstorageName: 'database',
          path: `$page.${fullPath}.$data`,
          user,
          validator: () => false,
          defaultValue: {}
        })
      )
      // return new Promise(resolve => {
      //   resolve(

      //   )
      // })
    }
  }
}
