<template>
  <div id="app">
    <Error :error="error"></Error>
    <router-view :error="error" :key="key"/>
  </div>
</template>

<script>
import Error from '@/components/Error'
import { mapState } from 'vuex'
export default {
  components: {
    Error
  },
  computed: {
    ...mapState({
      error: state => state.error
    }),
    key() {
      return this.$route.name
        ? this.$route.name + +new Date()
        : this.$route + +new Date()
    }
  }
}
</script>

<style lang="scss"></style>
