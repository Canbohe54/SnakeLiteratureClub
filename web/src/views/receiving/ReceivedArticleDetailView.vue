<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <ReceivedArticleDetail class="artdetail"/>

    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBarResponsive.vue'
import ReceivedArticleDetail from '@/components/receiving/ReceivedArticleDetail.vue'

@Options({
  components: {
    ReceivedArticleDetail,
    NavBar,
  }
})
export default class ReceivedArticleDetailView extends Vue { }
</script>
<style>
.navbar {
  margin: 0 0;
}
</style>
