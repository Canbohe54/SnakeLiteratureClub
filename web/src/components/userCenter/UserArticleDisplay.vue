<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-card class="box-card result-list-card">
          <el-empty v-if="articleList.artList.length === 0" description="暂无结果" />
          <el-row gutter="12">
            <el-col v-for="(articleInfo, index) in articleList.artList" :key="index" :span="12">
              <el-card class="box-card result-card-body" @click='gotoDetail(articleInfo.id, articleInfo.status)'
                shadow="hover">
                <div>
                  {{ articleInfo.time }}
                  <h2>{{ articleInfo.title }} {{ articleInfo.status === 1 ? "*（草稿）" : "" }}</h2>
                  <div style="min-height: 40px;">
                    {{ articleInfo.description.length > 20 ? articleInfo.description.slice(0, 20) + '...' :
                      articleInfo.description }}
                  </div>
                </div>

                <div style="margin: 10px 0 0 0;">
                  <el-text v-if="avgGradeMap.get(articleInfo.id) != ''" class="result-rate-text">评分：{{
                    avgGradeMap.get(articleInfo.id) }}/15</el-text>
                  <el-text v-else class="result-rate-text">暂无评分</el-text>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total" class="search-result-pageination">
          </el-pagination>
        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import ArticleInfoCard from "@/components/article/ArticleInfoCard.vue";
import { reactive, watch } from 'vue'
import { SYNC_GET, SYNC_POST } from '@/scripts/Axios'
import { useRoute } from 'vue-router'
import router from '@/router'
import { AttributeAddableObject } from "@/scripts/ArticleTagFilter";
import { ref } from 'vue'
import { useStore } from "vuex";

// const props = defineProps(['queryRoute', 'queryParams'])

const avgGradeMap = new Map()
const route = useRoute()
const store = useStore()

// 不知道为什么不能监听searchWord
// const searchWord = ref(route.query.wd)
const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 0
}
const articleList = reactive({
  artList: [],
  originalArticleList: []
})
const articleStatus = ref([])
getArticleList()

watch(() => route.query.wd, () => {
  getArticleList()
})
// 监听 page size 改变的事件
function handleSizeChange(newSize: any) {
  pageInfo.pageSize = newSize
  getArticleList()
}

// 监听 页码值 改变的事件
function handleCurrentChange(newPage: any) {
  pageInfo.currentPage = newPage
  getArticleList()
}

async function getTextBy(artList: any) {
  await Promise.all(
    artList.map(async (item: any) => {
      await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: item.text_by
      }, response => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          item.text_by = response.data.user_info.name
        } else {
          console.log(response)
        }
      })
    })
  )
  artList.sort((a: any, b: any) => {
    return a.status - b.status
  })
  articleList.artList = artList
  articleList.originalArticleList = artList
}

async function getArticleList() {
  if (route.params.id === store.getters.getUserInfo.id) {
    articleStatus.value = [1, 2, 3]
  } else {
    articleStatus.value = [3]
  }
  let params: AttributeAddableObject = {
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
    contributor_id: route.params.id,
    status_list: articleStatus.value
  }
  await (SYNC_GET('/contributor/contributorArticles', params, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      await getAvgGrade(response.data.articles.list)
      pageInfo.total = response.data.articles.total
      await getTextBy(response.data.articles.list)
    } else {
      console.log(response)
    }
  }))
}
function gotoDetail(articleId: any, articleStatus: any) {
  if (articleId !== '' && articleId !== undefined) {
    if (articleStatus === 1) {
      router.push({ path: '/articleEditor', query: { id: articleId } })
    } else {
      router.push({ path: '/articleDetail', query: { id: articleId } })
    }
  } else {
    router.push({ path: '/articleNotFound' })
  }
}

async function getAvgGrade(artList: any) {
  await Promise.all(
    artList.map(async (item: any) => {
      await SYNC_POST('/grade/getAvgGrade', {
        article_id: item.id
      }, response => {
        if (response.status === 200 && response.data.statusMsg === 'success') {
          if(Math.round(response.data.avg_grade) == response.data.avg_grade){
            avgGradeMap.set(response.data.article_id, response.data.avg_grade.toFixed(0))
          }else{
            avgGradeMap.set(response.data.article_id, response.data.avg_grade.toFixed(2))
          }
        } else {
          console.log(response)
        }
      })
    })
  )
}

defineExpose({ articleList })
</script>
<style scoped>
.result-list-card :deep(.el-card__body) {
  width: -webkit-fill-available;
}

.result-list-card {
  border-radius: 10px;
}

.result-card-body {
  height: 200px;
}

.result-rate-text {
  font-size: 16px;
  font-weight: bold;
  color: #ffd500;
}

.search-result-pageination {
  margin: 20px 0 10px 0;
  display: flex;
  justify-content: center;
}

.result-single-card {
  border-radius: 10px;
  box-shadow: none;
  margin-bottom: 10px;
}
</style>
