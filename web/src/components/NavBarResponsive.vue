<template>
    <el-row class="resp-nav">
        <el-col :lg="3" :md="4" :sm="2" :xs="2">
            <div class="logoTitle">
                <router-link to="/">
                    <span class="logo"></span>
                    <span class="titleLink hidden-sm-and-down">蛇拾文学社</span>
                </router-link>
            </div>
        </el-col>
        <el-col :lg="10" :md="12" class="hidden-sm-and-down"><!--md及以上时的菜单，horizontal，展开的-->
            <el-menu :default-active="menu_option[0].index" class="el-menu-demo" mode="horizontal" router>
                <el-menu-item v-for="item in menu_option" :key="item.index" :index="item.index" :route="item.route">
                    {{ item.title }}
                </el-menu-item>
            </el-menu>
        </el-col>
        <el-col :span="5" class="hidden-sm-and-down"><!--搜索框，仅在大于md上显示-->
            <div class="mt-4 search-input">
                <el-input placeholder="搜索" class="input-with-select">
                    <template #append>
                        <el-button :icon="Search" type="primary" />
                    </template>
                </el-input>
            </div>
        </el-col>
        <el-col class="hidden-sm-and-down"><!--用户信息，仅在大于md上显示-->

        </el-col>
        <el-col :span="2" class="hidden-md-and-up" id="sm_down_menu_icon"><!--sm及以下时的菜单，vertical，折叠的，整合了以上三个功能-->
            <el-link :underline="false" style="display: flex; justify-content: center; align-items: center;">
                <Search style="width: 1.5em; height: 1.5em; margin-right: 10px" />
            </el-link>
            <div style="margin-right:20px; display: flex; justify-content: center; align-items: center;">
                <div class="menu-toggle" :onclick="handleSmMenu"></div>
            </div>
        </el-col>
    </el-row>
    <el-row class="hidden-md-and-up"><!--sm及以下时的菜单，vertical，折叠的-->
        <el-col :span="24">
            <el-collapse-transition>

                <div v-show="sm_menu_expand">
                    <el-menu :default-active="menu_option[0].index" class="el-menu-demo sm-menu-detail" mode="vertical"
                        router>
                        <el-menu-item v-for="item in menu_option" :key="item.index" :index="item.index"
                            :route="item.route">
                            {{ item.title }}
                        </el-menu-item>
                    </el-menu>
                </div>
            </el-collapse-transition>

        </el-col>
    </el-row>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { Search, Upload } from '@element-plus/icons-vue';
const menu_option = reactive([
    {
        index: '1',
        title: '文学大厅',
        route: '/'
    },
    {
        index: '2',
        title: '公开作品',
        route: '/'
    },
    {
        index: '3',
        title: '优秀作品',
        route: '/'
    },
    {
        index: '4',
        title: '待推荐稿件', // 学生：我要投稿，老师：代投文章，志愿者：待审阅稿件，专家：待推荐稿件，猎头：推荐稿件
        route: '/'
    }
])

const sm_menu_expand = ref(false);

function handleSmMenu() {
    if (sm_menu_expand.value) {
        sm_menu_expand.value = false;
    } else {
        sm_menu_expand.value = true;
    }
}

</script>
<style scoped>
.resp-nav {
    display: flex;
    justify-content: space-between;
}

.logoTitle {
    margin: 10px 0 0 0;
}

.logo {
    display: flex;
    float: left;
    height: 32px;
    width: 32px;
    margin: 0.5px 10px 0 16px;
    background: url("../assets/logo.png") no-repeat center center;
    background-size: contain;
}

.titleLink {
    display: flex;
    float: left;
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    margin-top: 10px;
}


.input-with-select .el-input__wrapper {
    border-radius: 8px;
}

.input-with-select .el-input-group__append {
    background-color: var(--el-fill-color-blank);
    border-radius: 8px;
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
}

#sm_down_menu_icon {
    display: flex;
    justify-content: center;
    align-items: center;
}

.sm-menu-detail {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-color: #f5f7fa;
    padding: 20px 0;
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
</style>
