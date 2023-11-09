<template>
  <div>
    <!--    <el-card class="box-card">-->
    <!--      <div v-for="articleInfo in articleList" class="articleBasic">-->
    <!--        <h3>-->
    <!--          {{ articleInfo.title }}-->
    <!--        </h3>-->
    <!--      </div>-->
    <!--    </el-card>-->
    <!--    <el-card>-->
    <!--      <el-table :data="articleList" border stripe>-->
    <!--        &lt;!&ndash;        id, time, text_by, title, description, status, attr&ndash;&gt;-->
    <!--        <el-table-column label="id" prop="id"></el-table-column>-->
    <!--        <el-table-column label="text_by" prop="text_by"></el-table-column>-->
    <!--        <el-table-column label="description" prop="description"></el-table-column>-->
    <!--        <el-table-column label="time" prop="time"></el-table-column>-->

    <!--      </el-table>-->
    <div>
      <div v-for="articleInfo in articleList" :key="articleInfo.time">
        <h2>{{ articleInfo.title }}</h2>
        {{ articleInfo.time }}
        {{ articleInfo.text_by }}
        {{ articleInfo.description }}
      </div>
    </div>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageInfo.currentPage"
      :page-sizes="[1, 20, 30, 40]"
      :page-size="pageInfo.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pageInfo.total">
    </el-pagination>
    <!--    </el-card>-->
  </div>
</template>
<script lang="ts" setup>
import {reactive} from "vue";
import {POST} from "@/scripts/Axios";


const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 10,
});
let articleList = []
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
      articleList = response.data.articles.list

      console.log(pageInfo.pageSize)

      pageInfo.total = response.data.articles.total

    } else {
      console.log(response)
    }
  })

}
</script>
