<template>
  <el-row>
    <el-col :lg="18" :md="20" :sm="24" style="margin: auto;">
      <div>
        <el-container>
          <el-main>
            <el-card>
              <el-row class="article-box-card">
                <el-text class="article-detail-title">{{ articleDetail.title }}</el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">
                  <el-button v-if="textByIdentity === 'CONTRIBUTOR'" link :onclick="handleAuthorClicked">{{ articleDetail.textBy }}</el-button>
                  <span v-else>{{articleDetail.authorName}}</span>
                  {{articleDetail.authorGrade?"（"+articleDetail.authorGrade+"）":""}} {{articleDetail.authorOrganization}}
                  <span v-if="articleDetail.mentor !== ''">指导老师：
                    <el-button v-if="textByIdentity !== 'CONTRIBUTOR'" link :onclick="handleAuthorClicked">{{articleDetail.mentor}}</el-button>
                    <span v-else>{{articleDetail.mentor}}</span>
                  </span>
                </el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">发布时间：{{ articleDetail.time }}</el-text>
              </el-row>

                <el-button link type="primary" :onclick="handleDiscriptionSmall" style="font-size: 16px;">小
                </el-button>
                <el-button link type="primary" :onclick="handleDiscriptionMedium" style="font-size: 18px;">中
                </el-button>
                <el-button link type="primary" :onclick="handleDiscriptionLarge" style="font-size: 20px;">大
                </el-button>


              <el-collapse style="padding-top: 10px">

                <div class="description-head"><span>文章描述</span></div>
                <el-text class="article-description" :size="displaySize">{{ articleDetail.description }}</el-text>
                <div class="filter-head"><span>文章标签</span></div>
                <SearchFilter style="display:none;" ref="SearchFilterRef" @change="searchFilterChange"/>
                <ArticleTags v-if="articleTagsVisible" ref="articleTags" :tagsJsons="articleDetail.tags"></ArticleTags>
                <span v-else>无</span>
                <div class="contain-head"><span>文章内容</span> <el-button link v-if="isDetailCssChange" type="primary"
                    :onclick="handleDetailRemoveCSS" >恢复默认字体大小
                  </el-button></div>
                <!-- 待弃用 -->
                <ArticleDisplayCard class="article-contain-card" :articleRaw="articleDetail.raw"
                                    :lock-before-preview="false"
                                    :article-id="articleDetail.id"></ArticleDisplayCard>
              </el-collapse>
              <!--              <ArticleDisplayCard :articleRaw="articleDetail.raw" :lock-before-preview="true" :article-id="articleDetail.id"></ArticleDisplayCard>-->
              <!--              <el-text class="article-description" :size="displaySize">{{ articleDetail.description }}</el-text>-->
            </el-card>
          </el-main>

        </el-container>
      </div>
      <div class="button-container">
        <el-button v-if="identity === 'EXPERT' || identity === 'ADMINISTRATOR'" class="3" type="success"
                   @click="handleRecommendClicked">
          向报社推荐
        </el-button>
        <el-button v-if="identity === 'HUNTER' || identity === 'ADMINISTRATOR'" class="3" type="success"
                   @click="handleAcceptClicked">
          受理
        </el-button>
        <el-button class="3" type="warning" @click="handleRejectClicked">
          打回
        </el-button>
      </div>
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
                  placeholder="例如：感谢您的投稿，请您将文章上锁并发送到..."></el-input>
        <span class="accept-button-container">
        <el-button type="primary" @click="handleAcceptArticleClicked">
          收录
        </el-button>
        <el-button @click="acceptManager.acceptDialogVisible = false">取消</el-button>
        </span>
      </el-dialog>
      <el-dialog
          draggable
          v-model="rejectManager.rejectDialogVisible"
          title="打回文章"
          width="30%"
      >
        <div class="accept-head"><span>打回理由</span></div>
        <el-input v-model="rejectManager.rejectInfo"
                  maxlength="200"
                  show-word-limit
                  class="accept-info-input"
                  :autosize="{ minRows: 4, maxRows: 10}"
                  type="textarea"
                  placeholder="例如：感谢您的投稿，但很遗憾..."></el-input>
        <span class="accept-button-container">
        <el-button type="primary" @click="handleRejectArticleClicked">
          打回
        </el-button>
        <el-button @click="rejectManager.rejectDialogVisible = false">取消</el-button>
        </span>
      </el-dialog>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {getCurrentInstance, reactive, ref, onMounted} from 'vue'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {toUserPage} from '@/scripts/userInfo'
