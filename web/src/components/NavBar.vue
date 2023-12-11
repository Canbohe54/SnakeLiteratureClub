<template>
  <el-affix>
    <div aria-label="page header" class="header-display">
      <el-page-header title="蛇拾文学社" style="background-color: white;" @back="backToLobby">
        <template #icon>
          <div class="flex items-center">
            <img alt="snakeliteratureclub" src="../assets/logo.png" class="header-logo">
          </div>
        </template>
        <template #content><!--菜单栏-->
          <div class="nav-content">
            <el-menu :default-active="(route.path === '/')?'lobby':null" router class="el-menu-demo nav-menu" mode="horizontal" :ellipsis="false" @select="handleSelect">
              <div class="flex-grow"></div>
              <el-menu-item index="lobby" class="nav-menu-option" route="/">文学大厅</el-menu-item>
              <!-- <el-sub-menu index="2">
            <template #title>Workspace</template>
            <el-menu-item index="2-1">item one</el-menu-item>
            <el-menu-item index="2-2">item two</el-menu-item>
            <el-menu-item index="2-3">item three</el-menu-item>
            <el-sub-menu index="2-4">
              <template #title>item four</template>
              <el-menu-item index="2-4-1">item one</el-menu-item>
              <el-menu-item index="2-4-2">item two</el-menu-item>
              <el-menu-item index="2-4-3">item three</el-menu-item>
            </el-sub-menu>
          </el-sub-menu> -->
            </el-menu><!--搜索框-->
            <div class="mt-4 search-input" v-if="route.path!=='/search'">
              <el-input v-model="searchInput" placeholder="搜索" class="input-with-select" @keydown.enter="handleSearch">
                <template #append>
                  <el-button :icon="Search" type="primary" @click="handleSearch"/>
                </template>
              </el-input>
            </div>
          </div>
        </template>

        <template #extra>
          <div class="nav-extra">
            <div v-if="userInfo.identity === '学生' && route.path !== '/articleEditor'" class="nav-contribute">
            <el-button type="primary" class="mr-2" plain :onclick="handleUpload"><el-icon><Upload /></el-icon>&nbsp;投稿文章</el-button>
          </div>
            <div class="flex items-center"><!--用户信息设置栏-->
              <el-dropdown @command="handleDropdownCommand">
                <router-link :to="userInfoRedirect" class="user-info-link">
                  <span class="el-dropdown-link">
                    <div class="user-display"> <!--用户信息-->
                      <el-tag class="ml-2 user-display-content" :type="userTagType" disable-transitions><span
                          v-if="userInfo.name !== '' && userInfo.name !== undefined">{{ userInfo.name }} ·
                        </span>{{ userInfo.identity }}</el-tag>
                      <el-avatar class="mr-3 user-display-content" :size="40"
                        :src="userInfo.avatar" />
                    </div>
                    <!-- <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon> -->
                  </span>
                </router-link>
                <template #dropdown>
                  <el-dropdown-menu v-if="userInfo.identity === '未登录'">
                    <el-dropdown-item command="login">点击登录！</el-dropdown-item>
                  </el-dropdown-menu>
                  <el-dropdown-menu v-else>
                    <el-dropdown-item command="userCenter">个人中心</el-dropdown-item>
                    <el-dropdown-item command="accManage" v-if="!route.path.match('/account')">账号设置</el-dropdown-item>
                    <el-dropdown-item divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </template>
      </el-page-header>
    </div>
  </el-affix>
</template>
<style scoped>
.header-display {
  /*头部*/
  border-bottom: 1px solid var(--el-border-color);
}

.header-logo {
  /*logo*/
  width: 32px;
  height: 32px;
  margin: 0 0 0 16px;
}

.flex-grow {
  /*菜单栏*/
  flex-grow: 1;
}
.nav-content {
  display: flex;
  align-items: center;
}

.search-input {
  margin:0 0 0 200px;
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

.nav-contribute {
  /*投稿按钮*/
  margin: 0 30px 0 0;
  display: flex;
}

.nav-extra {
  display: flex;
  align-items: center;
}
.user-display {
  /*用户信息展示栏*/
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 16px 0 0;
}

.user-info-link {
  /*用户信息展示栏链接*/
  text-decoration: none;
  outline: none;
}

.user-display-content {
  /*用户信息展示栏内容*/
  margin: 0 8px 0 0;
  font-size: 12px;
}

.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
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
.el-dropdown-link:focus {
  outline: none;
}
</style>
<script lang="ts" setup>
// import { ArrowDown } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
// const activeIndex = ref('2')
import { useRoute, useRouter } from 'vue-router'
import { Search, Upload } from '@element-plus/icons-vue'
import { el } from 'element-plus/es/locale'
const router = useRouter()
const route = useRoute()
const store = useStore()
// logo跳转部分
const backToLobby = () => {
  router.push('/')
}

// 导航菜单部分
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

// 搜索框部分
const searchInput = ref('')
const handleSearch = async () => {
  if (searchInput.value !== '' && searchInput.value !== undefined) {
    router.push({ path: '/search', query: { wd: searchInput.value } })
  } else {
    router.push({ path: '/search' })
  }
}

// 投稿文章
const handleUpload = () => {
  router.push('/articleEditor')
}

// 用户信息部分
// const userInfo = reactive({ // 用户信息
//   userName: 'Canbohe54',
//   userIdentity: '专家'
// })
const userInfo = reactive(store.state.userInfo)

const identityTagType = (userIdentity: string) => {
  switch (userIdentity) {
    case '专家':
      return 'warning'
    case '学生':
      return 'success'
    case '管理员':
      return 'warning'
    default:
      return 'info'
  }
}

const userTagType = ref(identityTagType(userInfo.identity))
const userInfoRedirect = ref(userInfo.identity === '未登录' ? '/login' : '/user')
// 未登录则跳转到登录页面，已登录则跳转到个人信息页面 未完成

// 下拉菜单部分
const handleDropdownCommand = (command: string | number | object) => {
  switch (command) {
    case 'login':
      router.push('/login')
      break
    case 'userCenter':
      router.push('/user')
      break
    case 'accManage':
      router.push('/account/info')
      break
    default:
      break
  }
}
</script>
