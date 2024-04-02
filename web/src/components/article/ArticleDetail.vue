<template>
  <el-tooltip
    v-if="store.getters.getUserInfo.identity === 'EXPERT' || store.getters.getUserInfo.identity === 'HUNTER' || store.getters.getUserInfo.identity === 'ADMINISTRATOR'"
    class="box-item" effect="light" content="解除锁定并退出" placement="top">
    <el-button class="exit-button" type="primary" @click="handleExitClicked" circle plain>
      <svg width="24" height="24" viewBox="0 0 48 48" fill="currentColor">
        <path fill-rule="evenodd" clip-rule="evenodd"
          d="M31 4a1 1 0 011 1v2a1 1 0 01-1 1H8v32h23a1 1 0 011 1v2a1 1 0 01-1 1H6a2 2 0 01-2-2V6a2 2 0 012-2h25zm4.846 10.658l7.778 7.778a2 2 0 010 2.828l-7.778 7.778a1 1 0 01-1.415 0l-1.414-1.414a1 1 0 010-1.414l4.235-4.235-18.206-.08a1 1 0 01-.996-.996l-.008-1.894a1 1 0 01.88-.997l.125-.007 18.572.082-4.602-4.601a1 1 0 010-1.414l1.414-1.414a1 1 0 011.415 0z"
          fill="currentColor" />
      </svg>
    </el-button>
  </el-tooltip>
  <div class="operation-afffix">
    <div class="like-container" @click="like()">
      <el-icon :size="24">
        <LikeBroken v-if="!isUp" />
        <LikeBold v-else />
      </el-icon>
      <div>{{ currentLikeCount }}</div>
    </div>
  </div>

  <el-row>
    <el-col :lg="18" :md="20" :sm="24" style="margin: auto;">
      <div>
        <el-container>
          <el-main>
            <el-card class="article-card">
              <el-row class="article-box-card">
                <el-text class="article-detail-title">{{ articleDetail.title }}</el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">
                  <el-button link :onclick="handleAuthorClicked">{{ articleDetail.textBy }}</el-button>
                  <!-- TODO: 作者信息-->
                  （五年级） 下北泽中学 指导老师：野兽先辈
                </el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">发布时间：{{ articleDetail.time }}</el-text>&nbsp;&nbsp;
                <el-text class="article-detail-author">&nbsp;<el-icon><View /></el-icon>&nbsp;{{ currentViewCount }}</el-text>
              </el-row>
              <div style="display: flex; justify-content:center;align-items: flex-end;">
                <el-button type="primary" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  @click="handleUpdateArticleClicked">修改文章
                </el-button>
                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  @click="delArticleDialogVisible = true">删除文章
                </el-button>
                <el-button type="warning" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  :onclick="handleLockClicked">{{ isLocked ? '取消锁定' : '锁定' }}
                </el-button>
                <el-button link type="primary" :onclick="handleDiscriptionSmall" style="font-size: 16px;">小
                </el-button>
                <el-button link type="primary" :onclick="handleDiscriptionMedium" style="font-size: 18px;">中
                </el-button>
                <el-button link type="primary" :onclick="handleDiscriptionLarge" style="font-size: 20px;">大
                </el-button>
              </div>

              <el-collapse style="padding-top: 10px">
                <div class="description-head"><span>文章描述</span></div>
                <el-text class="article-description">{{ articleDetail.description }}</el-text>
                <div class="contain-head"><span>文章内容</span></div>
                <ArticleDisplayCard :articleRaw="articleDetail.raw" :lock-before-preview="false"
                  :article-id="articleDetail.id"></ArticleDisplayCard>
              </el-collapse>

            </el-card>

            <el-card v-if="articleDetail.text_by_id === store.getters.getUserInfo.id">
              <div class="contain-head">
                <span>专家/报社反馈</span>
              </div>
              <UserMessageDisplay :article-id="route.query.id" />

              <div class="contain-head">
                <span>审核建议</span>
              </div>
              <el-text class="article-reason" :size="displaySize">{{ articleDetail.reason === '' ? '暂无建议' :
      articleDetail.reason }}</el-text>
            </el-card>

            <el-card v-if="displayMessage">
              <el-text class="MessageInputTag">反馈:</el-text>
              <el-input class="MessageInputBox" v-model="messageInputText" :disabled="store.getters.getToken === ''"
                :placeholder="store.getters.getToken === '' ? '请先登录后评论! ' : ''" type="textarea"
                :autosize="{ maxRows: 3, minRows: 3 }" />
              <div class="MessageAddSubmit">
                <el-button @click="addMessage(route.query.id, messageInputText)"
                  :disabled="store.getters.getToken === ''">反馈</el-button>
              </div>
            </el-card>

          </el-main>
          <el-footer>
            <suspense>
              <CommentDisplay :articleId="route.query.id" />
            </suspense>
          </el-footer>
        </el-container>
      </div>
      <el-dialog draggable v-model="delArticleDialogVisible" title="删除文章" width="30%">
        <span>确定删除文章？</span>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="delArticleDialogVisible = false">取消</el-button>
            <el-button type="danger" @click="handleDelArticleClicked">
              删除
            </el-button>
          </span>
        </template>
      </el-dialog>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { AttributeAddableObject } from '@/scripts/ArticleTagFilter'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SYNC_GET, SYNC_POST } from '@/scripts/Axios'
