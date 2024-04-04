<template>
  <ul class="MessageList" v-if="messageList.length > 0">
    <li v-for="(msg, index) in messageList">
      <el-row class="MessageDisplay">
        <SnakeAvatar :picture-url="msg.from.avatar" @click="toUserPage(msg.from.id)" :kKey="index" class="SenderAvatar"/>
        <div class="blank"></div>
        <el-text class="SenderName" @click="toUserPage(msg.from.id)">{{ msg.from.name }}</el-text>
        <div class="e"></div>
        <el-text>{{ msg.message }}</el-text>
      </el-row>
      <el-row class="MessageExtra" v-if="msg.from.id === store.getters.getUserInfo.id">
        <el-button size="small" @click="deleteMessage(msg.id)">删除</el-button>
      </el-row>
      <el-divider class="MessageDivider"/>
    </li>
  </ul>
  <div v-else>
    <el-text>暂无消息</el-text>
    <el-divider/>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { toUserPage } from '@/scripts/userInfo'
import { SnachResponse } from '@/scripts/types/ResponseObject'
import { getCookie } from '@/scripts/cookie'
import SnakeAvatar from "@/components/common/SnakeAvatar.vue";

const props = defineProps({
  articleId: {
    type: String,
    required: true
  }
})

interface UserResponse {
  name: string,
  pictureUrl: string
}

interface MessageResponse {
  id: string,
  from: string,
  message: string
}

interface UserInfo {
  name: string,
  id: string,
  avatar: string
}

interface Message {
  id: string,
  from: UserInfo,
  message: string,
}

const store = useStore()
const messageList = ref<Message[]>([])

function getUserInfo(userId: string) {
  let userInfo: UserInfo = {
    id: '',
    name: '',
    avatar: ''
  }
  $.get({
    url: 'http://localhost:19198/usr/getUserBasicInfo',
    enctype: 'multipart/form-data',
    async: false,
    data: {
      user_id: userId
    },
    success: (data: SnachResponse<{ user_info: UserResponse }>) => {
      if (data.code === 2001) {
        userInfo = {
          id: userId,
          name: data.data.user_info.name,
          avatar: data.data.user_info.pictureUrl
        }
      } else {
        console.log(data)
        ElMessage({
          message: 'Load user info failed.',
          type: 'error'
        })
      }
    }
  })
  return userInfo
}

function loadMessageList() {
  $.get({
    url: 'http://localhost:19198/message/getMessageListByArticleId',
    enctype: 'multipart/form-data',
    async: false,
    data: {
      'articleId': props.articleId
    },
    success: (data: SnachResponse<MessageResponse[]>) => {
      if (data.code === 2001) {
        console.log(data)
        for (let msg of data.data) {
          const userInfo = getUserInfo(msg.from)
          messageList.value.push({
            from: {
              id: msg.from,
              name: userInfo.name,
              avatar: userInfo.avatar
            },
            id: msg.id,
            message: msg.message
          })
        }
      } else {
        ElMessage({
          message: data.message,
          type: 'error'
        })
      }
    }
  })
}

function deleteMessage(messageId: string) {
  $.post({
    url: 'http://localhost:19198/message/deleteMessage',
    enctype: 'multipart/form-data',
    async: false,
    data: {
      'token': getCookie('token'),
      'messageId': messageId
    },
    success: (data: SnachResponse<boolean>) => {
      if (data.code === 2001) {
        ElMessage({
          message: '删除成功',
          type: 'success'
        })
        loadMessageList()
      } else {
        ElMessage({
          message: data.message,
          type: 'error'
        })
      }
    }
  })
}

loadMessageList()
</script>

<style lang="css" scoped>
.MessageList {
  list-style: none;
  padding-left: 5px;
}

.MessageDisplay {
  display: flex;
  margin-top: 20px;
  margin-bottom: 0;
}

.SenderName {
  width: 70px;
}

.SenderAvatar:hover, .SenderName:hover {
  cursor: pointer;
}

.MessageExtra {
  display: flex;
  justify-content: flex-end;
  margin: 0 10px 0 0;
}

.MessageDivider {
  margin-top: 10px;
  margin-bottom: 0;
}
</style>
