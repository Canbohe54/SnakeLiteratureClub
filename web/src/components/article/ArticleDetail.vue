<template>
  <el-tooltip
      v-if="acceptManager.showAcceptButton"
      class="box-item" effect="light" content="收录文章" placement="top">
    <el-button class="accept-button" type="success" @click="handleAcceptClicked" circle plain>
      <svg width="24" height="24" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M32 6H22V42H32V6Z" fill="none" stroke="#333" stroke-width="4" stroke-linejoin="round"/><path d="M42 6H32V42H42V6Z" fill="none" stroke="#333" stroke-width="4" stroke-linejoin="round"/><path d="M10 6L18 7L14.5 42L6 41L10 6Z" fill="none" stroke="#333" stroke-width="4" stroke-linejoin="round"/><path d="M37 18V15" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/><path d="M27 18V15" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </el-button>
  </el-tooltip>
  <el-tooltip
      v-if="recommendManager.showRecommendButton"
      class="box-item" effect="light" content="向报社推荐" placement="top">
    <el-button class="recommend-button" type="success" @click="handleRecommendClicked" circle plain>
      <svg width="24" height="24" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M40 4H8V44H40V4Z" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/><path d="M24 12V36" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/><path d="M40 36H24H8" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/><path d="M8 4L14 12H34L40 4" stroke="#333" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </el-button>
  </el-tooltip>
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
                  <el-button v-if="textByIdentity === 'CONTRIBUTOR'" link :onclick="handleAuthorClicked">{{
      articleDetail.textBy }}</el-button>
                  <span v-else>{{ articleDetail.authorName }}</span>
                  {{ articleDetail.authorGrade ? "（" + articleDetail.authorGrade + "）" : "" }}
                  {{ articleDetail.authorOrganization }}
                  <span v-if="articleDetail.mentor !== ''">指导老师：
                    <el-button v-if="textByIdentity !== 'CONTRIBUTOR'" link :onclick="handleAuthorClicked">{{
      articleDetail.mentor }}</el-button>
                    <span v-else>{{ articleDetail.mentor }}</span>
                  </span>
                </el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">发布时间：{{ articleDetail.time }}</el-text>&nbsp;&nbsp;
                <el-text class="article-detail-author">&nbsp;<el-icon>
                    <View />
                  </el-icon>&nbsp;{{ currentViewCount }}</el-text>
              </el-row>
              <div style="display: flex; justify-content:center;align-items: flex-end;">
                <el-button type="primary" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  @click="handleUpdateArticleClicked">修改文章
                </el-button>
                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  @click="handleDeleteClicked">删除文章
                </el-button>
                <el-button type="warning" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                  :onclick="handleLockClicked">{{ isLocked ? '设置所有人可见' : '设置仅自己（和收稿者）可见' }}
                </el-button>
                <el-button type="success" link v-if="postButtonVisible" :onclick="handlePOSTEDClicked">刊登
                </el-button>
                <el-button type="warning" link v-if="postButtonVisible" :onclick="handleNotPostClicked">不刊登
                </el-button>
                <!--                <el-button type="warning" link v-if="publicButtonVisible"-->
                <!--                           :onclick="handleViewableClicked">{{!onlyMyself ? '设置仅自己可见': '设置所有人可见'}}-->
                <!--                </el-button>-->
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
                <div class="contain-head"><span>文章内容</span> <el-button link v-if="isDetailCssChange" type="primary"
                    :onclick="handleDetailRemoveCSS" >恢复默认字体大小
                  </el-button></div>
                <ArticleDisplayCard class="article-disp-card" :articleRaw="articleDetail.raw"
                  :lock-before-preview="false" :article-id="articleDetail.id"></ArticleDisplayCard>
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
      <el-dialog draggable v-model="notPostedVisible" title="删除文章" width="30%">
        <span>确定拒绝收录邀请？(文章将被设置为公开)</span>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="notPostedVisible = false">取消</el-button>
            <el-button type="warning" @click="handleUnPOSTEDClicked">
              确定拒绝
            </el-button>
          </span>
        </template>
      </el-dialog>
      <el-dialog
          draggable
          v-model="acceptManager.acceptDialogVisible"
          title="收录文章"
          width="30%"
      >
        <div class="accept-head"><span>收录邀请</span></div>
        <el-input v-model="acceptManager.acceptInfo"
                  maxlength="200"
                  show-word-limit
                  class="accept-info-input"
                  :autosize="{ minRows: 4, maxRows: 10}"
                  type="textarea"
                  placeholder="例如：我是xxx报社的xxx，对您的文章很有兴趣，是否可以收录和刊登您的文章？"></el-input>
        <span class="accept-button-container">
        <el-button type="primary" @click="handleAcceptArticleClicked">
          收录
        </el-button>
        <el-button @click="acceptManager.acceptDialogVisible = false">取消</el-button>
        </span>
      </el-dialog>
      <el-dialog
          draggable
          v-model="recommendManager.recommendDialogVisible"
          title="向报社推荐"
          width="30%"
      >
        <span class="recommend-to-head">收稿方</span>
        <el-select
            class="contribute-to-selection"
            v-model="recommendManager.recommendTo"
            filterable
            remote
            reserve-keyword
            placeholder="请输入收稿方"
            no-data-text="暂无匹配的收稿方"
            no-match-text="暂无匹配的收稿方"
            default-first-option
            remote-show-suffix
            :remote-method="getHunterByName"
            :loading="recommendManager.loading"
            style="width: 240px"
        >
          <el-option
              v-for="item in recommendManager.options"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          >
            <span style="float: left">{{ item.name }}</span>
            <span
                style="float: right;color: var(--el-text-color-secondary);font-size: 13px;"
            >{{ item.organization }}</span
            >
          </el-option>
          <template #loading>
            <svg class="circular" viewBox="0 0 50 50">
              <circle class="path" cx="25" cy="25" r="20" fill="none"/>
            </svg>
          </template>
        </el-select>
        <template #footer>
      <span class="accept-button-container">
        <el-button type="primary" @click="handleRecommendArticleClicked">
          推荐
        </el-button>
        <el-button @click="recommendManager.recommendDialogVisible = false">取消</el-button>

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
const displaySize = ref("default")
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
const acceptManager = reactive({
  acceptDialogVisible: false,
  acceptInfo: '我是xxx报社的xxx，对您的文章很有兴趣，是否可以收录和刊登您的文章？',
  showAcceptButton: false,
})