import { useStore } from 'vuex'
import CommentDisplay from '@/components/article/CommentDisplay.vue'
import { toUserPage } from '@/scripts/userInfo'
import { errorCallback } from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import { lockArticleById, unlockArticle } from '@/scripts/ArticleLocker'
import { View } from "@element-plus/icons-vue";
import UserMessageDisplay from "@/components/article/UserMessageDisplay.vue";
import LikeBroken from "@/components/common/SnakeIcons/LikeBroken.vue";
import LikeBold from "@/components/common/SnakeIcons/LikeBold.vue";
import { getCookie } from "@/scripts/cookie";
import { SnachResponse } from "@/scripts/types/ResponseObject";

const router = useRouter()
const route = useRoute()
const store = useStore()

const articleDetail = reactive<AttributeAddableObject>({
  id: null,
  text: '',
  time: '',
  textBy: '',
  text_by_id: '',
  title: '',
  description: '',
  auditStatus: '',
  publishStatus: '',
  tags: '{}',
  raw: {},
  fileType: '',
  receivedBy: '',
  reason: '',
})

const isLocked = ref(false)
const delArticleDialogVisible = ref(false)
const displayMessage = ref(false)
const currentStatus = ref('')
const currentLikeCount = ref(0)
const currentViewCount = ref(0)

let isUp = ref(false)

const messageInputText = ref('')

function handleDiscriptionSmall() {
  $('.article-description').css('font-size', '16px')
}

function handleDiscriptionMedium() {
  $('.article-description').css('font-size', '18px')
}

function handleDiscriptionLarge() {
  $('.article-description').css('font-size', '20px')
}

async function getTextBy() {
  articleDetail.text_by_id = articleDetail.textBy
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: articleDetail.textBy
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      articleDetail.textBy = response.data.data.user_info.name
    } else {
      console.log(response)
    }
  })
}

async function getLikeStatus() {
  if (store.getters.getUserInfo.identity === '未登录') {
    isUp.value = false
    return
  }
  await SYNC_GET('/like/getLikeStatus', {
    articleId: route.query.id,
    userId: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      currentStatus.value = response.data.data.currentStatus
      // console.log(currentStatus.value)
      if (currentStatus.value === 'like') {
        isUp.value = true
      } else {
        isUp.value = false
      }
    }
  })
}

async function getLikeCount() {
  await SYNC_GET('/like/getLikeCountByArticleID', {
    articleId: route.query.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      currentLikeCount.value = response.data.data.currentLikeCount
    }
  })
}

async function getViewCount() {
  await SYNC_GET('/view/getViewCount', {
    articleId: route.query.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      currentViewCount.value = response.data.data.currentViewCount
    }
    else {
      errorCallback(response)
    }
  })
}

const like = async () => {
  if (store.getters.getUserInfo.identity === '未登录') {
    ElMessage({
      showClose: true,
      message: '请先登录',
      type: 'warning',
      grouping: true,
    })
    return
  }
  await SYNC_POST('/like/like', {
    token: store.getters.getToken,
    articleId: route.query.id,
    userId: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      if (response.data.data.currentStatus === 'like') {
        isUp.value = true
        currentLikeCount.value += 1
        $('.like-container').addClass('liked')
        ElMessage({
          showClose: true,
          message: '点赞成功',
          type: 'success',
          grouping: true,
        })
      } else {
        isUp.value = false
        currentLikeCount.value -= 1
        $('.like-container').removeClass('liked')
        ElMessage({
          showClose: true,
          message: '取消点赞成功',
          type: 'success',
          grouping: true,
        })
      }
    } else {
      errorCallback(response)
    }
  })
}

const addViewCount = async () => {
  await SYNC_POST('/view/addViewCount', {
    articleId: route.query.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      currentViewCount.value = response.data.data.currentViewCount
    } else {
      errorCallback(response)
    }
  })
}

