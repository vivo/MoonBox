import low from 'lowdb'
import LocalStorage from 'lowdb/adapters/LocalStorage'

const adapter = new LocalStorage(`moonbox-client`)
const localstorage = low(adapter)

localstorage
  .defaults({
    sys: {},
    database: {}
  })
  .write()

function getLocalStorage (path) {
  const database = JSON.parse(window.localStorage.getItem('moonbox-client'))['database']
  if (!Object.keys(database).length) return null
  if (!database['public']['$page'][path]) return null
  const data = database['public']['$page'][path]['$data']
  if (Object.keys(data).length) {
    return data
  } else {
    return null
  }
}

function clearLocalStorage (path) {
  const data = JSON.parse(window.localStorage.getItem('moonbox-client'))
  if (data.database.public && data.database.public.$page) {
    delete data.database.public.$page[path]
  }
  localStorage.setItem('moonbox-client', JSON.stringify(data))
}

export default {
  localstorage,
  getLocalStorage,
  clearLocalStorage
}
