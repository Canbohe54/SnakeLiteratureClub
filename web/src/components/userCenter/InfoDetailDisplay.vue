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
          <!-- :class="isUserMyFollowed!=='true'?'followed':''" -->
            <el-button :type="isUserMyFollowed==='true'? 'info': 'primary' " class="followed" plain v-if="route.params.id !== store.getters.getUserInfo.id" :onclick="handleFollowPeople">{{ isUserMyFollowed==='true'?"已关注":"关注" }}</el-button>
        </div>
    </el-row>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { GET, SYNC_GET, SYNC_POST } from '@/scripts/Axios'
import { useRoute } from 'vue-router'
import { AttributeAddableObject } from "@/scripts/ArticleTagFilter";
import { ElMessage } from 'element-plus';

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
  id: '',
  name: '',
  identity: '',
  unit: '',
  introduction: '',
  email: '',
  avatar: ''
})

const isUserMyFollowed = ref('false');

// 若访问地址没有指定id，返回用户个人信息页
(async () => {
  if (route.params.id === '' || route.params.id === undefined) {
    return
  }
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: route.params.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      let detail = response.data['user_info']
      userInfo.value.avatar = detail.pictureUrl
      userInfo.value.id = detail.id
      userInfo.value.email = detail.email
      userInfo.value.name = detail.name
      userInfo.value.unit = detail.organization
      userInfo.value.identity = detail.group
      userInfo.value.introduction = detail.introduction
      userTagType.value = identityTagType(userInfo.value.identity)
    } else {
      console.log('response error')
    }
  })
  await SYNC_GET('/usr/getIsFollowedByUID', {
    token: store.getters.getToken,
    user_id: route.params.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      isUserMyFollowed.value = response.data['followed']
      console.log(isUserMyFollowed.value)
    } else {
      console.log('response error')
    }
  })
})()

async function handleFollowPeople() {
  if (isUserMyFollowed.value === 'true') {
    await SYNC_POST('/usr/unfollowByUID', {
      token: store.getters.getToken,
      user_id: route.params.id
    }, (response) => {
      console.log("unfollow")
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        isUserMyFollowed.value = 'false'
        ElMessage.info('取消关注成功')
        location.reload()
      } else {
        console.log('response error')
      }
    })
  } else {
    await SYNC_POST('/usr/followByUID', {
      token: store.getters.getToken,
      user_id: route.params.id
    }, (response) => {
      console.log("follow")
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        isUserMyFollowed.value = 'true'
        ElMessage.success('关注成功')
        location.reload()
      } else {
        console.log('response error')
      }
    })
  }
}

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

const userTagType = ref()
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
    /* --el-button-hover-text-color: var(--el-color-info);
    --el-button-hover-bg-color: var(--el-color-info-light-9);
    --el-button-hover-border-color: var(--el-color-info-light-5); */
    /*active */
    /* --el-button-active-text-color: var(--el-color-info);
    --el-button-active-bg-color: var(--el-color-info-light-9);
    --el-button-active-border-color: var(--el-color-info-light-5); */
}
</style>
