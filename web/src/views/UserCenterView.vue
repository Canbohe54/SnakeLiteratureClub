<template>
    <div class="user-center">
      <NavBar />
      <div class="common-layout user-center-disp">
        <el-container>
          <el-header class="user-header">
            <InfoDetailDisplay />
          </el-header>
          <el-header class="user-center-bar">
            <UserCenterBar />
          </el-header>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </div>
    </div>
</template>
<style scoped>
.user-header {
  --el-header-padding: 0 0;
  --el-header-height: 100%;
  /* border-bottom: 1px solid var(--el-border-color); */
}

.user-center {
  margin: 0 50px;
}
.user-center-bar {
  margin: 0 150px 10px;
}
.user-center-disp {
  margin: 0 100px;
}
.navbar {
  margin: 0 0;
}
</style>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBar.vue'
import InfoDetailDisplay from '@/components/userCenter/InfoDetailDisplay.vue'
import UserCenterBar from '@/components/userCenter/UserCenterBar.vue'
// 页面需要添加监听，若无用户token，则跳转到登录页面
// 旧vue写法： 可惜用不得
// mounted() {
//   if (!localStorage.getItem('token')) {
//     this.$router.push('/login')
//   }
// }
@Options({
  components: {
    NavBar,
    InfoDetailDisplay,
    UserCenterBar
  },
  beforeRouteUpdate(to, from, next) {
    location.reload()
  }
})
export default class UserCenterView extends Vue { }
</script>
