<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-container>
          <el-main>
            <el-card class="article-card">
              <el-row class="article-box-card">
                <el-text class="article-detail-title">{{ articleDetail.title }}</el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">（
                  <el-button link :onclick="handleAuthorClicked">{{ articleDetail.text_by }}</el-button>
                  ） {{ articleDetail.time }}
                </el-text>
              </el-row>
              <div style="display: flex; justify-content:center;align-items: flex-end;">
                <el-button type="primary" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                           @click="handleUpdateArticleClicked">修改文章
                </el-button>
                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                           @click="delArticleDialogVisible=true">删除文章
                </el-button>

                <el-button type="warning" link :onclick="handleLockClicked">{{
                    isLocked ? '取消锁定' : '锁定'
                  }}
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='small'}" style="font-size: small;">小
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='default'}" style="font-size: medium;">中
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='large'}" style="font-size: large;">大
                </el-button>
              </div>

              <el-divider/>
              <ArticleDisplayCard :articleRaw="articleDetail.raw" :lock-before-preview="false" :article-id="articleDetail.id"></ArticleDisplayCard>
              <el-text class="article-description" :size="displaySize">{{ articleDetail.description }}</el-text>
<!--              点赞-->
              <div class="circle flex-h" @click="like()" :class="isUp?'check':''">
                <div class="img-box" :class="isUp?'img-box-check':''">
                  <img v-if="isUp" src="@/assets/images/like.svg" alt="" />
                  <img v-else src="@/assets/images/unlike.svg" alt="" />
                </div>
              </div>
              <div class="likeCount">
                {{ currentLikeCount }}
              </div>
              <div>
                <el-icon>
                  <View />
                </el-icon>
                <div>{{ currentViewCount }}</div>
              </div>
              <el-collapse style="padding-top: 10px">
                <div class="description-head"><span>文章描述</span></div>
                <el-text class="article-description" :size="displaySize">{{ articleDetail.description }}</el-text>
                <div class="contain-head"><span>文章内容</span></div>
                <ArticleDisplayCard :articleRaw="articleDetail.raw" :lock-before-preview="false"
                                    :article-id="articleDetail.id"></ArticleDisplayCard>

              </el-collapse>

            </el-card>

          <el-card v-if="articleDetail.text_by_id === store.getters.getUserInfo.id">
            <div class="contain-head">
              <span>专家/报社反馈</span>
              <UserMessageDisplay :article-id="route.query.id" />
            </div>

            <div class="contain-head">
              <span>审核建议</span></div>
            <el-text class="article-reason" :size="displaySize">{{ articleDetail.reason === '' ? '暂无建议' : articleDetail.reason }}
            </el-text>
          </el-card>
          </el-main>
          <el-footer>
            <suspense>
              <CommentDisplay :articleId="route.query.id"/>
            </suspense>
          </el-footer>
        </el-container>
      </div>
      <el-dialog
        draggable
        v-model="delArticleDialogVisible"
        title="删除文章"
        width="30%"
      >
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
import {onMounted, reactive, ref} from 'vue'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import CommentDisplay from '@/components/article/CommentDisplay.vue'
import {toUserPage} from '@/scripts/userInfo'
import {errorCallback} from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import {lockArticleById, unlockArticle} from '@/scripts/ArticleLocker'
import {View} from "@element-plus/icons-vue";
import UserMessageDisplay from "@/components/article/UserMessageDisplay.vue";

// 该页面没有锁
const router = useRouter()
const route = useRoute()
const store = useStore()

const articleDetail = reactive<AttributeAddableObject>({
  id: null,
  text: '',
  time: '',
  text_by: '',
  text_by_id: '',
  title: '',
  description: '',
  status: '',
  tags: '{}',
  raw: {},
  file_type: '',
  received_by: '',
  reason: '',
})

const displaySize = ref("default")

const isLocked = ref(false)

const delArticleDialogVisible = ref(false)

const currentStatus = ref('')
const currentLikeCount = ref(0)
const currentViewCount = ref(0)

let isUp = ref(false)
function handleClick () {
  isUp.value = !isUp.value
}

async function getTextBy() {
  articleDetail.text_by_id = articleDetail.text_by
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: articleDetail.text_by
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      articleDetail.text_by = response.data.data.user_info.name
    } else {
      console.log(response)
    }
  })
}

async function getLikeStatus() {
  if(store.getters.getUserInfo.identity === '未登录'){
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
      if(currentStatus.value === 'like'){
        isUp.value = true
      } else{
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
    else{
        errorCallback(response)
    }
  })
}

const like = async () => {
  if(store.getters.getUserInfo.identity === '未登录'){
    ElMessage({
      showClose: true,
      message: '请先登录',
      type: 'warning'
    })
    return
  }
  await SYNC_POST('/like/like', {
    token: store.getters.getToken,
    articleId: articleDetail.id,
    userId: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      if(response.data.data.currentStatus === 'like'){
        isUp.value = true
        currentLikeCount.value += 1
        ElMessage({
          showClose: true,
          message: '点赞成功',
          type: 'success'
        })
      } else{
        isUp.value = false
        currentLikeCount.value -= 1
        ElMessage({
          showClose: true,
          message: '取消点赞成功',
          type: 'success'
        })
      }
    } else {
      errorCallback(response)
    }
  })
}

const addViewCount = async () => {
  await SYNC_POST('/view/addViewCount', {
    token: store.getters.getToken,
    articleId: articleDetail.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      currentViewCount.value = response.data.data.currentViewCount
    } else {
      errorCallback(response)
    }
  })
}


onMounted(() => {
    // 设置5秒后执行跳转操作
    setTimeout(() => {
        addViewCount()
    }, 5000); // 5000毫秒即5秒
});

async function getRaw(articleId: String) {
  axios({
    url: '/article/getArticleFileById',
    method: 'GET',
    headers: {'Content-Type': 'multipart/form-data'},
    params: {article_id: articleId},
    responseType: 'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data], {type: articleDetail.file_type})
    articleDetail.raw = new File([blob], articleDetail.title, {type: articleDetail.file_type})

  }).catch(error => {
    console.error(error);
  });
}

async function getIsLocked() {
  await SYNC_GET('/article/checkLocked', {
    articleId: articleDetail.id
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
  if (articleDetail.status === 'PUBLISHED' && articleDetail.received_by !== '' && !articleDetail.public) {
    let expire = 0;
    await SYNC_GET("/article/getArticleLockExpire", {articleId: articleDetail.id}, (response) => {
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
  if (articleDetail.text_by !== '' && articleDetail.text_by !== undefined) {
    // router.push('/user/'+articleDetail.text_by_id)
    toUserPage(articleDetail.text_by_id)
  } else {
    router.push({path: '/userNotFound'})
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
    router.push({path: '/articleEditor', query: {id: articleDetail.id}})
  } else {
    router.push({path: '/articleNotFound'})
  }
  getIsLocked()
}

// 有article_id时初始化ArticleDetail
(async () => {
  let articleRaw: ArrayBuffer
  if (route.query.id === '' || route.query.id === undefined) return
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
      await getRaw(articleDetail.id)
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
    user-select: none; /* 防止快速点击图片被选中，可不加，为提高体验，博主加上了这几行代码。*/
    & img {
      width: 100%;
      height: 100%;
    }
  }
}
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
}

/* 以下为处理兼容代码，可不看。*/

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
}

.likeCount {
  text-align: left;
}
</style>
