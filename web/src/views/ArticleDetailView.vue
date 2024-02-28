<template>
  <div class="articleDetail">
    <NavBar class="navbar" /><!--导航栏-->
    <ArticleDetail class="artdetail"/>
<!--    <GradeDisplay/>-->
<!--    <GradeEditor/>-->
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import ArticleDetail from "@/components/article/ArticleDetail.vue"
import GradeDisplay from "@/components/grade/GradeDisplay.vue";
import GradeEditor from "@/components/grade/GradeEditor.vue";
@Options({
  components: {
    GradeEditor,
    GradeDisplay,
    NavBar,
    ArticleDetail,
  }
})
export default class ArticleDetailView extends Vue { }
</script>
<style>
.articleDetail {
  margin: 0 50px;
}

.navbar {
  margin: 0 0;
}
</style>
