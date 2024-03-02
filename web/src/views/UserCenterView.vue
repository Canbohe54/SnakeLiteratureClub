<template>
  <el-scrollbar height="100vh">
    <div class="container">
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
  </el-scrollbar>
</template>
<style src="../styles/responsive.scss" lang="scss"></style>
<style scoped>
.user-header {
  --el-header-padding: 0 0;
  --el-header-height: 100%;
  /* border-bottom: 1px solid var(--el-border-color); */
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

@Options({
  components: {
    NavBar,
    InfoDetailDisplay,
    UserCenterBar
  },
  beforeRouteUpdate(to, from, next) {
    if (from.params.id !== to.params.id) {
      location.reload()
    } else {
      next()
    }
  }
})
export default class UserCenterView extends Vue { }
</script>
