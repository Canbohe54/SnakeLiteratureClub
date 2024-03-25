<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar/>
      <NavBarOld class="navbar" hidden/><!--导航栏-->
      <Carousel/>
      <!-- <LobbyArticle class="lobby_article"/> -->
      <!-- <ArticleInfo></ArticleInfo> -->
      <ArticleInfoList/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import infroll from '../components/temp_infroll.vue'
import { useStore } from 'vuex'
import Carousel from "../components/Carousel.vue";
import LobbyArticle from "@/components/LobbyArticle.vue";
import NavBar from "@/components/NavBarResponsive.vue";
import ArticleInfoList from "@/components/ArticleInfoList.vue";


@Options({
  components: {
    infroll,
    Carousel,
    LobbyArticle,
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
  margin: 20px 0;
}

</style>
