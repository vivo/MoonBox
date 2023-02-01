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
  // 回放第一层查询
  getExecutionList(params) {
    return fetch({
      url: '/api/replay/runList',
      method: 'get',
      params
    })
  },
  // 回放第二层查询
  getUriReplayListJava(params) {
    return fetch({
      url: '/api/replay/data/uriList',
      method: 'get',
      params
    })
  },
  // 回放第三层查询
  getUriReplayDataList(params) {
    return fetch({
      url: '/api/replay/data/dataList',
      method: 'get',
      params
    })
  },
  // 回放详情
  getTaskRunDetail(params) {
    return fetch({
      url: '/api/replay/detail',
      method: 'get',
      params
    })
  },
  // 第一层停止
  stop(params) {
    return fetch({
      url: '/api/record/stopRun',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 第一层删除
  deleteTask(params) {
    return fetch({
      url: '/api/replay/delete',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 根据taskRunId获取所有traceId
  getTraceIdListAllByTaskRunId(params) {
    return fetch({
      url: '/api/record/data/dataList',
      method: 'get',
      params
    })
  },
  // 运行
  runPlayback(params) {
    return fetch({
      url: '/api/replay/run',
      method: 'post',
      params
    })
  },
  // 失败回放
  failPlayFail(params) {
    return fetch({
      url: '/api/replay/reRunFailedData',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 重新回放
  rePlayback(params) {
    return fetch({
      url: '/api/replay/reRun',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 第三层详情
  getReplayDataDetail(params) {
    return fetch({
      url: '/api/replay/data/dataDetail',
      method: 'get',
      params
    })
  },
  // 流量数据详情-忽略入口流量对比结果
  ignoreReplayConfigPath(params) {
    return fetch({
      url: '/api/replay/diff/create',
      method: 'post',
      params
    })
  },
  // 流量数据详情-生成忽略路径
  guessUserWant(params) {
    return fetch({
      url: '/api/replay/diff/guessUserWant',
      method: 'post',
      params: qs.stringify(params, { arrayFormat: 'indices' })
    })
  },
  // 执行
  execute(params) {
    return fetch({
      url: '/api/taskRun/execute',
      method: 'get',
      params
    })
  },
  // 心跳列表
  getHeartbeatList(params) {
    return fetch({
      url: '/api/console-agent/agentActiveHost',
      method: 'get',
      params
    })
  },
  // 日志
  getLogList(params) {
    return fetch({
      url: '/api/log/list',
      method: 'get',
      params
    })
  }
}
