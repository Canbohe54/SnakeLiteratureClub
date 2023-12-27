<template>
  <el-card>
    <ul v-if="commentDisplayConf.maxCommentRowsNum > 0" v-infinite-scroll="loadCommentList" style="list-style: none; padding-left: 5px">
      <li v-for="(item) in commentList">
        <el-row>
          <el-avatar> user </el-avatar>
          <div class="blank"></div>
          <el-text>{{ item.commenter }}</el-text>
          <div class="e"></div>
          <el-text>{{ item.text }}</el-text>
        </el-row>
        <el-divider/>
      </li>
    </ul>
    <div v-else>
      <el-text>没有评论? 点击上方新建!</el-text>
      <el-divider/>
    </div>
    <el-text v-if="commentDisplayConf.nowDisplayRowsNum >= commentDisplayConf.maxCommentRowsNum" type="info">我也是有底线的~</el-text>
  </el-card>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { SYNC_GET, SYNC_POST } from '@/scripts/Axios'
import { ElMessage } from 'element-plus'

interface Comment {
  commenter: string,
  text: string,
  id: string,
  time: string,
  commenterId: string,
  commenterAvatar: string
}

const props = defineProps({
  articleId: {
    type: String,
    required: true
  }
})
const commentList = ref<Comment[]>([])
const commentDisplayConf = ref({
  nowDisplayRowsNum: 0,
  eachLoadLimit: 1,
  maxCommentRowsNum: 0
})

async function getUserInfo (userId: string) {
  let userInfo
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: userId
  }, (response) => {
    if (response.data.statusMsg === 'Success.') {
      userInfo = {
        name: response.data['user_info']['name'],
        avatar: response.data['user_info']['pictureUrl']
      }
    } else {
      userInfo = null
    }
  })
  return userInfo
}

async function loadCommentList () {
  await SYNC_POST('/comm/load', {
    id: props.articleId,
    startAt: commentDisplayConf.value.nowDisplayRowsNum,
    limit: commentDisplayConf.value.eachLoadLimit
  }, async (response) => {
    if (response.data.statusMsg === 'Success.') {
      for (const commentRes of response.data.res[0]) {
        const commenter = await getUserInfo(commentRes['text_by'])
        if (commenter === null) {
          ElMessage({
            message: '获取评论用户信息失败!',
            type: 'error'
          })
          continue
        }
        commentList.value.push({
          commenter: commenter['name'],
          commenterAvatar: commenter['avatar'],
          commenterId: commentRes['text_by'],
          id: commentRes['id'],
          text: commentRes['text'],
          time: commentRes['time']
        })
      }
      commentDisplayConf.value.maxCommentRowsNum = response.data.rowsNum
      commentDisplayConf.value.nowDisplayRowsNum += commentDisplayConf.value.eachLoadLimit
    } else {
      ElMessage({
        message: '导入评论列表失败!',
        type: 'error'
      })
      return
    }
  })
}

await loadCommentList()
</script>

<style>

</style>
