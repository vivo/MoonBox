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

import { get } from 'lodash'
import router from '@/router'
let opened = [
  {
    name: 'Dashboard',
    fullPath: '/dashboard',
    meta: {
      title: 'dashboard',
      auth: false
    }
  }
]

// 判定是否需要缓存
const isKeepAlive = data => get(data, 'meta.cache', false)

export default {
  namespaced: true,
  state: {
    // 可以在多页 tab 模式下显示的页面
    pool: [],
    // 当前显示的多页面列表
    opened: opened,
    // 当前页面
    current: '',
    currentNodeName: '',
    parentNodeName: '',
    // 需要缓存的页面 name
    keepAlive: []
  },
  actions: {
    /**
     * @class opened
     * @description 更新页面列表上的某一项
     * @param {Object} state vuex state
     * @param {Object} param { index, params, query, fullPath } 路由信息
     */
    openedUpdate ({ state, commit, dispatch }, { index, params, query, name }) {
      return new Promise(async resolve => {
        // 更新页面列表某一项
        let page = state.opened[index]
        page.params = params || page.params
        page.query = query || page.query
        page.name = name || page.name
        router.push(page)
        state.opened.splice(index, 1, page)
        window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        // end
        commit('currentSet', page.name)
        resolve()
      })
    },
    /**
     * @class opened
     * @description 刷新当前 tag (刷新一个页面)
     * @param {Object} state vuex state
     * @param {Object} param new tag info
     */
    refresh ({ state, commit, dispatch }, { pageSelect } = {}) {
      return new Promise(async resolve => {
        // 找到这个页面在已经打开的数据里是第几个
        const index = state.opened.findIndex(page => page.name === pageSelect)
        const indexOpened = state.opened[index]
        let newPage = state.opened[0]
        const isCurrent = state.current === pageSelect
        // 如果关闭的页面就是当前显示的页面
        if (isCurrent) {
          // 去找一个新的页面
          let len = state.opened.length
          for (let i = 1; i < len; i++) {
            if (state.opened[i].name === pageSelect) {
              if (i < len - 1) {
                newPage = state.opened[i + 1]
              } else {
                newPage = state.opened[i - 1]
              }
              break
            }
          }
        }
        // 找到这个页面在已经打开的数据里是第几个
        if (index >= 0) {
          // 如果这个页面是缓存的页面 将其在缓存设置中删除
          dispatch('localstorage/pageClear', { fullPath: state.opened[index].fullPath }, { root: true })
          commit('keepAliveRemove', state.opened[index].name)
          // 更新数据 删除关闭的页面
          state.opened.splice(index, 1)
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        }
        // 最后需要判断是否需要跳到首页
        if (isCurrent) {
          const { name = '', params = {}, query = {} } = newPage
          let routerObj = {
            name,
            params,
            query
          }
          router.push(routerObj)
        }
        setTimeout(() => {
          state.opened.splice(index, 0, indexOpened)
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
          router.push(indexOpened)
          commit('keepAlivePush', indexOpened.name)
        }, 0)
        // end
        resolve()
      })
    },
    add ({ state, commit, dispatch }, { tag, params, query, name }) {
      return new Promise(async resolve => {
        // 设置新的 tag 在新打开一个以前没打开过的页面时使用
        let newTag = tag
        newTag.params = params || newTag.params
        newTag.query = query || newTag.query
        newTag.name = name || newTag.name
        // 添加进当前显示的页面数组
        state.opened.push(newTag)
        window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        router.push(newTag)
        commit('currentSet', tag.name)
        // 如果这个页面需要缓存 将其添加到缓存设置
        if (isKeepAlive(newTag)) {
          commit('keepAlivePush', tag.name)
        }
        // end
        resolve()
      })
    },
    /**
     * @class current
     * @description 打开一个新的页面
     * @param {Object} state vuex state
     * @param {Object} param 从路由钩子的 to 对象上获取 { name, params, query, fullPath } 路由信息
     */
    open ({ state, commit, dispatch }, { name, params, query, fullPath }) {
      if (JSON.parse(window.sessionStorage.getItem('openedPage'))) {
        state.opened = JSON.parse(window.sessionStorage.getItem('openedPage'))
      }
      return new Promise(async resolve => {
        // 已经打开的页面
        let opened = state.opened || []
        // 判断此页面是否已经打开 并且记录位置
        let pageOpendIndex = 0
        const pageOpend = opened.find((page, index) => {
          let same = decodeURI(page.name) === decodeURI(name)
          pageOpendIndex = same ? index : pageOpendIndex
          return same
        })
        if (pageOpend) {
          // 页面以前打开过
          await dispatch('openedUpdate', {
            index: pageOpendIndex,
            params,
            query,
            name
          })
        } else {
          // 页面以前没有打开过
          let page = state.pool.find(t => t.name === name)
          // 如果这里没有找到 page 代表这个路由虽然在框架内 但是不参与标签页显示
          if (page) {
            await dispatch('add', {
              tag: Object.assign({}, page),
              params,
              query,
              name
            })
          }
        }
        window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        // end
        resolve()
      })
    },
    /**
     * @class opened
     * @description 关闭一个 tag (关闭一个页面)
     * @param {Object} state vuex state
     * @param {Object} param { tagName: 要关闭的标签名字 }
     */
    close ({ state, commit, dispatch }, { tagName,notNext }) {
      return new Promise(async resolve => {
        // 下个新的页面
        let newPage = state.opened[0]
        const isCurrent = state.current === tagName
        // 如果关闭的页面就是当前显示的页面
        if (isCurrent) {
          // 去找一个新的页面
          let len = state.opened.length
          for (let i = 1; i < len; i++) {
            if (state.opened[i].name === tagName) {
              if (i < len - 1) {
                newPage = state.opened[i + 1]
              } else {
                newPage = state.opened[i - 1]
              }
              break
            }
          }
        }
        // 找到这个页面在已经打开的数据里是第几个
        const index = state.opened.findIndex(page => page.name === tagName)
        if (index >= 0) {
          // 如果这个页面是缓存的页面 将其在缓存设置中删除
          dispatch('localstorage/pageClear', { fullPath: state.opened[index].fullPath }, { root: true })
          commit('keepAliveRemove', state.opened[index].name)
          // 更新数据 删除关闭的页面
          state.opened.splice(index, 1)
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        }
        // 最后需要判断是否需要跳到首页
        if (isCurrent && !notNext) {
          const { name = '', params = {}, query = {} } = newPage
          let routerObj = {
            name,
            params,
            query
          }
          router.push(routerObj)
        }
        // end
        resolve()
      })
    },
    /**
     * @class opened
     * @description 关闭当前标签左边的标签
     * @param {Object} state vuex state
     * @param {Object} param { pageSelect: 当前选中的tagName }
     */
    closeLeft ({ state, commit, dispatch }, { pageSelect } = {}) {
      return new Promise(async resolve => {
        const pageAim = pageSelect || state.current
        let currentIndex = 0
        state.opened.forEach((page, index) => {
          if (page.name === pageAim) {
            currentIndex = index
          }
        })
        if (currentIndex > 0) {
          // 删除打开的页面 并在缓存设置中删除
          state.opened.splice(1, currentIndex - 1).forEach(({ name, fullPath }) => {
            dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
            commit('keepAliveRemove', name)
          })
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        }
        state.current = pageAim
        if (router.app.$route.name !== pageAim) {
          router.push(pageAim)
        }
        // end
        resolve()
      })
    },
    /**
     * @class opened
     * @description 关闭当前标签右边的标签
     * @param {Object} state vuex state
     * @param {Object} param { pageSelect: 当前选中的tagName }
     */
    closeRight ({ state, commit, dispatch }, { pageSelect } = {}) {
      return new Promise(async resolve => {
        const pageAim = pageSelect || state.current
        let currentIndex = 0
        state.opened.forEach((page, index) => {
          if (page.name === pageAim) {
            currentIndex = index
          }
        })
        // 删除打开的页面 并在缓存设置中删除
        state.opened.splice(currentIndex + 1).forEach(({ name, fullPath }) => {
          dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
          commit('keepAliveRemove', name)
        })
        window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        // 设置当前的页面
        state.current = pageAim
        if (router.app.$route.name !== pageAim) {
          router.push(pageAim)
        }
        // end
        resolve()
      })
    },
    /**
     * @class opened
     * @description 关闭当前激活之外的 tag
     * @param {Object} state vuex state
     * @param {Object} param { pageSelect: 当前选中的tagName }
     */
    closeOther ({ state, commit, dispatch }, { pageSelect } = {}) {
      return new Promise(async resolve => {
        const pageAim = pageSelect || state.current
        let currentIndex = 0
        state.opened.forEach((page, index) => {
          if (page.name === pageAim) {
            currentIndex = index
          }
        })
        // 删除打开的页面数据 并更新缓存设置
        if (currentIndex === 0) {
          state.opened.splice(1).forEach(({ name, fullPath }) => {
            dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
            commit('keepAliveRemove', name)
          })
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        } else {
          state.opened.splice(currentIndex + 1).forEach(({ name, fullPath }) => {
            dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
            commit('keepAliveRemove', name)
          })
          state.opened.splice(1, currentIndex - 1).forEach(({ name, fullPath }) => {
            dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
            commit('keepAliveRemove', name)
          })
          window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        }
        // 设置新的页面
        state.current = pageAim
        if (router.app.$route.name !== pageAim) {
          router.push(pageAim)
        }
        // end
        resolve()
      })
    },
    /**
     * @class opened
     * @description 关闭所有 tag
     * @param {Object} state vuex state
     */
    closeAll ({ state, commit, dispatch }) {
      return new Promise(async resolve => {
        // 删除打开的页面 并在缓存设置中删除
        state.opened.splice(1).forEach(({ name, fullPath }) => {
          dispatch('localstorage/pageClear', { fullPath: fullPath }, { root: true })
          commit('keepAliveRemove', name)
        })
        window.sessionStorage.setItem('openedPage', JSON.stringify(state.opened))
        // 关闭所有的标签页后需要判断一次现在是不是在首页
        await dispatch('openedUpdate', { index: 0 })
        // if (router.app.$route.name !== 'Dashboard') {
          // router.push({
          //   name: 'Dashboard'
          // })
        // }
        // end
        resolve()
      })
    }
  },
  mutations: {
    /**
     * @class keepAlive
     * @description 从已经打开的页面记录中更新需要缓存的页面记录
     * @param {Object} state vuex state
     */
    keepAliveRefresh (state) {
      state.keepAlive = state.opened.filter(item => isKeepAlive(item)).map(e => e.name)
    },
    /**
     * @description 删除一个页面的缓存设置
     * @param {Object} state vuex state
     * @param {String} name name
     */
    keepAliveRemove (state, name) {
      const list = [...state.keepAlive]
      const index = list.findIndex(item => item === name)
      if (index !== -1) {
        list.splice(index, 1)
        state.keepAlive = list
      }
    },
    /**
     * @description 增加一个页面的缓存设置
     * @param {Object} state vuex state
     * @param {String} name name
     */
    keepAlivePush (state, name) {
      const keep = [...state.keepAlive]
      keep.push(name)
      state.keepAlive = keep
    },
    /**
     * @description 清空页面缓存设置
     * @param {Object} state vuex state
     */
    keepAliveClean (state) {
      state.keepAlive = []
    },
    /**
     * @class current
     * @description 设置当前激活的页面 fullPath
     * @param {Object} state vuex state
     * @param {String} fullPath new fullPath
     */
    currentSet (state, page) {
      state.current = page
      window.sessionStorage.setItem('currentPage', page)
    },
    /**
     * @class pool
     * @description 保存 pool (候选池)
     * @param {Object} state vuex state
     * @param {Array} routes routes
     */
    init (state, routes) {
      const pool = []
      const push = function (routes) {
        routes.forEach(route => {
          if (route.children) {
            push(route.children)
          } else {
            if (!route.hidden) {
              const { meta, name, path } = route
              pool.push({ meta, name, path })
            }
          }
        })
      }
      push(routes)
      state.pool = pool
    }
  }
}
