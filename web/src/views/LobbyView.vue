<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar/>
      <NavBarOld class="navbar" hidden/><!--导航栏-->
      <Carousel/>
      <!-- <LobbyArticle class="lobby_article"/> -->
      <!-- <ArticleInfo></ArticleInfo> -->
      <ArticleInfoList mode="STATIC" :is_card="true"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import { useStore } from 'vuex'
import Carousel from "../components/Carousel.vue";
import NavBar from "@/components/NavBarResponsive.vue";
import ArticleInfoList from "@/components/ArticleInfoList.vue";


@Options({
  components: {
    Carousel,
    NavBar,
    ArticleInfoList
  }
})
export default class LobbyView extends Vue {}
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
