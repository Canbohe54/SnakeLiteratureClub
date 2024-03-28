<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar />
      <NewUserCenter />
    </div>
  </el-scrollbar>
</template>
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
import NavBar from '@/components/NavBarResponsive.vue'
import NewUserCenter from '@/components/user/NewUserCenter.vue'

@Options({
  components: {
    NavBar,
    NewUserCenter
  },
  beforeRouteUpdate(to, from, next) {
    if (from.params.id !== to.params.id) {
      location.reload()
    } else {
      next()
    }
  }
})
export default class UserCenterView extends Vue {}
</script>
