<template>
  <div>
    <el-container>
      <el-main>
        <el-card>
          <el-row class="article-box-card"><el-text class="article-detail-title">{{articleDetail.title}}</el-text></el-row>
          <el-row class="article-box-card"><el-text class="article-detail-author">（<el-button link :onclick="handleAuthorClicked">{{articleDetail.text_by}}</el-button>） {{articleDetail.time}}</el-text></el-row>
          <div style="display: flex; justify-content:center;align-items: flex-end;">
            <el-button type="primary" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id" @click="handleUpdateArticleClicked">修改文章</el-button>
            <el-button type="danger" link v-if="articleDetail.text_by_id === store.getters.getUserInfo.id" @click="delArticleDialogVisible=true">删除文章</el-button>
            <el-button type="warning" link circle :onclick="handleFavorite">{{ isFavorited?'取消收藏':'收藏' }}</el-button>
            <el-button link type="primary" :onclick="()=>{displaySize='small'}" style="font-size: small;">小</el-button>
            <el-button link type="primary" :onclick="()=>{displaySize='default'}" style="font-size: medium;">中</el-button>
            <el-button link type="primary" :onclick="()=>{displaySize='large'}" style="font-size: large;">大</el-button>
          </div>

          <el-divider />
          <el-text :size="displaySize">{{articleDetail.text}}</el-text>
        </el-card>
      </el-main>

      <div class="gradeEdit" v-if="store.getters.getUserInfo.identity=='专家'">
        <GradeEditor class="graedit"/>
      </div>

      <div class="gradeDis">
        <GradeDisplay class="graDis"/>
      </div>

      <el-footer>
        <suspense>
          <CommentDisplay :articleId="route.query.id" />
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
</template>
<script lang="ts" setup>
import {reactive, ref} from "vue";
import {AttributeAddableObject} from "@/scripts/ArticleTagFilter";
import {useRoute, useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {SYNC_GET, SYNC_POST} from "@/scripts/Axios";
import {Star} from "@element-plus/icons-vue";
import GradeEditor from "@/components/grade/GradeEditor.vue";
import GradeDisplay from "@/components/grade/GradeDisplay.vue";
import {useStore} from "vuex";
import CommentDisplay from "@/components/article/CommentDisplay.vue";

const router = useRouter()
const route = useRoute()
const store = useStore()

const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  text_by: '',
  text_by_id: '',
  title: '',
  description: '',
  status: '',
  attr: ''
})

const displaySize = ref("default")

const isFavorited = ref(false)

const delArticleDialogVisible = ref(false)
function errorCallback(response: any) {
  console.log(response)
  if (response.status === 200) {
    ElMessage({
      showClose: true,
      message: response.data.statusMsg,
      type: 'error'
    })
  } else {
    ElMessage({
      showClose: true,
      message: 'Network Error!',
      type: 'error'
    })
  }
}
// 有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      for (const dataKey in response.data.article) {
        articleDetail[dataKey] = response.data.article[dataKey]
      }
      await getTextBy()
      await getIsFavorited()
    } else {
      errorCallback(response)
    }
  })
})()
async function getTextBy () {
      articleDetail.text_by_id = articleDetail.text_by
      await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: articleDetail.text_by
      }, async (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          articleDetail.text_by = response.data.user_info.name
        } else {
          console.log(response)
        }
      })
}

async function getIsFavorited() {
  await SYNC_GET('/usr/isArticleFavor', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
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
  if (isFavorited.value) {
    await SYNC_POST('/usr/cancelFavorite', {
      token: store.getters.getToken,
      article_id: articleDetail.id
    }, async (response) => {
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        ElMessage({
          showClose: true,
          message: '取消收藏成功',
          type: 'success'
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
    router.push('/user/'+articleDetail.text_by_id)
  } else {
    router.push({path: '/userNotFound'})
  }
}
const handleDelArticleClicked =async () => {
  await SYNC_POST('/contributor/delArticle', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      ElMessage({
        showClose: true,
        message: '删除文章成功',
        type: 'success'
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
}
</script>
<style>
.article-box-card{
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

.e{
  border-right: 1px solid var(--el-border-color);
  margin-right: 10px;
  margin-left: 10px;
}

.blank{
  margin-right: 10px;
}

.gradeDis{
  text-align:center;
  margin: 20px 20px;
}
</style>
