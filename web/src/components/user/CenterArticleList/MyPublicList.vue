<template>
    <el-text class="list-title">{{ store.getters.getUserInfo.id === route.path.split('/')[2]
     ? '我的' : 'Ta的' }}作品</el-text>
    <ArticleInfoList option="USER_PUBLIC_LIST"  mode="USER_PUBLIC_LIST" :is_card="true" />
</template>

<script setup>
import { useStore } from 'vuex';
import { reactive, toRefs, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import ArticleInfoList from '@/components/ArticleInfoList.vue';

const store = useStore();
const router = useRouter();
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