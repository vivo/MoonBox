import toolTip from './index.vue'

const ToolTip = {
  install: Vue => {
    Vue.component('tool-tip', toolTip)
  }
}

export default ToolTip