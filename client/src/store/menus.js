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

export default {
  state: {
    menus: [
      {
        childNums: 0,
        icon: 'el-icon-video-camera-solid',
        title: '流量录制',
        name: 'record',
        url: '/record/index',
        needAdmin: false
      },
      {
        childNums: 0,
        icon: 'el-icon-s-platform',
        title: '流量回放',
        name: 'playback',
        url: '/playback/index',
        needAdmin: false
      },
      {
        childNums: 0,
        icon: 'el-icon-s-finance',
        title: '回放类&方法Mock',
        name: 'mock',
        url: '/mock/index',
        needAdmin: false
      },
      {
        childNums: 0,
        icon: 'el-icon-s-data',
        title: '回放对比配置',
        name: 'diff',
        url: '/diff/index',
        needAdmin: false
      },
      {
        childNums: 0,
        icon: 'el-icon-s-order',
        title: 'agent文件管理',
        name: 'agentfile',
        url: '/agentfile/index',
        needAdmin: false
      }
    ]
  }
}
