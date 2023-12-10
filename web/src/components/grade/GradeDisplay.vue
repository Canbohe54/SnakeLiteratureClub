<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-card class="box-card result-list-card">
          <el-empty v-if="gradeList.graList.length === 0" description="暂无结果" />
          <el-row v-for="(gradeInfo,index) in gradeList.graList"
                  :key="index"
                  :span="8"
                  :gutter="24"
                  :offset="index > 0 ? 2 : 0">
            <el-col :span="24" >
              <el-card class="box-card result-single-card" >
                <div>{{gradeInfo.text_by}}</div>
                <div>{{gradeInfo.grade_all}}</div>
                <div>{{gradeInfo.advice}}</div>
<!--                <div>-->
<!--                  <el-table :data="gradeInfo" border stripe style="width: 100%">-->
<!--                    <el-table-column prop="expert_id" label="评分人" width="180" />-->
<!--                    <el-table-column prop="grade_all" label="评分" width="180" />-->
<!--                    <el-table-column prop="advice" label="评价" />-->
<!--                  </el-table>-->
<!--                </div>-->
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
import { reactive, watch } from 'vue'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}
const gradeList = reactive({
  graList: []
})
getGradeList()

watch(() => route.query.id, () => {
  getGradeList()
})
// 监听 page size 改变的事件
function handleSizeChange (newSize: any) {
  pageInfo.pageSize = newSize
  getGradeList()
}

// 监听 页码值 改变的事件
function handleCurrentChange (newPage: any) {
  pageInfo.currentPage = newPage
  getGradeList()
}

async function getTextBy (graList: any) {
  await Promise.all(
    graList.map(async (item: any) => {
      await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: item.expert_id
      }, response => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          item.text_by = response.data.user_info.name
        } else {
          console.log(response)
        }
      })
    })
  )
  console.log(graList)
  gradeList.graList = graList
}

async function getGradeList () {
  await (SYNC_POST('/grade/getGradeByArticleId', {
    article_id: route.query.id,
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
  }, async (response) => {
    console.log(response)
    if (response.status === 200 && response.data.statusMsg === 'success') {
      pageInfo.total = response.data.grades.total
      await getTextBy(response.data.grades.list)
    } else {
      console.log(response)
    }
  }))
}
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
