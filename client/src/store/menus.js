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
