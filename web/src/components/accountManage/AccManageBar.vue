<template>
    <el-col class="acc-manage-bar">
      <div class="acc-bar-empty"></div>
      <el-menu
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        :default-active="menuDefaultAct"
        router
      >
      <el-menu-item index="info" class="acc-bar-menu-item" route="/account/info">
          <el-icon><icon-user /></el-icon>
          <span>用户信息</span>
        </el-menu-item>
        <el-menu-item index="email" class="acc-bar-menu-item" route="/account/email/info">
          <el-icon><icon-email /></el-icon>
          <span>绑定邮箱</span>
        </el-menu-item>
        <el-menu-item index="password" class="acc-bar-menu-item" route="/account/password/verify">
          <el-icon><icon-lock /></el-icon>
          <span>修改密码</span>
        </el-menu-item>
        <el-menu-item index="cancel" class="acc-bar-menu-item" route="/account/cancel/verify">
          <el-icon><icon-circle-close /></el-icon>
          <span>注销账户</span>
        </el-menu-item>
      </el-menu>
    </el-col>
</template>

<style>
.acc-bar-empty {
    height: 30px;
}
.acc-manage-bar {
    min-height: 100vh;
    border-right: 1px solid var(--el-border-color);
}
.acc-bar-menu-item {
    padding: 10px 0 10px 30px!important;
}
.el-menu--vertical.el-menu { /*取消菜单栏右边框*/
  border-right: none !important;
  text-decoration: none;
}

</style>

<script lang="ts" setup>
import {
  User as IconUser,
  Lock as IconLock,
  Message as IconEmail,
  CircleClose as IconCircleClose
} from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const route = useRoute()
const menuDefaultAct = ref(route.path.split('/')[2])
</script>