function addMessage(articleId: string, message: string) {
  $.post({
    url: 'http://localhost:19198/message/addMessage',
    enctype: 'multipart/form-data',
    async: false,
    data: {
      token: getCookie('token'),
      from: JSON.parse(getCookie('userInfo'))['id'],
      to: articleId,
      message: message
    },
    success: (data: SnachResponse<boolean>) => {
      if (data.code === 2001) {
        ElMessage({
          message: '添加成功',
          type: 'success'
        })
      } else {
        ElMessage({
          message: data.message,
          type: 'error'
        })
      }
    }
  })
}

onMounted(async () => {
  await getLikeStatus()
  if (isUp.value) {
    $('.like-container').addClass('liked')
  }
  // 设置5秒后执行跳转操作
  setTimeout(async () => {
    addViewCount()
  }, 5000); // 5000毫秒即5秒

});

async function getRaw(articleId: String) {
  axios({
    url: '/article/getArticleFileById',
    method: 'GET',
    headers: { 'Content-Type': 'multipart/form-data' },
    params: { article_id: articleId },
    responseType: 'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data], { type: articleDetail.fileType })
    articleDetail.raw = new File([blob], articleDetail.title, { type: articleDetail.fileType })

  }).catch(error => {
    console.error(error);
  });
}

async function getIsLocked() {
  await SYNC_GET('/article/checkLocked', {
    articleId: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      isLocked.value = response.data.data
    } else {
      errorCallback(response)
    }
  })
}

async function handleUnlock() {
  // 如果非公开且已刊登，无法手动解锁
  if (articleDetail.publishStatus === 'POSTED') {
    let expire = 0;
    await SYNC_GET("/article/getArticleLockExpire", { articleId: articleDetail.id }, (response) => {
      if (response.status === 200 && response.data.code === 2001) {
        expire = response.data.data
      } else {
        errorCallback(response)
      }
    })

    let message = ''
    if (expire > 86400) {
      message = `文章已发布，无法手动解锁，将于${Math.ceil(expire / 86400)}天后解锁。`
    } else if (expire > 3600) {
      message = `该文章已发布，无法手动解锁，将于${Math.ceil(expire / 3600)}小时后解锁。`
    } else if (expire > 0) {
      message = `该文章已发布，无法手动解锁，将于${Math.ceil(expire / 60)}分钟后解锁。`
    } else {
      isLocked.value = false
    }
    ElMessage({
      type: 'warning',
      message: message,
      showClose: true
    })
  } else {
    await unlockArticle(articleDetail.id, store.getters.getUserInfo.id)
    isLocked.value = !isLocked.value
  }
}

async function handleLock() {
  await lockArticleById(articleDetail.id, store.getters.getUserInfo.id, 5184000)
  isLocked.value = !isLocked.value
}

async function handleExitClicked() {
  await SYNC_POST('/article/checkLocked', {
    articleId: route.query.id,
    // requester: store.getters.getUserInfo.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      if (response.data.data === true) {
        await SYNC_POST('/article/getPermissions', {
          articleId: route.query.id,
          requester: store.getters.getUserInfo.id,
        }, async (response) => {
          if (response.status === 200 && response.data.code === 2001) {
            await unlockArticle(articleDetail.id, store.getters.getUserInfo.id)
          }
        })
      }
    }
  })
  router.back()
  return
}
async function handleLockClicked() {
  if (isLocked.value) {
    // 文章已锁定，执行解锁相关逻辑
    await handleUnlock()
  } else {
    // 文章未锁定，执行锁定相关逻辑
    await handleLock()
  }

}

const handleAuthorClicked = () => {
  if (articleDetail.textBy !== '' && articleDetail.textBy !== undefined) {
    // router.push('/user/'+articleDetail.text_by_id)
    toUserPage(articleDetail.text_by_id)
  } else {
    router.push({ path: '/userNotFound' })
  }
}
const handleDelArticleClicked = async () => {
  await SYNC_POST('/contributor/delArticle', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      ElMessage({
        showClose: true,
        message: '删除文章成功',
        type: 'warning'
      })
    } else {
      errorCallback(response)
    }
  })
  delArticleDialogVisible.value = false
  router.back()
}
const handleUpdateArticleClicked = () => {
  if (articleDetail.id !== '' && articleDetail.id !== undefined) {
    router.push({ path: '/articleEditor', query: { id: articleDetail.id } })
  } else {
    router.push({ path: '/articleNotFound' })
  }
  getIsLocked()
}

