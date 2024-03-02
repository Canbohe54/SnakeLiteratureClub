<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar"/><!--导航栏-->
      <Carousel/>
      <LobbyArticle class="lobby_article"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import infroll from '../components/temp_infroll.vue'
import { useStore } from 'vuex'
import Carousel from "../components/Carousel.vue";
import LobbyArticle from "@/components/LobbyArticle.vue";

console.log(useStore())
@Options({
  components: {
    NavBar,
    infroll,
    Carousel,
    LobbyArticle
  }
})
export default class LobbyView extends Vue {}
</script>
<style src="../styles/responsive.scss" lang="scss"></style>
<style scoped>
.navbar {
  margin: 0 0;
}

.lobby_article {
  margin: 20px 0;
}

</style>
