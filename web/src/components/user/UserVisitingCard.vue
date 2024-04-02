<template>
    <SnakeAvatar :size="100" :pictureUrl="userInfo.pictureUrl" ref="snakeAvatar" />
    <div>
        <div>
            <div class="user-visiting-name">{{ userInfo.name }}</div>
            <div style="margin:5px 0;"><IdentityTag :identity="userInfo.identity"></IdentityTag></div>
        </div>
    </div>
    <div>
        <span>{{ userInfo.organization }}</span>&nbsp;&nbsp;
        <span>{{ userInfo.attrs }}</span>
    </div>
    <div class="user-introduction">
        {{ userInfo.introduction }}
    </div>
</template>
<script setup>
import { useStore } from 'vuex';
import { reactive,toRefs,ref, onMounted } from 'vue';
import IdentityTag from '@/components/common/IdentityTag.vue';
import SnakeAvatar from '../common/SnakeAvatar.vue';
import { SYNC_GET } from '@/scripts/Axios';
import { getIdentityAttrName } from '@/scripts/common/userIdentityMatch';

const props = defineProps({
    userId: String,
})

const { userId } = toRefs(props);

const snakeAvatar = ref(null);

const userInfo = reactive({
    pictureUrl: '{"avatar":"1","color":"#f56c6c"}',
    name: '田所浩二',
    identity: 'CONTRIBUTOR',
    attrs: '五年级',
    organization: '下北泽中学',
    introduction: '这是一位来自下北泽中学五年级的学生，他的研究方向是沼气动力学。'
})

function getUserInfo(){
    SYNC_GET('/usr/getUserBasicInfo', {
        user_id: userId.value
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userInfo.pictureUrl = response.data.data.user_info.pictureUrl;
            userInfo.name = response.data.data.user_info.name;
            let identity = response.data.data.user_info.identity;
            let attrs = response.data.data.user_info.attrs;
            userInfo.identity = identity;
            userInfo.attrs = attrs[getIdentityAttrName(identity)];
            userInfo.organization = response.data.data.user_info.organization;
            userInfo.introduction = response.data.data.user_info.introduction;
            await snakeAvatar.value.updateAvatar(userInfo.pictureUrl);
        }
    })
}
onMounted(() => {
    getUserInfo();
})
</script>

<style scoped>
.user-visiting-name {
    font: bold 20px/1.5 'Microsoft YaHei';
}
.user-introduction {
    margin-top: 10px;
    color: #606266;
    font-size: 14px;
    line-height: 1.5;
    word-break: break-all;
    white-space: pre-wrap;
}
</style>
