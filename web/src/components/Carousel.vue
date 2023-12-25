<template>
  <el-carousel :interval="4000" type="card">
    <el-carousel-item v-for="item in imageList" :key="item">
      <img :src="item.url" no-repeat style="object-fit: fill;height:100%; width:100%;" :alt="item.description"/>
    </el-carousel-item>
  </el-carousel>
</template>
<script lang="ts" setup>
// import { getAssetsFile } from "@/scripts/ImageUtil";
import snake from '@/assets/snake.png'
const imageList = [
  { url: snake , description: 'snakeliteratureclub'},
  { url: snake, description: 'snake'},
  { url: snake, description: 'snake' },
];

</script>
<style scoped>
.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>


