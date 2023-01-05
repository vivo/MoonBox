import fetch from './fetch'
import tools from './tools'
import storage from './localstorage'
import cookies from './cookies'

const { localstorage, getLocalStorage, clearLocalStorage } = storage

export default {
  fetch,
  tools,
  cookies,
  localstorage,
  getLocalStorage,
  clearLocalStorage
}
