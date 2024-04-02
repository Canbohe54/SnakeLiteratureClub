<template>
    <div class="user-simp-info" v-if="getToken() != ''">
        <!-- <el-avatar :size="64" id="avatar"><img id="avatar-img"/></el-avatar> -->
        <SnakeAvatar :pictureUrl="userInfo.pictureUrl" :size="64" :kKey="kKey"/>
        <div class="user-simp-info-disp">
            <div class="user-simp-name">{{ userInfo.name }}</div>
            <div><IdentityTag :identity="userInfo.identity"></IdentityTag></div>
            <div class="user-id-disp">用户ID：{{ userInfo.id }}</div>
        </div>
        <div class="user-simp-option">
            <router-link class="user-simp-item" to="/user">个人中心</router-link>
            <router-link class="user-simp-item" to="/account">用户设置</router-link>
        </div>
        <div class="user-simp-option logout-option">
            <a class="user-simp-item" @click="handleLogout">退出登录</a>
        </div>
    </div>
    <div class="user-simp-info" v-else>
        <div class="user-simp-option">
            <router-link class="user-simp-item"  to="/login">点击登录</router-link>
            <router-link class="user-simp-item"  to="/register">点击注册</router-link>
        </div>
    </div>
</template>
<script setup>
import router from '@/router';
import { clearCookie, removeCookie } from '@/scripts/cookie';
import { getToken } from '@/scripts/token';
import { useStore } from 'vuex';
import { onMounted, onUpdated, reactive, ref, toRefs } from 'vue';
import IdentityTag from '@/components/common/IdentityTag.vue';
import SnakeAvatar from '@/components/common/SnakeAvatar.vue';

const props = defineProps({
    kKey: {
        type: String,
        default: '',
    },
})

const { kKey } = toRefs(props)

// console.log(getToken())
const store = useStore()
const userInfo = reactive(store.getters.getUserInfo)

// function init(){
//     if (userInfo.pictureUrl){
//         let avatar = JSON.parse(userInfo.pictureUrl)
//         $("#avatar").css("background-color", `${avatar.color}`)
//         $("#avatar-img").attr("src", 'avatars/'+`${avatar.avatar}`+'.png')
//     }
// }
// init()
// onMounted(() => {
//     init()
// })

function handleLogout(){
    store.commit('clear')
    router.push('/login')
}

</script>
<style scoped>

.user-simp-info {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    margin: 10px;
}

.user-simp-info-disp {
    margin: 10px 0;
    text-align: center;
}

.user-simp-name {
    text-align: center;
    font: bold 18px 'Microsoft YaHei';
    margin-bottom: 8px;
}

.user-id-disp {
    font: 12px 'Microsoft YaHei';
    color: #606266;
    margin-top: 8px;
}

.user-simp-option {
    width: 100%;
}

.user-simp-item {
    display: block;
    width: auto;
    text-decoration: none;
    text-align: center;
    color: #606266;
    padding: 10px 0;
    margin: 5px 0;
    border-radius: 8px;
}

.user-simp-item:hover {
    cursor: pointer;
    background-color: #f5f5f5;
}

.logout-option {
    border-top: 1px solid #f0f0f0;
}

</style>
