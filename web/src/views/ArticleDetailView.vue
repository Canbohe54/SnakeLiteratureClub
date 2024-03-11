<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <ArticleDetail class="artdetail"/>
<!--    <GradeDisplay/>-->
<!--    <GradeEditor/>-->
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBarOld.vue'
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
.navbar {
  margin: 0 0;
}
</style>
