<template>
  <div class="articleEdit">
    <NavBar class="navbar" /><!--导航栏-->
    <ArticleEditor class="artedit"/>

  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import ArticleEditor from "@/components/article/ArticleEditor.vue";
@Options({
  components: {
    NavBar,
    ArticleEditor
  }
})
export default class ArticleEditView extends Vue { }
</script>
<style>
.artedit {
  margin: 20px 50px;
}

.navbar {
  margin: 0 0;
}
</style>