const recommendManager = reactive({
  recommendDialogVisible: false,
  recommendTo: '',
  options: [],
  loading: false,
  showRecommendButton: false,
})
const redirectToArticle = (subPath: string) => {
  router.push({
    path: subPath,
    query: {
      id: articleDetail.id
    }
  })
}

const isLocked = ref(false)
const delArticleDialogVisible = ref(false)
const displayMessage = ref(false)
const currentStatus = ref('')
const currentLikeCount = ref(0)
const currentViewCount = ref(0)
const textByIdentity = ref('CONTRIBUTOR')
const publicButtonVisible = ref(false)
const postButtonVisible = ref(false)
const onlyMyself = ref(false)
const notPostedVisible = ref(false)
let isUp = ref(false)

const messageInputText = ref('')

const isDetailCssChange = ref(false)

function handleDiscriptionSmall() {
  $('.article-description').css('font-size', '16px')
  if($('#docxContainer .docx p span').length !== 0){
    $('#docxContainer .docx p span').css('font-size', '16px')
    isDetailCssChange.value = true
  } else {
    $('.txtPreview').css('font-size', '16px')
  }

}

function handleDiscriptionMedium() {
  $('.article-description').css('font-size', '18px')
  if($('#docxContainer .docx p span').length !== 0){
    $('#docxContainer .docx p span').css('font-size', '18px')
    isDetailCssChange.value = true
  }else {
    $('.txtPreview').css('font-size', '18px')
  }
}

