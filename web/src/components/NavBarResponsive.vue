<template>
    <el-row class="resp-nav">
        <el-col :lg="3" :md="4" :sm="2" :xs="2" class="nav-logo-title">
            <div>
                <router-link to="/">
                    <span class="nav-logo"></span>
                    <span class="nav-title-link hidden-sm-and-down">蛇拾文学社</span>
                </router-link>
            </div>
        </el-col>
        <el-col :lg="10" :md="10" class="hidden-sm-and-down"><!--md及以上时的菜单，horizontal，展开的-->
            <el-menu :default-active="activeIndex" class="el-menu-demo nav-menu" mode="horizontal" router :ellipsis-icon="MoreFilled">
                <el-menu-item v-for="item in menu_option" :index="item.index" :route="item.route"
                    class="nav-menu-option">
                    {{ item.title }}
                </el-menu-item>
            </el-menu>
        </el-col>
        <el-col :span="5" class="hidden-sm-and-down lg-search flex-center"
            v-if="route.path !== '/search'"><!--搜索框，仅在大于md上显示-->
            <SearchBar />
        </el-col>
        <el-col :span="4" class="hidden-sm-and-down flex-center"><!--用户信息，仅在大于md上显示-->
            <el-popover placement="bottom" :width="200" trigger="hover" popper-style="border-radius: 8px;">
                <template #reference>
                    <el-avatar class="loginAvatar">
                        <img class="loginAvatar-img" v-if="getToken() != ''" />
                        <span v-else>登录</span>
                    </el-avatar>
                    <!-- @click="router.push('/login') " -->
                </template>
                <UserMenuCard />
            </el-popover>
        </el-col>
        <el-col :span="4" class="hidden-md-and-up flex-center"
            id="sm_down_menu_icon"><!--sm及以下时的菜单，vertical，折叠的，整合了以上三个功能-->
            <div :underline="false" class="flex-center" :onclick="handleUserBar">
                <User class="search-button" />
            </div>
            <div :underline="false" class="flex-center" :onclick="handleSearchRedirect" v-if="route.path !== '/search'">
                <Search class="search-button" />
            </div>
            <div class="menu-toggle-container">
                <div class="menu-toggle" :onclick="handleSmMenu"></div>
            </div>
        </el-col>
    </el-row>
    <el-row class="hidden-md-and-up"><!--sm及以下时的菜单，vertical，折叠的-->
        <el-col :span="24">
            <el-collapse-transition>
                <div v-show="sm_menu_expand">
                    <el-menu :default-active="activeIndex" class="el-menu-demo sm-menu-detail" mode="vertical">
                        <el-menu-item v-for="item in menu_option" :index="item.index" class="sm-menu-items" :route="item.route">
                            {{ item.title }}
                        </el-menu-item>
                    </el-menu>
                </div>
            </el-collapse-transition>
        </el-col>
    </el-row>
    <el-row class="hidden-md-and-up"><!--sm及以下时的用户信息栏，vertical，折叠的-->
        <el-col :span="24">
            <el-collapse-transition>
                <div v-show="user_bar_expand">
                    <UserMenuCard />
                </div>
            </el-collapse-transition>
        </el-col>
    </el-row>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search, Upload, User, MoreFilled } from '@element-plus/icons-vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import SearchBar from './SearchBar.vue';
import UserMenuCard from './user/UserMenuCard.vue';
import { getToken } from '@/scripts/token';

const router = useRouter();
const route = useRoute();
const store = useStore();

const userInfo = reactive(store.getters.getUserInfo)

// 学生：我要投稿，老师：代投文章，志愿者：待审阅稿件，专家：待推荐稿件，猎头：推荐稿件
const identityDepedOption = reactive([
    { // 0
        index: 'articleEditor',
        title: '我要投稿',
        route: '/articleEditor'
    },
    { // 1
        index: 'articleEditor',
        title: '代投文章',
        route: '/articleEditor'
    },
    { // 2
        index: 'articleAuditor',
        title: '待审阅稿件',
        route: '/articleAuditor'
    },
    { // 3
        index: 'receiving',
        title: '待推荐稿件',
        route: '/receiving'
    },
    { // 4
        index: 'receiving',
        title: '收到稿件',
        route: '/receiving'
    }
])

const menu_option = reactive([
    {
        index: 'lobby',
        title: '文学大厅',
        route: '/lobby'
    },
    {
        index: 'public',
        title: '公开作品',
        route: '/public'
    },
    {
        index: 'published',
        title: '优秀作品',
        route: '/published'
    }
])

const activeIndex = ref(router.currentRoute.value.name);

