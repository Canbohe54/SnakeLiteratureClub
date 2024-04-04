<template>
    <el-text class="list-title">{{( store.getters.getUserInfo.id === route.path.split('/')[2]
     ? '我' : 'Ta' )+ listName }}</el-text>
    <ArticleInfoList option="USER_PUBLIC_LIST" mode="USER_PUBLIC_LIST" :is_card="true" />
</template>

<script setup>
import { useStore } from 'vuex';
import { reactive, toRefs, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import ArticleInfoList from '@/components/ArticleInfoList.vue';
import { SYNC_GET } from '@/scripts/Axios';

const store = useStore();
const router = useRouter();
const route = useRoute();

const userId = route.params.id ? route.params.id : store.getters.getUserInfo.id
const userIdentity = ref('')
const listName = ref('作品')

async function getUserIdentity() {
    await SYNC_GET('/usr/getUserIdentity', {
        user_id: userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userIdentity.value = response.data.data
            setListName()
        }
        else {
            errorCallback(response)
        }
    })
}

function setListName () {
    if (userIdentity.value === 'CONTRIBUTOR') {
        listName.value = '发布的作品'
    }
    else if (userIdentity.value === 'TEACHER') {
        listName.value = '指导的作品'
    } else {
        listName.value = '的作品'
    }
}

onMounted(() => {
    getUserIdentity()
    setListName()
})

</script>

<style scoped>
.list-title {
    font-size: 20px;
    font-weight: bold;
    margin: 0 0 10px 0;
    display: inline-block;
}
</style>
