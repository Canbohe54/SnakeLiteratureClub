<template>
  <div class="user-center">
    <NavBar />
  </div>
</template>
<style>
.user-center {
  margin: 0 50px;
}
.navbar {
  margin: 0 0;
}
</style>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBar.vue'
// 页面需要添加监听，若无用户token，则跳转到登录页面
// 旧vue写法： 可惜用不得
// mounted() {
//   if (!localStorage.getItem('token')) {
//     this.$router.push('/login')
//   }
// }
@Options({
  components: {
    NavBar
  }
})
export default class UserCenterView extends Vue {}
</script>
