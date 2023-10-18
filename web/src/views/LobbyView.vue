<template>
  <div class="lobby">
    <NavBar class="navbar"/><!--导航栏-->
    <h1>This is Lobby Page</h1>
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import infroll from '../components/temp_infroll.vue'
@Options({
  components: {
    NavBar,
    infroll
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
