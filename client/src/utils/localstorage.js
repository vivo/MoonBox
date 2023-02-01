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