import {errorCallback} from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import SearchFilter from '@/components/search/SearchFilter.vue'
import {lockArticleById, unlockArticle} from '@/scripts/ArticleLocker'
import {View} from "@element-plus/icons-vue";
import ArticleTags from '@/components/common/ArticleTags.vue';

const router = useRouter()
const route = useRoute()
const store = useStore()
const SearchFilterRef = ref()
const articleTagsVisible = ref(false)
const textByIdentity = ref('CONTRIBUTOR')
const articleDetail = reactive<AttributeAddableObject>({
  id: null,
  text: '',
  time: '',
  textBy: '',
  text_by_id: '',
  title: '',
  description: '',
  status: '',
  tags: '{}',
  raw: {},
  fileType: ''
})
const displaySize = ref("default")
const identity = ref('')
const recommendManager = reactive({
  recommendDialogVisible: false,
  recommendTo: '',
  options: [],
  loading: false
})

const acceptManager = reactive({
  acceptDialogVisible: false,
  acceptInfo: '感谢您的投稿，请您将文章上锁并发送到',

})
const rejectManager = reactive({
  rejectDialogVisible: false,
  rejectInfo: '感谢您的投稿，但很遗憾，'
})

const articleTags = ref()

const searchFilterChange = () => {
  articleDetail.tags = SearchFilterRef.value.filterSelection
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


console.log(articleDetail)

const articlePDF = reactive({
  raw: {}
})

async function getPDF(articleId: String) {
  axios({
    url: '/article/word2pdf',
    method: 'GET',
    headers: {'Content-Type': 'multipart/form-data'},
    params: {article_id: articleId},
    responseType: 'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data], {type: articleDetail.fileType})
    articlePDF.raw = new File([blob], articleDetail.title, {type: articleDetail.fileType})

  }).catch(error => {
    console.error(error);
  });
}

async function getRaw(articleId: String) {
  axios({
    url: '/article/getArticleFileById',
    method: 'GET',
    headers: {'Content-Type': 'multipart/form-data'},
    params: {article_id: articleId},
    responseType: 'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data], {type: articleDetail.fileType})
    articleDetail.raw = new File([blob], articleDetail.title, {type: articleDetail.fileType})

  }).catch(error => {
    console.error(error);
  });
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

const isDetailCssChange = ref(false)

function handleDiscriptionSmall() {
  $('.article-description').css('font-size', '16px')
  $('#docxContainer .docx p span').css('font-size', '16px')
  isDetailCssChange.value = true
}

function handleDiscriptionMedium() {
  $('.article-description').css('font-size', '18px')
  $('#docxContainer .docx p span').css('font-size', '18px')
  isDetailCssChange.value = true
}

function handleDiscriptionLarge() {
  $('.article-description').css('font-size', '20px')
  $('#docxContainer .docx p span').css('font-size', '20px')
  isDetailCssChange.value = true
}

function handleDetailRemoveCSS() {
  $('#docxContainer .docx p span').css('font-size', '')
  isDetailCssChange.value = false
}


const handleAuthorClicked = () => {
  if (articleDetail.textBy !== '' && articleDetail.textBy !== undefined) {
    // router.push('/user/'+articleDetail.text_by_id)
    toUserPage(articleDetail.text_by_id)
  } else {
    router.push({path: '/userNotFound'})
  }
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
const addMessage = async () => {
  let userInfo = store.getters.getUserInfo
  await SYNC_POST('/message/addMessage', {
    from: userInfo.id,
    to: articleDetail.id,
    message: rejectManager.rejectInfo,
    token: store.getters.getToken
  }, async response => {
    if (response.status === 200 && response.data.code === 2001) {
      await (async () => {
        ElMessage({
          showClose: true,
          message: '感谢您的反馈',
          type: 'success'
        })
      })()
    } else {
      errorCallback(response)
    }
    rejectManager.rejectDialogVisible = false
  })
}

const handleRejectArticleClicked = async () => {
  let newPublishStatus = articleDetail.publishStatus === 'UNDER_REVIEW'? 'FAILED_REVIEW' : 'FAILED_RECORD'
  await SYNC_POST('/article/changeArticlePublishStatus', {
    articleId: articleDetail.id,
    status: newPublishStatus,
    token: store.getters.getToken
  }, async (response) => {

    if (response.status !== 200 || response.data.code !== 2001) {
      errorCallback(response)
    } else {
      await SYNC_POST('/article/changeArticleReceivedBy', {
        articleId: articleDetail.id,
        receivedBy: '',
        token: store.getters.getToken
      }, async (response: any) => {
        if (response.status !== 200 || response.data.code !== 2001) {
          errorCallback(response)
        } else {
          await addMessage()
        }
      })
      await unlockArticle(articleDetail.id, store.getters.getToken)
    }

  })
}
const handleAccept = async () => {
  let userInfo = store.getters.getUserInfo
  await SYNC_POST('/message/addMessage', {
    from: userInfo.id,
    to: articleDetail.id,
    message: acceptManager.acceptInfo,
    token: store.getters.getToken
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

const handleRecommendClicked = () => {
  recommendManager.recommendDialogVisible = true
}
const handleAcceptClicked = () => {
  acceptManager.acceptDialogVisible = true
}
const handleRejectClicked = () => {
  rejectManager.rejectDialogVisible = true
}

const handleUpdateArticleClicked = () => {
  if (articleDetail.id !== '' && articleDetail.id !== undefined) {
    router.push({path: '/articleEditor', query: {id: articleDetail.id}})
  } else {
    router.push({path: '/articleNotFound'})
  }
}
// 有article_id时初始化ArticleDetail
onMounted(async () => {
  let articleRaw: ArrayBuffer
  identity.value = store.getters.getUserInfo.identity
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/getPermissions', {
    articleId: route.query.id,
    requester: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      await SYNC_GET('/article/articleDetail', {
        article_id: route.query.id
      }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
          for (const dataKey in response.data.data.article) {
            articleDetail[dataKey] = response.data.data.article[dataKey]
          }
          SearchFilterRef.value.loadSelection(articleDetail.tags)
          if(JSON.stringify(articleDetail.tags).length > 0){
              articleTagsVisible.value = true
          }
          await getTextBy()
          await getRaw(articleDetail.id)

        } else {
          errorCallback(response)
        }
      })
    } else {
      errorCallback(response)
    }
  })

})


</script>
<style scoped>
.article-box-card {
  display: flex;
  justify-content: center;
}

.article-detail-title {
  font-size: 20px;
  font-weight: bold;
}

.article-detail-author {
  font-size: 14px;
}

.e {
  border-right: 1px solid var(--el-border-color);
  margin-right: 10px;
  margin-left: 10px;
}

.blank {
  margin-right: 10px;
}

.gradePanel {
  margin: 20px 20px;
}

.gradeDis {
  text-align: center;
}

.article-description {
  font-size: 16px;
  display: flex;
  white-space: pre-wrap;
  text-align: start !important;
  margin: 0 20px 20px;
}

.description-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.filter-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.contain-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.button-container {
  margin-bottom: 50px;
}

.recommend-to-head {
  margin-right: 15px;
}

.accept-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 15px;
}

.accept-button-container {
  display: flex;
  justify-content: flex-end;
}

.accept-info-input {
  margin-bottom: 10px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}
.contribute-to-selection {
}
</style>