function handleDiscriptionLarge() {
  $('.article-description').css('font-size', '20px')
  if($('#docxContainer .docx p span').length !== 0){
    $('#docxContainer .docx p span').css('font-size', '20px')
    isDetailCssChange.value = true
  }else {
    $('.txtPreview').css('font-size', '20px')
  }
}

function handleDetailRemoveCSS() {
  $('#docxContainer .docx p span').css('font-size', '')
  isDetailCssChange.value = false
}

async function getTextBy() {
  articleDetail.text_by_id = articleDetail.textBy
  await SYNC_GET('/usr/getUserBasicInfo', {
    user_id: articleDetail.textBy
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      articleDetail.textBy = response.data.data.user_info.name
      textByIdentity.value = response.data.data.user_info.identity
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
  if (route.query.id === undefined || (route.query.id === null || route.query.id === '')) {
    return
  }
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
      message = `文章已发布，无法手动解锁，将于${Math.ceil(expire / 86400)}天后解锁。如果误锁，请联系管理员。`
    } else if (expire > 3600) {
      message = `该文章已发布，无法手动解锁，将于${Math.ceil(expire / 3600)}小时后解锁。如果误锁，请联系管理员。`
    } else if (expire > 0) {
      message = `该文章已发布，无法手动解锁，将于${Math.ceil(expire / 60)}分钟后解锁。如果误锁，请联系管理员。`
    } else {
      isLocked.value = false
    }
    ElMessage({
      type: 'warning',
      message: message,
      showClose: true
    })
  } else {
    await unlockArticle(articleDetail.id, store.getters.getToken)
    isLocked.value = !isLocked.value
  }
}

async function handleLock() {
  await lockArticleById(articleDetail.id, store.getters.getToken, 5184000)
  isLocked.value = !isLocked.value
}

const handleRecommendClicked = async () => {
  // recommendManager.recommendDialogVisible = true
  await lockArticleById(articleDetail.id, store.getters.getToken, 7200)
  redirectToArticle('/receivedArticleDetail')
}
const handleAcceptClicked = async () => {
  // acceptManager.acceptDialogVisible = true
  await lockArticleById(articleDetail.id, store.getters.getToken, 7200)
  redirectToArticle('/receivedArticleDetail')
}
const handleNotPostClicked = () => {
  notPostedVisible.value = true
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
            await unlockArticle(articleDetail.id, store.getters.getToken)
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

  if (articleDetail.text_by_id !== '' && articleDetail.text_by_id !== undefined) {
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
const changeArticlePublishStatus = async (publishStatus: string) => {
  await SYNC_POST('/article/changeArticlePublishStatus', {
    articleId: articleDetail.id,
    status: publishStatus,
    token: store.getters.getToken
  }, async (response) => {
    if (response.status !== 200 || response.data.code !== 2001) {
      errorCallback(response)
    }
  })
}
const changeArticleAuditStatus = async (auditStatus: string) => {
  await SYNC_POST('/article/changeArticleStatus', {
    articleId: articleDetail.id,
    status: auditStatus,
    token: store.getters.getToken
  }, async (response) => {
    if (response.status !== 200 || response.data.code !== 2001) {
      errorCallback(response)
    }
  })
}

const handlePOSTEDClicked = async () => {
  await changeArticlePublishStatus('POSTED')
  await lockArticleById(articleDetail.id, store.getters.getToken, 5184000)
  isLocked.value = true
  // await changeArticleAuditStatus('LOCKED')
}
const handleUpdateArticleClicked = () => {
  if (articleDetail.publishStatus === 'POSTED') {
    ElMessage({
      type: 'warning',
      message: '已刊登文章无法修改',
      grouping: true,
      showClose: true
    })
    return
  }
  if (articleDetail.id !== '' && articleDetail.id !== undefined) {
    router.push({ path: '/articleEditor', query: { id: articleDetail.id } })
  } else {
    router.push({ path: '/articleNotFound' })
  }
  // getIsLocked()
}
const handleDeleteClicked = () => {
  if (articleDetail.publishStatus === 'POSTED') {
    ElMessage({
      type: 'warning',
      message: '已刊登文章无法删除',
      grouping: true,
      showClose: true
    })
    return
  }
  delArticleDialogVisible.value = true
}
const handleUnPOSTEDClicked = async () => {
  await changeArticlePublishStatus('PUBLIC')
  notPostedVisible.value = false
}
const handleAccept = async () => {
  let userInfo = store.getters.getUserInfo
  await SYNC_POST('/message/addMessage', {
    token: store.getters.getToken,
    from: userInfo.id,
    to: articleDetail.id,
    message: acceptManager.acceptInfo
  }, async response => {
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '受理完成!',
        type: 'success'
      })
    } else {
      errorCallback(response)
    }
    // 被收录，继续锁12小时，等待作者锁定文章
    await lockArticleById(articleDetail.id, store.getters.getToken, 43200)
    router.back()
  })
}
const handleAcceptArticleClicked = async () => {
  let newPublishStatus = 'POST_RECORD'

  await SYNC_POST('/article/changeArticlePublishStatus', {
    articleId: articleDetail.id,
    status: newPublishStatus,
    token: store.getters.getToken
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      await handleAccept()
    }else {
      errorCallback(response)
    }
  })

}

