<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <Receiving class="receiving"/>

    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBarResponsive.vue'
import Receiving from '@/components/magazinePublisher/Receiving.vue'

@Options({
  components: {
    Receiving,
    NavBar,
  }
})
export default class ReceivingView extends Vue { }
</script>
<style>
.navbar {
  margin: 0 0;
}
</style>