function setIdentityDepedOption(identity: string) {
    switch (identity) {
        case 'CONTRIBUTOR':
            menu_option.push(identityDepedOption[0]);
            break;
        case 'TEACHER':
            menu_option.push(identityDepedOption[1]);
            break;
        case 'VOLUNTEER':
            menu_option.push(identityDepedOption[2]);
            break;
        case 'PROFESSOR':
            menu_option.push(identityDepedOption[3]);
            break;
        case 'HUNTER':
            menu_option.push(identityDepedOption[4]);
            break;
        case 'ADMINISTRATOR':
            menu_option.push(identityDepedOption[1]);
            menu_option.push(identityDepedOption[2]);
            menu_option.push(identityDepedOption[3]);
            menu_option.push(identityDepedOption[4]);
    }
}

onMounted(() => {
    if (userInfo.pictureUrl) {
        let avatar = JSON.parse(userInfo.pictureUrl)
        $(".loginAvatar").css("background-color", `${avatar.color}`)
        $(".loginAvatar-img").attr("src", 'avatars/' + `${avatar.avatar}` + '.png')
    }
    if (store.getters.getUserInfo.identity != '未登录') {
        setIdentityDepedOption(store.getters.getUserInfo.identity)
    }
})

const sm_menu_expand = ref(false);
const user_bar_expand = ref(false);

function handleSmMenu() {
    if (sm_menu_expand.value) {
        sm_menu_expand.value = false;
        user_bar_expand.value = false;
        //改变为非展开状态
        $(".menu-toggle").toggleClass("menu-toggle-active", false);
    } else {
        sm_menu_expand.value = true;
        user_bar_expand.value = false;
        //改变为展开状态
        $(".menu-toggle").toggleClass("menu-toggle-active", true);

    }
}

function handleUserBar() {
    if (user_bar_expand.value) {
        user_bar_expand.value = false;
        sm_menu_expand.value = false;
        //改变为非展开状态
        $(".menu-toggle").toggleClass("menu-toggle-active", false);
    } else {
        user_bar_expand.value = true;
        sm_menu_expand.value = false;
        //改变为非展开状态
        $(".menu-toggle").toggleClass("menu-toggle-active", false);
    }
}

function handleSearchRedirect() {
    router.push('/search');
}

</script>
<style scoped>
.resp-nav {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #ebeef5;
}

.nav-logo-title {
    display: flex;
    align-items: center;
}

.nav-logo {
    display: flex;
    float: left;
    height: 32px;
    width: 32px;
    margin: 0.5px 10px 0 16px;
    background: url("../assets/logo.png") no-repeat center center;
    background-size: contain;
}

.nav-title-link {
    display: flex;
    float: left;
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    margin-top: 10px;
}

.lg-search {
    margin: 0;
    padding: 0;
}

.nav-menu {
    /*取消菜单栏下边框*/
    border-bottom: none !important;
    text-decoration: none;
    width: 500px;
}

.nav-menu-option:hover {
    color: var(--el-menu-hover-text-color) !important;
    background-color: var(--el-menu-bg-color) !important;
}

.nav-menu-option:focus {
    background-color: var(--el-menu-bg-color) !important;
}

.sm-menu-detail {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-color: #fafafa;
    padding: 20px 0;
}

.sm-user-menu-detail {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    padding: 20px 0;
}

.sm-menu-items {
    width: 100%;
    justify-content: center;
    text-align: center;
    padding: 10px 0;
}

.search-button {
    width: 1.5em;
    height: 1.5em;
    margin: 0 10px 0 0;
}

.search-button:hover {
    color: #409eff;
}

.menu-toggle-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 0;
}

@media screen and (max-width: 576px) {
    .menu-toggle-container {
        margin-right: 40px;
    }
}

.menu-toggle {
    position: relative;
    width: 20px;
    height: 40px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
}

.menu-toggle::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 0.12rem;
    background-color: #606266;
    transform: translateY(-7px);
    border-radius: 0.12rem;
    box-shadow: 0 7px #606266;
}

.menu-toggle::after {
    content: '';
    position: absolute;
    width: 100%;
    height: 0.12rem;
    background-color: #606266;
    transform: translateY(7px);
    border-radius: 0.12rem;
    box-shadow: 0 -7px #606266;
}

.menu-toggle:hover::before {
    background-color: #409eff;
    box-shadow: 0 7px #409eff;
}

.menu-toggle:hover::after {
    background-color: #409eff;
    box-shadow: 0 -7px #409eff;
}

.menu-toggle-active::before {
    transform: rotate(45deg) translate(0px, 0px);
    box-shadow: 0 0 #606266;
}

.menu-toggle-active::after {
    transform: rotate(-45deg) translate(0px, 0px);
    box-shadow: 0 0 #606266;
}

.menu-toggle-active:hover::before {
    background-color: #409eff;
    box-shadow: 0 0 #409eff;
}

.menu-toggle-active:hover::after {
    background-color: #409eff;
    box-shadow: 0 0 #409eff;
}
</style>
