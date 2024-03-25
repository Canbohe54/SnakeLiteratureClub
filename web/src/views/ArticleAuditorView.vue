<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <ArticleAuditor class="artAuditor"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBarResponsive.vue'
import ArticleAuditor from '@/components/auditor/ArticleAuditor.vue'
@Options({
  components: {
    NavBar,
    ArticleAuditor,
  }
})
export default class ArticleAuditorView extends Vue { }
</script>
<style>
.navbar {
  margin: 0 0;
}
</style>
