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
                <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id"
                           @click="delArticleDialogVisible=true">删除文章
                </el-button>
<!--                <el-button type="warning" link :onclick="handleFavorite">{{-->
<!--                    isFavorited ? '取消收藏' : '收藏'-->
<!--                  }}-->
<!--                </el-button>-->
                <el-button type="warning" link :onclick="handleLock">{{
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
            </el-card>
          </el-main>
          <el-card class="gradePanel">
            <GradeDisplay class="graDis"/>
            <div class="gradeEdit" v-if="store.getters.getUserInfo.identity=='专家'">
              <GradeEditor class="graedit"/>
            </div>
          </el-card>
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
import {reactive, ref} from 'vue'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import GradeEditor from '@/components/grade/GradeEditor.vue'
import GradeDisplay from '@/components/grade/GradeDisplay.vue'
import {useStore} from 'vuex'
import CommentDisplay from '@/components/article/CommentDisplay.vue'
import {toUserPage} from '@/scripts/userInfo'
import {errorCallback} from '@/scripts/ErrorCallBack'
import axios from 'axios'
import ArticleDisplayCard from '@/components/article/ArticleDisplayCard.vue'
import {lockArticleById, unlockArticle} from '@/scripts/ArticleLocker'

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
  attr: '',
  raw: {},
  file_type: ''
})

const displaySize = ref("default")

const isLocked = ref(false)

const delArticleDialogVisible = ref(false)

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


async function getRaw(articleId: String) {
  axios({
    url: '/article/getArticleFileById',
    method:'GET',
    headers: { 'Content-Type': 'multipart/form-data' },
    params: { article_id: articleId },
    responseType:'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data],{type:articleDetail.file_type})
    articleDetail.raw = new File([blob], articleDetail.title, {type:articleDetail.file_type})

  }).catch(error => {
    console.error(error);
  });
}

async function getIsLocked () {
  await SYNC_GET('/article/checkLocked',{
    articleId: articleDetail.id
  },async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      console.log(response)
      isLocked.value = response.data
    } else {
      errorCallback(response)
    }
  })
}
async function handleLock() {
  // TODO： 锁多久 解锁的业务逻辑
  if(isLocked){
    await unlockArticle(articleDetail.id)
  }else {
    await lockArticleById(articleDetail.id, store.getters.getUserInfo.id, 3600)
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
}

// 有article_id时初始化ArticleDetail
(async () => {
  let articleRaw: ArrayBuffer
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      console.log(response.data)
      for (const dataKey in response.data.data.article) {
        if (dataKey == 'raw') {
          continue
        }
        articleDetail[dataKey] = response.data.data.article[dataKey]
      }
      await getIsLocked()
      await getTextBy()
      await getRaw(articleDetail.id)

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
}
</style>
