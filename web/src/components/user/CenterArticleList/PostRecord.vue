<template>
  <el-text class="list-title">{{( store.getters.getUserInfo.id === route.path.split('/')[2]
    ? '我' : 'Ta' )+ listName }}</el-text>
  <ArticleInfoList option="POST_RECORD"  mode="POST_RECORD" :is_card="true" />

</template>
<script lang="ts" setup>
import {useStore} from 'vuex'
import {useRoute} from 'vue-router'
import { ref } from 'vue'
import ArticleInfoList from "@/components/ArticleInfoList.vue";

const store = useStore()
const route = useRoute()

const listName = ref('需确认收录的文章')

</script>
<style scoped>
.list-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 10px 0;
  display: inline-block;
}
</style>
