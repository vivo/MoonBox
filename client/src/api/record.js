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

import fetch from '../utils/fetch'
import qs from 'qs'

export default {
  // 新增录制模板
  addRecord(params) {
    return fetch({
      url: '/api/record/templateManage/create',
      method: 'post',
      params
    })
  },
  // 第1层 查询
  getRecordList(params) {
    return fetch({
      url: '/api/record/templateManage/list',
      method: 'get',
      params
    })
  },
  // 第1层 删除
  delete(params) {
    return fetch({
      url: '/api/record/templateManage/delete',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 录制任务更新
  update(params) {
    return fetch({
      url: '/api/record/templateManage/update',
      method: 'post',
      params
    })
  },
  // 录制任务编辑、详情回显
  recordDetail(params) {
    return fetch({
      url: '/api/record/templateManage/detail',
      method: 'get',
      params
    })
  },
  // 录制任务新增、编辑、详情：获取插件
  getPlugins() {
    return fetch({
      url: '/api/record/templateManage/supportInvokeTypes',
      method: 'get'
    })
  },
  // 运行
  run(params) {
    return fetch({
      url: '/api/record/run',
      method: 'post',
      params
    })
  },
  // 执行历史
  getExecutionList(params) {
    return fetch({
      url: '/api/record/runList',
      method: 'get',
      params
    })
  },
  // 第二层 详情
  getTaskRunDetail(params) {
    return fetch({
      url: '/api/record/runDetail',
      method: 'get',
      params
    })
  },
  // 第二层删除
  deleteTask(params) {
    return fetch({
      url: '/api/record/deleteRun',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 重新录制
  tryTask(params) {
    return fetch({
      url: '/api/record/reRun',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 第二层 停止
  stop(params) {
    return fetch({
      url: '/api/record/stopRun',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 获取日志列表
  getLogList(params) {
    return fetch({
      url: '/api/log/list',
      method: 'get',
      params
    })
  },
  // 获取接口列表
  getUriDataCountList(params) {
    return fetch({
      url: '/api/record/data/uriList',
      method: 'get',
      params
    })
  },
  // 录制数据列表
  getUriTaskDataList(params) {
    return fetch({
      url: '/api/record/data/dataList',
      method: 'get',
      params
    })
  },
  // 第4层 详情
  flowDetail(params) {
    return fetch({
      url: '/api/record/data/dataDetail',
      method: 'get',
      params
    })
  },
  // 第4层 删除
  deleteFlow(params) {
    return fetch({
      url: '/api/record/data/deleteData',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 获取应用名称
  getAppNameList(params) {
    return fetch({
      url: '/api/app/appNameList',
      method: 'get',
      params
    })
  }
}
