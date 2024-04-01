<template>
  <el-carousel :interval="4000" height="350px" type="card">
    <el-carousel-item v-for="item in imageList" :key="item">
      <img :src="item.url" no-repeat style="object-fit: cover;height:100%; width:100%;" :alt="item.description"/>
    </el-carousel-item>
  </el-carousel>
</template>
<script lang="ts" setup>
// import { getAssetsFile } from "@/scripts/ImageUtil";
import bridge_1 from '@/assets/carousel/bridge_1.png'
import bridge_2 from '@/assets/carousel/bridge_2.png'
import bridge_3 from '@/assets/carousel/bridge_3.png'
const imageList = [
  { url: bridge_1, description: 'The_bridge_of_youth'},
  { url: bridge_2, description: 'The_bridge_of_youth' },
  { url: bridge_3, description: 'The_bridge_of_youth' },
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


