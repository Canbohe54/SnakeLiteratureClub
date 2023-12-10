<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-card class="box-card result-list-card">
          <el-empty v-if="articleList.artList.length === 0" description="暂无结果" />
          <el-row>
            <el-col v-for="(articleInfo,index) in articleList.artList"
                    :key="index"
                    :span="12">
              <el-card class="box-card" @click='gotoDetail(articleInfo.id)' >
                <div>
                  {{ articleInfo['text_by'] }} - {{ articleInfo.time }}
                  <h2>{{ articleInfo.title }}</h2>
                  {{ articleInfo.description }}
                </div>

                <div>
                    <el-button type="primary" round v-if = "avgGradeMap.get(articleInfo.id) != '' ">{{avgGradeMap.get(articleInfo.id)}}/15</el-button>
                    <el-button type="primary" round v-else>暂无评分</el-button>
                </div>

              </el-card>
            </el-col>
          </el-row>

          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageInfo.currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pageInfo.total"
            class="search-result-pageination">
          </el-pagination>
        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import { useStore } from 'vuex'
import {useRoute} from "vue-router";
import router from "@/router";

let avgGrade = ''
// let avgGradeDic : {[key:string]:number} = {};
let index2 = 0
let avgGradeArray:number[] = new Array(9999)
let  avgGradeMap = new Map()
const store = useStore()
const route = useRoute()

// 不知道为什么不能监听searchWord
// const searchWord = ref(route.query.wd)
const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}
const articleList = reactive({
  artList: [],
  originalArticleList: [],
})
const articleStatus = [3]
getArticleList()

watch(() => route.query.wd, () => {
  getArticleList()
})
// 监听 page size 改变的事件
function handleSizeChange (newSize: any) {
  pageInfo.pageSize = newSize
  getArticleList()
}

// 监听 页码值 改变的事件
function handleCurrentChange (newPage: any) {
  pageInfo.currentPage = newPage
  getArticleList()
}

async function getTextBy (artList: any) {
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

  articleList.artList = artList
  articleList.originalArticleList = artList
}

async function getArticleList () {
  await (SYNC_GET('/article/search', {
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
    keyword: route.query.wd,
    status_list: articleStatus,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log(response)
      await getAvgGrade(response.data.articles.list)
      pageInfo.total = response.data.articles.total
      await getTextBy(response.data.articles.list)
    } else {
      console.log(response)
    }
  }))
}
function gotoDetail (articleId : any) {
  if (articleId !== '' && articleId !== undefined) {
    router.push({ path: '/articleDetail', query: { article_id: articleId } })
  } else {
    router.push({ path: '/articleNotFound' })
  }
}

async function getAvgGrade (artList: any) {
  index2 = 0
  await Promise.all(
    artList.map(async (item: any) => {
      await SYNC_POST('/grade/getAvgGrade', {
        article_id: item.id
      }, response => {
        console.log(response)
        if (response.status === 200 && response.data.statusMsg === 'success') {
          avgGradeMap.set(response.data.article_id,response.data.avg_grade)
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
.result-list-card {
  border-radius: 10px;
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
