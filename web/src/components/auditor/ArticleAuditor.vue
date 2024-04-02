<template>
  <el-row>
    <el-col :span="18" :offset="3">

        <el-container>
          <el-main>
            <div class="article-container" v-show="showArticle">
            <el-card class="article-first-card">
              <el-row class="article-box-card">
                <el-text class="article-detail-title">{{ articleDetail.title }}</el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">
                  <el-button v-if="textByIdentity === 'CONTRIBUTOR'" link :onclick="handleAuthorClicked">{{ articleDetail.textBy }}</el-button>
                  <span v-else>{{articleDetail.authorName}}</span>
                  （{{articleDetail.authorGrade}}） {{articleDetail.authorOrganization}}
                  <span v-if="articleDetail.mentor !== ''">指导老师：{{articleDetail.mentor}}</span>
                </el-text>
              </el-row>
              <el-row class="article-box-card">
                <el-text class="article-detail-author">发布时间：{{ articleDetail.time }}</el-text>&nbsp;&nbsp;
              </el-row>
              <div style="display: flex; justify-content:center;align-items: flex-end;margin-bottom: 10px;">

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
                <SearchFilter ref="SearchFilterRef" @change="searchFilterChange" :disabled="true"/>

                <div class="contain-head"><span>文章内容</span></div>
                <!-- 待弃用 -->
                <ArticleDisplayCard class="article-contain-card" :articleRaw="articleDetail.raw"
                                    :lock-before-preview="false"
                                    :article-id="articleDetail.id"></ArticleDisplayCard>
              </el-collapse>
            </el-card>

            </div>
            <div v-if="showArticle">
                <el-card class="reason-card">

                  <div class="reason-head"><span>审核建议</span></div>
                  <el-input
                      class="editor-header"
                      v-model="reason"
                      maxlength="300"
                      show-word-limit
                      :autosize="{ minRows: 2, maxRows: 5}"
                      type="textarea"
                      placeholder="批改意见参考："
                  />

                </el-card>
            </div>
          </el-main>
        </el-container>



      <div v-if="showArticle == false">
        <el-empty description="暂时没有需要审核的文章，感谢您的付出！"/>
      </div>
      <div class="button-container">
        <el-button v-if="showArticle" class="3" type="success" @click="handleAuditClicked(true)">
          受理
        </el-button>
        <el-button v-if="showArticle" class="3" type="danger" @click="handleAuditClicked(false)">
          打回
        </el-button>
        <el-button class="3" @click="handleExit">退出审核</el-button>
      </div>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'

import {useStore} from 'vuex'

import {toUserPage} from '@/scripts/userInfo'
import {errorCallback} from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import SearchFilter from '@/components/search/SearchFilter.vue'
import {View} from "@element-plus/icons-vue";

// 该页面没有锁
const router = useRouter()
const store = useStore()

const articleDetail = reactive<AttributeAddableObject>({
  id: 'null',
  text: '',
  time: '',
  textBy: '',
  text_by_id: '',
  title: '',
  description: '',
  status: '',
  tags: {},
  raw: {},
  fileType: ''
})
const reason = ref('')
const displaySize = ref("default")
const SearchFilterRef = ref()
const isFavorited = ref(false)
const showArticle = ref(false)
const textByIdentity = ref('CONTRIBUTOR')

const searchFilterChange = () => {
  articleDetail.tags = JSON.stringify(SearchFilterRef.value.filterSelection)
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
  if (articleDetail.textBy !== '' && articleDetail.textBy !== undefined) {
    // router.push('/user/'+articleDetail.text_by_id)
    toUserPage(articleDetail.text_by_id)
  } else {
    router.push({path: '/userNotFound'})
  }
}

const handleAuditClicked = async (pass: boolean) => {
  await SYNC_POST('/auditor/audit', {
    id: store.getters.getUserInfo.id,
    identity: store.getters.getUserInfo.identity,
    article_id: articleDetail.id,
    audit_result: pass,
    reason: reason.value
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '感谢您的建议！',
        type: 'success'
      })
      await getAuditedArticle()
    } else {
      errorCallback(response)
    }
  })
}

const handleExit = async () => {
  await SYNC_POST('/auditor/cancelAudit', {
    id: store.getters.getUserInfo.id,
    identity: store.getters.getUserInfo.identity,
    article_id: articleDetail.id,
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '感谢您的贡献！',
        type: 'success'
      })
      router.push('/')
    } else {
      errorCallback(response)
    }
  })
}
// 有article_id时初始化ArticleDetail
const getAuditedArticle = async () => {
  const userInfo = store.getters.getUserInfo
  let param = {
    id: userInfo.id,
    identity: userInfo.identity
  }

  await SYNC_GET('/auditor/getUnauditedArticle', param, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      if (response.data.data.id == 'null') {
        showArticle.value = false
        return
      }
      showArticle.value = true
      for (const dataKey in response.data.data) {
        if (dataKey == 'raw') {
          continue
        }
        articleDetail[dataKey] = response.data.data[dataKey]
      }
      SearchFilterRef.value.loadSelection(articleDetail.tags)
      await getTextBy()
      await getRaw(articleDetail.id)
    } else {
      errorCallback(response)
    }
  })
}

getAuditedArticle()
</script>
<style scoped>
.article-container {
  margin-bottom: 20px;
}

.article-first-card {
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

.contain-head {
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

.reason-card:deep(.el-card__body) {
  padding: 5px 20px;
}

.reason-card {
  margin-bottom: 15px;
}

.reason-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.article-contain-card {
  margin-bottom: 10px;
}
.button-container {
  margin-bottom: 50px;
}
.editor-header {
  margin-bottom: 15px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}
</style>
