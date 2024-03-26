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
                <el-text class="article-detail-author">（
                  <el-button link :onclick="handleAuthorClicked">{{ articleDetail.text_by }}</el-button>
                  ） {{ articleDetail.time }}
                </el-text>
              </el-row>
              <div style="display: flex; justify-content:center;align-items: flex-end;margin-bottom: 10px;">
                <el-button type="primary" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                           @click="handleUpdateArticleClicked">修改文章
                </el-button>
                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                           @click="delArticleDialogVisible=true">删除文章
                </el-button>
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
              <!--              <el-divider/>-->
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
          审核通过！
        </el-button>
        <el-button v-if="showArticle" class="3" type="danger" @click="handleAuditClicked(false)">
          审核不通过
        </el-button>
        <el-button class="3" @click="handleExit">退出审核</el-button>
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
import {reactive, ref} from 'vue'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {useRoute, useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'

import {useStore} from 'vuex'

import {toUserPage} from '@/scripts/userInfo'
import {errorCallback} from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import SearchFilter from '@/components/search/SearchFilter.vue'

// 该页面没有锁
const router = useRouter()
const store = useStore()

const articleDetail = reactive<AttributeAddableObject>({
  id: 'null',
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
const reason = ref('')
const displaySize = ref("default")
const SearchFilterRef = ref()
const isFavorited = ref(false)
const showArticle = ref(false)
const delArticleDialogVisible = ref(false)


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

// 有article_id时初始化ArticleDetail
const getAuditedArticle = async () => {
  const userInfo = store.getters.getUserInfo
  let param = {
    id: userInfo.id,
    identity: userInfo.identity
  }

  await SYNC_GET('/auditor/getUnauditedArticle', param, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      console.log(response)
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
      SearchFilterRef.value.loadSelection(JSON.parse(articleDetail.attr))
      articleDetail.attr = JSON.parse(articleDetail.attr).tags
      await getTextBy()
      await getRaw(articleDetail.id)
    } else {
      errorCallback(response)
    }
  })
}
getAuditedArticle()

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
const handleDelArticleClicked = async () => {
  await SYNC_POST('/contributor/delArticle', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
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
