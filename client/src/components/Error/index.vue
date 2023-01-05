<template>
  <div class='error-page' v-if="error.show">
    <div>
      <img class="imgBox" src="~@/assets/images/error.png" alt="">
      <div class="text">
        <div>{{ error.msg || '页面飞走了，请检查您访问的页面地址是否正确' }}</div>
      </div>
      <div class="btns">
        <el-button @click="skipIndex">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['error'],
  methods: {
    skipIndex () {
      this.$store.commit('error/setError', false)
      const openedPage = JSON.parse(window.sessionStorage.openedPage)
      const current = openedPage.find(item => item.name === window.sessionStorage.currentPage)
      this.routerPush(current)
    }
  }
}
</script>
<style lang="scss" scoped>
.error-page {
  position: fixed;
  z-index: 2147483647;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  background: #fff;
  .imgBox {
    margin: 0 auto;
    width: 155px;
  }
  .text {
    margin-top: 25px;
    font-size: 18px;
    color: #999999;
  }
  .btns {
    margin-top: 40px;
  }
  .btns .btn {
    display: inline-block;
    margin-bottom: 20px;
    width: 120px;
    height: 48px;
    line-height: 48px;
    color: #fff;
    background: rgb(10, 136, 240);
    border-radius: 3px;
    letter-spacing: 1px;
  }
}
</style>
