<template>
  <div>
    <div class="received_block">
      <el-table class="received_table" :data="receivedTableData" :header-cell-style="{'text-align': 'center'}" stripe header-align="center" style="width: 100%">
        <template #empty>
          <el-empty description="暂时没有收到稿件哦" />
        </template>
        <el-table-column prop="title" label="文章标题" width="180" align="center"/>
        <el-table-column prop="description" label="文章描述" width="180" align="center"/>
        <el-table-column prop="text_by" label="作者" width="180" align="center"/>
        <el-table-column prop="mentor" label="指导老师" width="180" align="center"/>
        <el-table-column fixed="right" label="操作" width="120" align="center">
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
    </div>
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
const overflowToolTipOptions = {
  effect: 'light',
  visible: 'false'

}

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
async function getUserName(userId: string) {
  return new Promise(function (resolve, reject) {
    SYNC_GET('/usr/getUserBasicInfo', {
      user_id: userId
    }, async (response) => {
      if (response.status === 200 && response.data.code === 2001) {
        resolve(response.data.data.user_info.name)
      } else {
        console.log(response)
      }
    })
  })
}
const processReceivedArticle = (receivedArticle: AttributeAddableObject) => {
  receivedArticle.text_by_id = receivedArticle.text_by
  getUserName(receivedArticle.text_by_id).then((result) => {
    receivedArticle.text_by = result
  })
  if (receivedArticle.mentor !== '') {
    getUserName(receivedArticle.mentor).then((result) => {
      receivedArticle.mentor = result
    })
  }
  for(const k of Object.keys(receivedArticle)){
    if(receivedArticle[k] === ''){
      receivedArticle[k] = '--.--'
    }
  }
  return receivedArticle
}
const getReceivedArticle = async (pageNum: Number, pageSize: Number) => {
  let param = {
    auditor_id: store.getters.getUserInfo.id,
    page_num: pageNum,
    page_size: pageSize
  }
  await SYNC_GET('/article/getReceivedArticleById', param, async (response: any) => {
    if (response.status === 200 && response.data.code === 2001) {
      for (let receivedArticle of response.data.data.list) {
        processReceivedArticle(receivedArticle)
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


<style scoped>
.received_block :deep( .el-table__header-wrapper) {
  display: flex;
  justify-content: center;
}

.received_table {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.received_pagination {
  margin: 20px 0 10px 0;
  display: flex;
  justify-content: center;
}

:deep( .el-table .cell) {
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}


</style>
