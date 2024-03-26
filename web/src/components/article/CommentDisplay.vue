<template>
  <el-card class="CommentAdd">
    <el-text class="CommentInputTag">评论:</el-text>
    <el-input
        class="CommentInputBox"
        v-model="CommentInputText"
        :disabled="store.getters.getToken === ''"
        :placeholder="store.getters.getToken === '' ? '请先登录后评论! ' : ''"
        type="textarea"
        :autosize="{ maxRows: 3, minRows: 3 }"/>
    <div class="CommentAddSubmit">
      <el-button
          @click="addComment(props.articleId, CommentInputText)"
          :disabled="store.getters.getToken === ''"
      >评论</el-button>
    </div>
  </el-card>
  <el-card>
    <ul class="CommentList" v-if="commentDisplayConf.maxCommentRowsNum > 0" v-infinite-scroll="loadCommentList">
      <li v-for="(item) in commentList">
        <el-row class="CommentDisplay">
          <el-avatar :src="item.commenterAvatar" @click="toUserPage(item.commenterId)" class="CommenterAvatar"/>
          <div class="blank"></div>
          <el-text class="CommenterName" @click="toUserPage(item.commenterId)">{{ item.commenter }}</el-text>
          <div class="e"></div>
          <el-text>{{ item.text }}</el-text>
        </el-row>
        <el-row class="CommentExtra" v-if="item.commenterId === store.getters.getUserInfo.id">
          <el-button size="small" @click="deleteComment(item.id)">Delete</el-button>
        </el-row>
        <el-divider class="CommentDivider"/>
      </li>
    </ul>
    <div v-else>
      <el-text>没有评论? 点击上方新建!</el-text>
      <el-divider/>
    </div>
    <el-text v-if="commentDisplayConf.nowDisplayRowsNum >= commentDisplayConf.maxCommentRowsNum" type="info">我也是有底线的~</el-text>
  </el-card>
  <p style="height: 20px" ></p>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { SYNC_GET, SYNC_POST } from '@/scripts/Axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { toUserPage } from '@/scripts/userInfo'

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
const router = useRouter()
const store = useStore()
const commentList = ref<Comment[]>([])
const commentDisplayConf = ref({
  nowDisplayRowsNum: 0,
  eachLoadLimit: 10,
  maxCommentRowsNum: 0
})
const CommentInputText = ref('')

async function getUserInfo (userId: string) {
  let userInfo
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: userId
  }, (response) => {
    if (response.data.code === 2001) {
      userInfo = {
        name: response.data.data['user_info']['name'],
        avatar: response.data.data['user_info']['pictureUrl']
      }
    } else {
      console.log(response)
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
    if (response.data.code === 2001) {
      for (const commentRes of response.data.data[0]) {
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

async function deleteComment (commentId: string) {
  await SYNC_POST('/comm/del', {
    token: store.getters.getToken,
    id: commentId
  }, async (response) => {
    if (response.data.statusMsg === 'Success.') {
      ElMessage({
        message: '评论删除成功! ',
        type: 'success'
      })
      location.reload()
    } else {
      ElMessage({
        message: response.data.statusMsg,
        type: 'warning'
      })
    }
  })
}

async function addComment (textOn: string, text: string) {
  await SYNC_POST('/comm/add', {
    token: store.getters.getToken,
    textOn: textOn,
    text: text
  }, async (response) => {
    if (response.data.statusMsg === 'Success.') {
      ElMessage({
        message: '评论添加成功! ',
        type: 'success'
      })
      location.reload()
    } else {
      ElMessage({
        message: response.data.statusMsg,
        type: 'warning'
      })
    }
  })
}

// function toUserPage (id: string) {
//   router.push(`/user/${id}/article`)
// }

await loadCommentList()
</script>

<style lang="scss" scoped>
.CommentList {
  list-style: none;
  padding-left: 5px;
}

.CommentDisplay {
  margin-bottom: 0;
}

.CommenterName {
  width: 70px;
}

.CommenterAvatar:hover, .CommenterName:hover {
  cursor: pointer;
}

.CommentExtra {
  display: flex;
  justify-content: flex-end;
  margin-right: 5px;
}

.CommentDivider {
  margin-top: 10px;
  margin-bottom: 15px;
}

.CommentInputTag {
  display: flex;
  justify-content: flex-start;
  margin: 0 0 10px 5px;
  font-size: 20px;
}

.CommentAdd {
  margin-bottom: 20px;
}

.CommentInputBox {
  font-size: 15px;
}

.CommentAddSubmit {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  margin-right: 15px;
}
</style>
