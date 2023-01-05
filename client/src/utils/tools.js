import store from '../store/'

function showLoading () {
  store.commit('updateLoadingStatus', {
    isLoading: true
  })
}

function hiddenLoading () {
  store.commit('updateLoadingStatus', {
    isLoading: false
  })
}

function formatTableTDBlank (data, f, value) {
  let newValue = value
  if (value === '' || typeof value === 'undefined' || value === null) {
    newValue = '--'
  }
  return newValue
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}


export default {
  // 展示loading
  showLoading,
  // 隐藏loading
  hiddenLoading,
  // 表格无数据处理
  formatTableTDBlank,
  // 校验域名
  isExternal,
}
