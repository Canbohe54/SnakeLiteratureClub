<template>
    <el-row class="user-info-disp">
        <div class="user-info-table">
            <div class="user-info-main">
                <el-avatar class="mr-3" :size="80" :src="userInfo.avatar"></el-avatar>
                <div class="user-nameid">
                    <el-text class="user-name">{{ userInfo.name }}</el-text>
                    <el-tag :type="userTagType" disable-transitions>{{ userInfo.identity }}</el-tag>
                </div>
            </div>
            <div class="user-info-detail">
                <p class="user-info-detail-p">
                    <el-text>单位：</el-text>
                <el-text>{{ userInfo.unit }}</el-text>
                </p>
                <p class="user-info-detail-p">
                    <el-text>邮箱：</el-text>
                <el-text>{{ userInfo.email }}</el-text>
                </p>
                <p class="user-info-detail-p">
                    <el-text>个人简介：</el-text>
                  <el-text>{{ userInfo.introduction }}</el-text>
                </p>
            </div>
        </div>
        <div>
            <el-button type="info" plain class="followed">已关注</el-button>
        </div>
    </el-row>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { GET } from '@/scripts/Axios'
import { useRoute } from 'vue-router'
import { AttributeAddableObject } from "@/scripts/ArticleTagFilter";

const store = useStore()
const route = useRoute()

interface UserInfo extends AttributeAddableObject{
  id: string,
  name: string,
  identity: string,
  unit: string,
  introduction: string,
  email: string,
  avatar: string
}
const userInfo = ref<UserInfo>({
  id: '123456',
  name: 'Canbohe39',
  identity: '学生',
  unit: 'South China Normal University',
  introduction: '这个人很懒，什么都没留下~',
  email: 'Canbohe39@snake.club',
  avatar: ''
});

// 若访问地址没有指定id，返回用户个人信息页
(() => {
  console.log(`id: ${route.params.id}`)
  if (route.params.id === '' || route.params.id === undefined) {
    return
  }
  GET('/usr/getUserBasicInfo', {
    user_id: route.params.id
  }, (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      let detail = response.data['user_info']
      userInfo.value.avatar = detail.pictureUrl
      userInfo.value.id = detail.id
      userInfo.value.email = detail.email
      userInfo.value.name = detail.name
      userInfo.value.unit = detail.organization
      userInfo.value.identity = detail.group
      userInfo.value.introduction = detail.introduction
    } else {
      console.log('response error')
    }
  })
})()

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

const userTagType = ref(identityTagType(userInfo.value.identity))
</script>
<style scoped>
.user-info-disp {
    margin: 50px 0 0 0;
    display: flex;
    justify-content: space-around;
}

.user-info-detail {
    text-align: start;
    margin: 10px 0 20px 0;
}

.user-info-main {
    display: flex;
}

.user-nameid {
    display: flex;
    align-items: center;
}

.user-name {
    margin: 0 10px 0 20px;
    font: bold 20px "Microsoft-YaHei";
}

.user-info-detail-p {
    margin: 0 0 6px 0;
}

.followed {
    margin-top: 24px;
    /*hover */
    --el-button-hover-text-color: var(--el-color-info);
    --el-button-hover-bg-color: var(--el-color-info-light-9);
    --el-button-hover-border-color: var(--el-color-info-light-5);
    /*active */
    --el-button-active-text-color: var(--el-color-info);
    --el-button-active-bg-color: var(--el-color-info-light-9);
    --el-button-active-border-color: var(--el-color-info-light-5);
}
</style>
