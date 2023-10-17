<template>
  <el-affix>
    <div aria-label="page header" class="header-display">
      <el-page-header title="蛇拾文学社" style="background-color: white;" @back="onback">
        <template #icon>
          <div class="flex items-center">
            <img alt="snakeliteratureclub" src="../assets/logo.png" class="header-logo">
          </div>
        </template>
        <template #content><!--菜单栏-->
          <div>
            <el-menu :default-active=null class="el-menu-demo" mode="horizontal" :ellipsis="false" @select="handleSelect">
              <div class="flex-grow"></div>
              <el-menu-item index="1">Processing Center</el-menu-item>
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
            </el-menu>
          </div>
        </template>

        <template #extra><!--用户信息设置栏-->
          <div class="flex items-center">
            <!-- <el-menu :default-active=null class="el-menu-demo" mode="horizontal" :ellipsis="false" @select="handleSelect">
              <div class="flex-grow"></div>
              <el-menu-item index="2">
                <el-tag>Default</el-tag>
                <el-avatar class="mr-3" :size="32"
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              </el-menu-item>
            </el-menu> -->
            <el-dropdown>
              <span class="el-dropdown-link">
                <div class="user-display">
                  <el-tag class="ml-2 user-display-content" :type="userTagType"><span v-if="userInfo.userName !== '' && userInfo.userName !== undefined">{{ userInfo.userName }} · </span>{{ userInfo.userIdentity }}</el-tag>
                  <el-avatar class="mr-3 user-display-content" :size="40"
                    src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                </div>
                <!-- <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon> -->
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>Action 1</el-dropdown-item>
                  <el-dropdown-item>Action 2</el-dropdown-item>
                  <el-dropdown-item>Action 3</el-dropdown-item>
                  <el-dropdown-item disabled>Action 4</el-dropdown-item>
                  <el-dropdown-item divided>Action 5</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-page-header>
    </div>
  </el-affix>
</template>
<style>
.header-display { /*头部*/
  border-bottom: 1px solid var(--el-border-color);
}

.header-logo { /*logo*/
  width: 32px;
  height: 32px;
  margin: 0 0 0 16px;
}

.flex-grow { /*菜单栏*/
  flex-grow: 1;
}

.user-display { /*用户信息展示栏*/
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 16px 0 0;
}

.user-display-content {   /*用户信息展示栏内容*/
  margin: 0 8px 0 0;
  font-size: 12px;
}

.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}

.el-menu--horizontal.el-menu {
  border-bottom: none !important;
  text-decoration: none;
}

.el-dropdown-link:focus {
  outline: none;
}
</style>
<script lang="ts" setup>
// import { ArrowDown } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
// const activeIndex = ref('2')
import { useRouter } from 'vue-router'

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

const router = useRouter()

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const onback = () => {
  router.push('/')
}

const userInfo = reactive({
  userName: '',
  userIdentity: '未登录'
})
const userTagType = ref(identityTagType(userInfo.userIdentity))
</script>
<script>

</script>
