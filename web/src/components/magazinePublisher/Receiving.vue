<template>
  <div class="received_table">
    <el-table :data="receivedTableData" stripe align="center" style="width: 100%">
      <el-table-column prop="title" label="文章标题" width="180"/>
      <el-table-column prop="id" label="作者" width="180"/>
      <el-table-column prop="mentor" label="指导老师" width="180"/>
      <el-table-column fixed="right" label="操作" width="120">
        <template #default="scope">
          <el-button
              type="primary"
              size="small"
              @click.prevent="auditArticle(scope.$index, scope.row)"
          >
            进入审阅
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="received_pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
                   layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total">
    </el-pagination>
  </div>
</template>

<script lang="ts" setup>
import {reactive} from 'vue'
import {SYNC_GET} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {errorCallback} from '@/scripts/ErrorCallBack'
import router from "@/router";

const store = useStore();
const receivedTableData = reactive<AttributeAddableObject>([])
const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 监听 page size 改变的事件
function handleSizeChange(newSize: any) {
  pageInfo.pageSize = newSize
  getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
}

// 监听 页码值 改变的事件
function handleCurrentChange(newPage: any) {
  pageInfo.currentPage = newPage
  getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
}

const getReceivedArticle = async (pageNum: Number, pageSize: Number) => {
  let param = {
    auditor_id: store.getters.getUserInfo.id,
    page_num: pageNum,
    page_size: pageSize
  }
  await SYNC_GET('/article/getReceivedArticleById', param, async (response: any) => {
    if (response.status === 200 && response.data.code === 2001) {
      for (const receivedArticle of response.data.data.list) {
        receivedTableData.push(receivedArticle)
      }
      pageInfo.total = response.data.data.total
    } else {
      errorCallback(response)
    }
  })
}

function gotoDetail(articleId: any) {
  if (articleId !== '' && articleId !== undefined) {
    router.push({path: '/receivedArticleDetail', query: {id: articleId}})
  } else {
    router.push({path: '/articleNotFound'})
  }
}

const auditArticle = (index: number, row: AttributeAddableObject) => {
  gotoDetail(row.id)
}

getReceivedArticle(pageInfo.currentPage, pageInfo.pageSize)
</script>


<style>
.received_table {
  text-align: center;
}

.received_pagination {
  margin: 20px 0 10px 0;
  display: flex;
  justify-content: center;
}
</style>
