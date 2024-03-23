<template>
  <div class="received_table">
    <el-table :data="receivedTableData" stripe align="center" style="width: 100%">
        <el-table-column prop="title" label="文章标题" width="180" />
        <el-table-column prop="id" label="作者" width="180" />
        <el-table-column prop="mentor" label="指导老师" width="180" />
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

const getReceivedArticle = async (pageNum: Number, pageSize: Number) => {
    let param = {
        auditor_id: 114514,
        // auditor_id: store.getters.getUserInfo().id,
        page_num: pageNum,
        page_size: pageSize
    }
    SYNC_GET('/article/getReceivedArticleById', param, async (response: any) => {
        if (response.status === 200 && response.data.message === 'Success.'){
            for (const receivedArticle of response.data.data.list) {
                receivedTableData.push(receivedArticle)
            }
        } else {
            errorCallback(response)
        }
    })
}
function gotoDetail(articleId: any) {
  if (articleId !== '' && articleId !== undefined) {
    router.push({ path: '/receivedArticleDetail', query: { id: articleId } })
  } else {
    router.push({ path: '/articleNotFound' })
  }
}
const auditArticle = (index: number, row: AttributeAddableObject) => {
  // TODO：审批页面跳转
  console.log(index, row)
  gotoDetail(row.id)
}

getReceivedArticle(1,2)
</script>


<style>
.received_table {
  text-align: center;
}
</style>
