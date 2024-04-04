<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <div style="margin-top: 20px;">
        <ArticleInfoList mode="RECEIVING" option="RECEIVING" is_card="false" />
      </div>
    </div>
  </el-scrollbar>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBarResponsive.vue'
import ArticleInfoList from "@/components/ArticleInfoList.vue";

@Options({
  components: {
    ArticleInfoList,
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
