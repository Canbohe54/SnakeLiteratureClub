<template>
  <div class="articleDetail">
    <NavBar class="navbar" /><!--导航栏-->
    <ArticleDetail class="artedit"/>

  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import ArticleDetail from "@/components/article/ArticleDetail.vue"
@Options({
  components: {
    NavBar,
    ArticleDetail
  }
})
export default class ArticleDetailView extends Vue { }
</script>
<style>
.artedit {
  margin: 20px 50px;
}

.navbar {
  margin: 0 0;
}
</style>
