<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <ArticleEditor class="artedit"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBarOld.vue'
import ArticleEditor from '@/components/article/ArticleEditor.vue'
@Options({
  components: {
    NavBar,
    ArticleEditor
  }
})
export default class ArticleEditView extends Vue { }
</script>
<style src="../styles/responsive.scss" lang="scss"></style>
<style>
.artedit {
  margin: 20px 50px;
}

.navbar {
  margin: 0 0;
}
</style>