const handleRecommendArticleClicked = async () => {
  let userInfo = store.getters.getUserInfo
  await SYNC_POST('/expert/recommendArticle', {
    id: userInfo.id,
    identity: userInfo.identity,
    article_id: articleDetail.id,
    recommend_to: recommendManager.recommendTo
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '成功推荐文章',
        type: 'success'
      })
      await unlockArticle(articleDetail.id, store.getters.getToken)
    } else {
      errorCallback(response)
    }
  })
  recommendManager.recommendDialogVisible = false
  router.back()
}
const getHunterByName = async (userName: string) => {
  if (userName == '') {
    return
  }
  recommendManager.loading = true
  await SYNC_GET('/usr/getUserBasicInfoByNameNoPagination', {
    name: userName,
    identity: ['HUNTER']
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      recommendManager.options = response.data.data
    } else {
      errorCallback(response)
    }
    recommendManager.loading = false
  })
}
const visibleInit = () => {
  // 作者看自己文章
  if (articleDetail.text_by_id === store.getters.getUserInfo.id) {
    if (articleDetail.publishStatus === 'POST_RECORD') {
      postButtonVisible.value = true
    }
    if (articleDetail.publishStatus === 'POSTED') {
      publicButtonVisible.value = true
    }
    if (articleDetail.auditStatus === 'AUDITED') {
      onlyMyself.value = false
      publicButtonVisible.value = true
    } else if (articleDetail.auditStatus === 'LOCKED') {
      onlyMyself.value = true
      publicButtonVisible.value = true
    }
  }
  // 其他人看公开文章
  if(articleDetail.pulishStatus === 'PUBLIC') {
    if (store.getters.getUserInfo.identity === 'HUNTER') {
      acceptManager.showAcceptButton = true
    } else if (store.getters.getUserInfo.identity === 'EXPERT') {
      recommendManager.showRecommendButton = true
    }
  }
}
// 有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  if (getCookie('userInfo') !== '' && (JSON.parse(getCookie('userInfo'))?.identity === 'EXPERT' || JSON.parse(getCookie('userInfo'))?.identity === 'HUNTER')) {
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

      visibleInit();
    } else {
      errorCallback(response)
    }
  })
})()
</script>
<style scoped>
.accept-button {
  width: 65px;
  height: 65px;
  position: absolute;
  bottom: 180px;
  right: 30px;
  z-index: 100;
}

.recommend-button {
  width: 65px;
  height: 65px;
  position: absolute;
  bottom: 180px;
  right: 30px;
  z-index: 100;
}

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
  margin: 0 20px 20px;
}

.article-disp-card {
  position: relative;

  transform: scale(1.02);
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
.accept-info-input {
  margin-bottom: 10px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}
.recommend-to-head {
  margin-right: 15px;
}
.accept-button-container {
  display: flex;
  justify-content: flex-end;
}
.accept-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 15px;
}
</style>
