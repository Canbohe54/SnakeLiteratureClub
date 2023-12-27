<template>
    <div class="user-bar">
        <el-menu :default-active="activeIndex" class="el-menu-demo user-bar-menu nav-menu" mode="horizontal" :ellipsis="false"
            @select="handleSelect">
            <el-menu-item index="article" class="nav-menu-option">稿件</el-menu-item>
            <el-menu-item index="favor" class="nav-menu-option">收藏</el-menu-item>
            <el-menu-item index="followed" class="nav-menu-option">关注</el-menu-item>
        </el-menu>
        <div class="infos-div">
            <div class="info-div">
                <p class="info-detail"><el-text>作品数</el-text></p>
                <p class="info-detail"><el-text>{{ userDigitInfo.articleNum }}</el-text></p>
            </div>
            <div class="info-div">
                <p class="info-detail"><el-text>关注数</el-text></p>
                <p class="info-detail"><el-text>{{ userDigitInfo.followNum }}</el-text></p>
            </div>
            <div class="info-div">
                <p class="info-detail"><el-text>粉丝数</el-text></p>
                <p class="info-detail"><el-text>{{ userDigitInfo.fansNum }}</el-text></p>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import store from '@/store';
import { onMounted, onUpdated, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
const route = useRoute()
const router = useRouter()
const activeIndex = ref(route.path.split('/')[3])

onUpdated(() => {
    activeIndex.value = route.path.split('/')[3]
})

const userDigitInfo = reactive({
    articleNum: 0,
    followNum: 0,
    fansNum: 0
})

async function getUserDigitInfo() {
    const id = route.params.id
    await SYNC_GET('/usr/getAllArticleNum', {
        user_id: route.params.id
    }, response => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
            userDigitInfo.articleNum = response.data.article_num
        } else {
            console.log(response)
        }
    })
    await SYNC_GET('/usr/getFollowNum', {
        user_id: route.params.id
    }, response => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
            userDigitInfo.followNum = response.data.follow_num
        } else {
            console.log(response)
        }
    })
    await SYNC_GET('/usr/getFansNum', {
        user_id: route.params.id
    }, response => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
            userDigitInfo.fansNum = response.data.fans_num
            console.log(response)
        } else {
            console.log(response)
        }
    })
}

getUserDigitInfo()

const handleSelect = (key: string, keyPath: string[]) => {
    console.log(store.getters.getUserInfo.id)
  console.log('/user/' + route.params.id + '/' + key)
  router.push('/user/' + route.params.id + '/' + key)
  //router.go(0)
}
</script>

<style scoped>
.user-bar {
    display: flex;
    justify-content: space-between;
}
.user-bar-menu {
    margin: 0 20px;
}
.flex-grow {
    flex-grow: 1;
}
.infos-div {
    margin: 0 10px;
    display: flex;
}
.nav-menu { /*取消菜单栏下边框*/
  border-bottom: none !important;
  text-decoration: none;
}
.nav-menu-option:hover {
  color: var(--el-menu-hover-text-color)!important;
  background-color: var(--el-menu-bg-color)!important;
}
.nav-menu-option:focus {
  background-color: var(--el-menu-bg-color)!important;
}
.info-div {
    margin: 0 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.info-detail {
    margin: 0;
}
</style>
