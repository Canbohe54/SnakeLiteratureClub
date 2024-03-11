<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBarResponsive/>
      <NavBarOld class="navbar" hidden/><!--导航栏-->
      <Carousel/>
      <LobbyArticle class="lobby_article"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBarOld from '../components/NavBarOld.vue'
import infroll from '../components/temp_infroll.vue'
import { useStore } from 'vuex'
import Carousel from "../components/Carousel.vue";
import LobbyArticle from "@/components/LobbyArticle.vue";
import NavBarResponsive from "@/components/NavBarResponsive.vue";

@Options({
  components: {
    NavBarOld,
    infroll,
    Carousel,
    LobbyArticle,
    NavBarResponsive
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
