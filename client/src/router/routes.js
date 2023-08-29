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

export default [
  {
    path: '/',
    redirect: 'dashboard',
    component: () => import(/* webpackChunkName: 'dashboard' */ 'views/Home'),
    name: 'home',
    meta: {
      title: 'dashboard'
    },
    children: [
      {
        path: 'dashboard',
        component: () => import(/* webpackChunkName: 'dashboard' */ 'views/dashboard'),
        name: 'Dashboard',
        meta: {
          title: 'dashboard'
        }
      },
      {
        path: '/record/index',
        component: () => import(/* webpackChunkName: 'record' */ 'views/record/index'),
        name: 'record',
        meta: {
          title: '流量录制'
        }
      },
      {
        path: '/record/addRecord/:type/:id/:templateId',
        component: () => import(/* webpackChunkName: 'addRecord' */ 'views/record/addRecord'),
        name: 'addRecord',
        meta: {
          title: '新增录制任务'
        }
      },
      {
        path: '/record/executionConfig/:type/:templateId/:taskRunId?',
        component: () => import(/* webpackChunkName: 'executionConfig' */ 'views/record/executionConfig'),
        name: 'executionConfig',
        meta: {
          title: '执行配置'
        }
      },
      {
        path: '/flow/httpDetail/:traceId/:taskRunId',
        component: () => import(/* webpackChunkName: 'httpDetail' */ 'views/record/httpFlowDetail'),
        name: 'httpDetail',
        meta: {
          title: 'http流量数据详情'
        }
      },
      {
        path: '/flow/dubboDetail/:traceId/:taskRunId',
        component: () => import(/* webpackChunkName: 'dubboDetail' */ 'views/record/dubboFlowDetail'),
        name: 'dubboDetail',
        meta: {
          title: 'dubbo流量数据详情'
        }
      },
      {
        path: '/flow/motanDetail/:traceId/:taskRunId',
        component: () => import(/* webpackChunkName: 'motanDetail' */ 'views/record/motanFlowDetail'),
        name: 'motanDetail',
        meta: {
          title: 'motan流量数据详情'
        }
      },
      {
        path: '/flow/javaDetail/:traceId/:taskRunId',
        component: () => import(/* webpackChunkName: 'javaDetail' */ 'views/record/javaFlowDetail'),
        name: 'javaDetail',
        meta: {
          title: 'Java流量数据详情'
        }
      },
      {
        path: '/playback/index',
        component: () => import(/* webpackChunkName: 'playback' */ 'views/playback/index'),
        name: 'playback',
        meta: {
          title: '流量回放'
        }
      },
      {
        path: '/playback/addReplay/:recordTaskRunId/:replayTaskRunId?/:type',
        component: () => import(/* webpackChunkName: 'addReplay' */ 'views/playback/addReplay'),
        name: 'addReplay',
        meta: {
          title: '新增回放'
        }
      },
      {
        path: '/playback/httpDetail/:replayTaskRunId/:replayTraceId',
        component: () => import(/* webpackChunkName: 'playbackhttpDetail' */ 'views/playback/httpDetail'),
        name: 'playbackhttpDetail',
        meta: {
          title: 'http流量回放数据详情'
        }
      },
      {
        path: '/playback/dubboDetail/:replayTaskRunId/:replayTraceId',
        component: () => import(/* webpackChunkName: 'playbackdubboDetail' */ 'views/playback/dubboDetail'),
        name: 'playbackdubboDetail',
        meta: {
          title: 'dubbo流量回放数据详情'
        }
      },
      {
        path: '/playback/motanDetail/:replayTaskRunId/:replayTraceId',
        component: () => import(/* webpackChunkName: 'playbackmotanDetail' */ 'views/playback/motanDetail'),
        name: 'playbackmotanDetail',
        meta: {
          title: 'motan流量回放数据详情'
        }
      },
      {
        path: '/playback/javaDetail/:replayTaskRunId/:replayTraceId',
        component: () => import(/* webpackChunkName: 'playbackJavaDetail' */ 'views/playback/javaDetail'),
        name: 'playbackjavaDetail',
        meta: {
          title: 'Java流量回放数据详情'
        }
      },
      {
        path: '/mock/index',
        component: () => import(/* webpackChunkName: 'mock' */ 'views/mock/index'),
        name: 'mock',
        meta: {
          title: '回放类&方法Mock'
        }
      },
      {
        path: '/diff/index',
        component: () => import(/* webpackChunkName: 'diff' */ 'views/diff/index'),
        name: 'diff',
        meta: {
          title: '回放对比配置'
        }
      },
      {
        path: '/agentfile/index',
        component: () => import(/* webpackChunkName: 'agentfile' */ 'views/agentfile/index'),
        name: 'agentfile',
        meta: {
          title: 'agent文件管理'
        }
      }
    ]
  }
]
