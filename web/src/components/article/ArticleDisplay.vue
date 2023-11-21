<template>
  <el-row>
    <el-col :span="18" :offset="3">

      <div>
        <el-card class="box-card">
          <el-empty v-if="articleList.artList" description="暂无结果" />
          <el-row v-for="(articleInfo,index) in articleList.artList"
                  :key="index"
                  :span="8"
                  :gutter="24"
                  :offset="index > 0 ? 2 : 0">
            <el-col :span="24">
              <el-card class="box-card">
                <div>
                  {{ articleInfo['text_by'] }} - {{ articleInfo.time }}
                  <h2>{{ articleInfo.title }}</h2>
                  {{ articleInfo.description }}
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
            :total="pageInfo.total">
          </el-pagination>
        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { SYNC_GET } from '@/scripts/Axios'
import { useStore } from 'vuex'
import { isRaw } from '@vue/composition-api'

const store = useStore()
const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}
const articleList = reactive({
  artList: []
})
getArticleList()

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
}

async function getArticleList () {
  await (SYNC_GET('/article/search', {
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
    keyword: store.getters.getSearchKey
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      await getTextBy(response.data.articles.list)
      pageInfo.total = response.data.articles.total
    } else {
      console.log(response)
    }
  }))
}
</script>
