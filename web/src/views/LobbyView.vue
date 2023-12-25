<template>
  <div class="lobby">
    <NavBar class="navbar"/><!--导航栏-->
    <h1>This is Lobby Page</h1>
    <Carousel/>
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import infroll from '../components/temp_infroll.vue'
import { useStore } from 'vuex'
import Carousel from "../components/Carousel.vue";

console.log(useStore())
@Options({
  components: {
    NavBar,
    infroll,
    Carousel
  }
})
export default class LobbyView extends Vue {}
</script>
<style>
.lobby {
  margin: 0 50px;
}
.navbar {
  margin: 0 0;
}
</style>
