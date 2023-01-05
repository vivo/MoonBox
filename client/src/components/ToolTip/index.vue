<template>
  <el-tooltip :placement="placement" :style="iStyle">
    <div slot="content">
      <slot name="inner"/>
    </div>
    <i :class="icon" />
  </el-tooltip>
</template>

<script>
export default {
  props: {
    placement: {
      type: String,
      default: 'top',
      validator: function (val) {
        return [
        'top-start',
        'top',
        'top-end',
        'left-start',
        'left',
        'left-end',
        'right-start',
        'right',
        'right-end',
        'bottom-start',
        'bottom',
        'bottom-end'
        ].some(item => item === val)
      }
    },
    icon: {
      type: String,
      default: 'el-icon-question'
    },
    iStyle: {
      type: String
    }
  }
}
</script>

<style lang="scss" scoped></style>
