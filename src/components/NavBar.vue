<template>
  <el-affix>
    <div aria-label="page header">
      <el-page-header title="蛇蛇文学社">
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
            <el-menu :default-active=null class="el-menu-demo" mode="horizontal" :ellipsis="false" @select="handleSelect">
              <div class="flex-grow"></div>
              <el-menu-item index="2">
                <el-tag>Default</el-tag>
                <el-avatar class="mr-3" :size="32"
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              </el-menu-item>
            </el-menu>
          </div>
        </template>
      </el-page-header>
    </div>
  </el-affix>
  <infroll></infroll>
</template>
<style>
.header-logo {
  width: 32px;
  height: 32px;
  margin: 0 0 0 16px;
}

.flex-grow {
  flex-grow: 1;
}
</style>
<script scope setup>
// import { ref } from 'vue'
import infroll from './temp_infroll.vue'
// const activeIndex = ref('2')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath)
}
</script>