<template>
  <el-text class="list-title">{{( store.getters.getUserInfo.id === route.path.split('/')[2]
    ? '我' : 'Ta' )+ '的收录' }}</el-text>
  <ArticleInfoList option="RECEIVED" mode="USER_PUBLIC_LIST" :is_card="true" />
</template>

<script setup>
import { useStore } from 'vuex';
import { reactive, toRefs, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import ArticleInfoList from '@/components/ArticleInfoList.vue';
import { SYNC_GET } from '@/scripts/Axios';

const store = useStore();
const route = useRoute();

</script>

<style scoped>
.list-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 10px 0;
  display: inline-block;
}
</style>