// 有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  if(getCookie('userInfo') !== '' && (JSON.parse(getCookie('userInfo'))?.identity === 'EXPERT'|| JSON.parse(getCookie('userInfo'))?.identity === 'HUNTER')){
    displayMessage.value = true
  }
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      for (const dataKey in response.data.data.article) {
        if (dataKey == 'raw') {
          continue
        }
        articleDetail[dataKey] = response.data.data.article[dataKey]
      }
      await getIsLocked()
      await getTextBy()
      await getRaw(route.query.id)
      await getLikeStatus()
      await getLikeCount()
      await getViewCount()

    } else {
      errorCallback(response)
    }
  })
})()
</script>
<style scoped>
.exit-button {
  width: 65px;
  height: 65px;
  position: absolute;
  bottom: 100px;
  right: 30px;
  z-index: 100;
}

.operation-afffix {
  position: fixed;
  right: 30px;
  bottom: 20px;
  z-index: 999;
}

.like-container {
  border-radius: 50%;
  border: 1px solid #ccc;
  background-color: #fff;
  padding: 10px;
  width: 45px;
  height: 45px;
}

.like-container:hover {
  cursor: pointer;
  color: #409eff;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
}

.liked {
  color: #409eff;
}

.article-card {
  margin-bottom: 20px;
}

.article-box-card {
  display: flex;
  justify-content: center;
}

.article-detail-title {
  font-size: 20px;
  font-weight: bold;
}

.description-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.article-detail-author {
  font-size: 14px;
}

.contain-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.article-description {
  font-size: 16px;
  display: flex;
  white-space: pre-wrap;
  text-align: start !important;
  margin-bottom: 20px;
  margin-left: 10px;
  margin-right: 10px;
}

.article-reason {
  display: flex;
  white-space: pre-wrap;
  text-align: start !important;
  margin-bottom: 20px;
  margin-left: 10px;
  margin-right: 10px;
}

.MessageInputTag {
  display: flex;
  justify-content: flex-start;
  margin: 0 0 10px 5px;
  font-size: 20px;
}

.MessageInputBox {
  font-size: 15px;
}

.MessageAddSubmit {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  margin-right: 15px;
}

/*
.circle {
  width: 20px;
  height: 20px;
  margin: 10px;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0px 0px 0px 0px rgba(223, 46, 58, 0.5);

  .img-box {
    width: 20px;
    height: 20px;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    -khtml-user-select: none;
    user-select: none; */

/* 防止快速点击图片被选中*/
/* & img {
      width: 100%;
      height: 100%;
    }
  }
} */
/*
.check {
  -webkit-transition: box-shadow 0.5s;
  -moz-transition: box-shadow 0.5s;
  -o-transition: box-shadow 0.5s;
  transition: box-shadow 0.5s;
  box-shadow: 0px 0px 0px 1em rgba(226, 32, 44, 0);
}

.img-box-check {
  animation: anm 0.5s;
  -moz-animation: anm 0.5s;
  -webkit-animation: anm 0.5s;
  -o-animation: anm 0.5s;
}

@keyframes anm {
  0% {
    transform: scale(0);
    -webkit-transform: scale(0);
    -moz-transform: scale(0);
  }

  50% {
    transform: scale(1.3);
    -webkit-transform: scale(1.3);
    -moz-transform: scale(1.3);
  }

  100% {
    transform: scale(1);
    -webkit-transform: scale(1);
    -moz-transform: scale(1);
  }
} */

/* 以下为处理兼容代码，可不看。*/
/*
@-moz-keyframes anm {
  0% {
    transform: scale(0);
    -webkit-transform: scale(0);
    -moz-transform: scale(0);
  }

  50% {
    transform: scale(1.3);
    -webkit-transform: scale(1.3);
    -moz-transform: scale(1.3);
  }

  100% {
    transform: scale(1);
    -webkit-transform: scale(1);
    -moz-transform: scale(1);
  }
}

@-webkit-keyframes anm {
  0% {
    transform: scale(0);
    -webkit-transform: scale(0);
    -moz-transform: scale(0);
  }

  50% {
    transform: scale(1.3);
    -webkit-transform: scale(1.3);
    -moz-transform: scale(1.3);
  }

  100% {
    transform: scale(1);
    -webkit-transform: scale(1);
    -moz-transform: scale(1);
  }
}

@-o-keyframes anm {
  0% {
    transform: scale(0);
    -webkit-transform: scale(0);
    -moz-transform: scale(0);
  }

  50% {
    transform: scale(1.3);
    -webkit-transform: scale(1.3);
    -moz-transform: scale(1.3);
  }

  100% {
    transform: scale(1);
    -webkit-transform: scale(1);
    -moz-transform: scale(1);
  }
} */
</style>
