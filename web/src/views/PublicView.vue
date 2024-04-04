<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar/>
      <ArticleInfoList option="PUBLIC" mode="PUBLIC" :is_card="true"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from "@/components/NavBarResponsive.vue";
import ArticleInfoList from "@/components/ArticleInfoList.vue";


@Options({
  components: {
    NavBar,
    ArticleInfoList
  }
})
export default class PublicView extends Vue {}
</script>
<style scoped>
.navbar {
  margin: 0 0;
}

.lobby_article {
  margin-bottom: 20px !important;
}

html {
  overflow-x: hidden;
  overflow-y: auto;
}
body {
  width: 100vw;
  overflow: hidden;
  padding-left: calc(100vw - 100%);
}
</style>
