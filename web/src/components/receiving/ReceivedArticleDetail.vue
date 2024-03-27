<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-container>
          <el-main>
            <el-card>
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
<!--                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"-->
<!--                           @click="delArticleDialogVisible=true">删除文章-->
<!--                </el-button>-->
                <el-button type="warning" link :onclick="handleFavorite">{{
                    isFavorited ? '取消收藏' : '收藏'
                  }}
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='small'}" style="font-size: small;">小
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='default'}" style="font-size: medium;">中
                </el-button>
                <el-button link type="primary" :onclick="()=>{displaySize='large'}" style="font-size: large;">大
                </el-button>
              </div>

              <el-collapse style="padding-top: 10px">

                <div class="description-head"><span>文章描述</span></div>
                <el-text class="article-description" :size="displaySize">{{ articleDetail.description }}</el-text>
                <div class="filter-head"><span>文章标签</span></div>
                <SearchFilter ref="SearchFilterRef" @change="searchFilterChange"/>

                <div class="contain-head"><span>文章内容</span></div>
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
        <el-button v-if="identity === 'EXPERT' || identity === 'ADMINISTRATOR'" class="3" type="success" @click="handleRecommendClicked">
          向杂志社推荐
        </el-button>
        <el-button v-if="identity === 'HUNTER' || identity === 'ADMINISTRATOR'" class="3" type="success" @click="handleAcceptClicked">
          受理
        </el-button>
        <el-button class="3" type="danger" @click="handleUnRecommendClicked">
          打回
        </el-button>
      </div>
      <el-dialog
        draggable
        v-model="recommendManager.recommendDialogVisible"
        title="向杂志社推荐"
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
        <div class="accept-head"><span>受录邀请</span></div>
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
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
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
import SearchFilter from '@/components/search/SearchFilter.vue'
import {lockArticleById} from "@/scripts/ArticleLocker";

const router = useRouter()
const route = useRoute()
const store = useStore()
const SearchFilterRef = ref()
const articleDetail = reactive<AttributeAddableObject>({
  id: null,
  text: '',
  time: '',
  text_by: '',
  text_by_id: '',
  title: '',
  description: '',
  status: '',
  attr: '',
  raw: {},
  file_type: ''
})

const displaySize = ref("default")
const identity = ref('')
const isFavorited = ref(false)
const recommendManager = reactive({
  recommendDialogVisible: false,
  recommendTo: '',
  options: [],
  loading: false
})

const acceptManager = reactive({
  acceptDialogVisible: false,
  acceptInfo: '感谢您的投稿，请您将文章上锁并发送到'
})
const searchFilterChange = () => {
  articleDetail.attr = JSON.stringify(SearchFilterRef.value.filterSelection)
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
    const blob = new Blob([response.data], {type: articleDetail.file_type})
    articlePDF.raw = new File([blob], articleDetail.title, {type: articleDetail.file_type})

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
    const blob = new Blob([response.data], {type: articleDetail.file_type})
    articleDetail.raw = new File([blob], articleDetail.title, {type: articleDetail.file_type})

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

async function getIsFavorited() {
  await SYNC_GET('/usr/isArticleFavor', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      if (response.data.isFavor === "True") {
        isFavorited.value = true
      } else {
        isFavorited.value = false
      }
    } else {
      errorCallback(response)
    }
  })
}

async function handleFavorite() {
  if (store.getters.getToken === '') {
    router.push('/login')
    return
  }
  if (isFavorited.value) {
    await SYNC_POST('/usr/cancelFavorite', {
      token: store.getters.getToken,
      article_id: articleDetail.id
    }, async (response) => {
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        ElMessage({
          showClose: true,
          message: '取消收藏成功',
          type: 'info'
        })
        isFavorited.value = false
      } else {
        errorCallback(response)
      }
    })
  } else {
    await SYNC_POST('/usr/addFavorite', {
      token: store.getters.getToken,
      article_id: articleDetail.id
    }, async (response) => {
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        ElMessage({
          showClose: true,
          message: '收藏成功',
          type: 'success'
        })
        isFavorited.value = true
      } else {
        errorCallback(response)
      }
    })
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
const handleRecommendArticleClicked = async () => {
  let userInfo = store.getters.getUserInfo
  await SYNC_POST('/expert/recommendArticle', {
    id: userInfo.id,
    identity: userInfo.identity,
    article_id: articleDetail.id,
    recommend_to: recommendManager.recommendTo
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      ElMessage({
        showClose: true,
        message: '成功推荐文章',
        type: 'success'
      })
    } else {
      errorCallback(response)
    }
  })
  recommendManager.recommendDialogVisible = false
  router.back()
}
const handleUpdateArticleClicked = () => {
  if (articleDetail.id !== '' && articleDetail.id !== undefined) {
    router.push({path: '/articleEditor', query: {id: articleDetail.id}})
  } else {
    router.push({path: '/articleNotFound'})
  }
}

const handleRecommendClicked = () => {
    recommendManager.recommendDialogVisible = true
}
const handleAcceptClicked = () => {
  acceptManager.acceptDialogVisible = true
}
const handleUnRecommendClicked = () => {
}

const handleAcceptArticleClicked = async () => {
  // 被收录，继续锁12小时，等待作者锁定文章
  await lockArticleById(articleDetail.id, store.getters.getUserInfo.id,43200)
}
// 有article_id时初始化ArticleDetail
(async () => {
  let articleRaw: ArrayBuffer
  identity.value = store.getters.getUserInfo.identity
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/getPermissions', {
    article_id: route.query.id,
    requester: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
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
          SearchFilterRef.value.loadSelection(JSON.parse(articleDetail.attr))
          articleDetail.attr = JSON.parse(articleDetail.attr).tags
          await getTextBy()
          await getRaw(articleDetail.id)
          if (store.getters.getToken !== '') {
            await getIsFavorited()
          }
        } else {
          errorCallback(response)
        }
      })
    } else {
      errorCallback(response)
    }
  })

})()
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
  display: flex;
  white-space: pre-wrap;
  text-align: start !important;
  margin-bottom: 20px;
  margin-left: 10px;
  margin-right: 10px;
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
.accept-button-container{
  display: flex;
  justify-content: flex-end;
}
.accept-info-input {
  margin-bottom: 10px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}
</style>
