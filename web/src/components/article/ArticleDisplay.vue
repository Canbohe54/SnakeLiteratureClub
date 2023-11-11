<template>

  <div>
    <el-card class="box-card">
      <el-row v-for="(articleInfo,index) in articleList.artList"
              :key="index"
              :span="8"
              :gutter="24"
              :offset="index > 0 ? 2 : 0">
        <el-col :span="24">
          <el-card class="box-card">
            <div>
              <h2>{{ articleInfo.title }}</h2>
              {{ articleInfo.time }}
              {{ articleInfo['text_by'] }}
              {{ articleInfo.description }}
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageInfo.currentPage"
        :page-sizes="[1, 20, 30, 40]"
        :page-size="pageInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageInfo.total">
      </el-pagination>
    </el-card>
  </div>
</template>
<script lang="ts" setup>
import {reactive} from "vue";
import {POST} from "@/scripts/Axios";

const pageInfo = {
  currentPage: 1,
  pageSize: 1,
  total: 10,
}
let articleList = reactive({
  artList: []
})
getArticleList()

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

function getArticleList() {
  POST("/article/allArticles", {page_num: pageInfo.currentPage, page_size: pageInfo.pageSize}, (response) => {
    if ((response.status === 200 || response.status === 0) && response.data.statusMsg === 'Success.') {
      articleList.artList = response.data.articles.list;
      pageInfo.total = response.data.articles.total;
    } else {
      console.log(response);
    }
  })

}
</script>
