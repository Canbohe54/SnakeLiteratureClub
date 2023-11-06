<template>
    <div>
        <logo-title />
    </div>
    <div class="forget-passwd">
      <h1>重置密码</h1>
      <router-view />
    </div>
  </template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import LogoTitle from '../components/LogoTitle.vue'
  @Options({
    components: {
      LogoTitle
    }
  })
export default class LoginView extends Vue {}
</script>
<style scoped>
.forget-passwd {
    display: flex;
    flex-direction: column;
    padding: 100px 50px;
}
</style>
