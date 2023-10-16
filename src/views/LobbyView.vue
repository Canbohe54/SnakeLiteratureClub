<template>
  <div class="lobby">
    <NavBar/><!--导航栏-->
    <h1>This is Lobby Page</h1>
  </div>
</template>
<script>
import NavBar from '../components/NavBar.vue'
export default {
  name:'LobbyView',
  components:{
    NavBar
  }
}
</script>