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
                <div>
                  <el-table :data="gradeInfo" border stripe style="width: 100%">
                    <el-table-column prop="expert_id" label="评分人" width="180" />
                    <el-table-column prop="grade_all" label="评分" width="180" />
                    <el-table-column prop="advice" label="评价" />
                  </el-table>
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
import { reactive, watch, ref } from 'vue'
import { SYNC_GET } from '@/scripts/Axios'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import router from '@/router'

const store = useStore()
const route = useRoute()
// 不知道为什么不能监听searcWord
// const searchWord = ref(route.query.wd)
const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}
const gradeList = reactive({
  graList: []
})
const gradeStatus = [3]
getGradeList()

watch(() => route.query.wd, () => {
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

  gradeList.graList = graList
}

async function getGradeList () {
  await (SYNC_GET('/grade/getGradeByArticleId', {
    article_id: route.query.id,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'success') {
      await getTextBy(response.data.grades.list)
      pageInfo.total = response.data.grades.total
    } else {
      console.log(response)
    }
  }))
}
function gotoDetail (articleId : any) {
  console.log(articleId)
  if (articleId !== '' && articleId !== undefined) {
    router.push({ path: '/articleDetail', query: { article_id: articleId } })
  } else {
    router.push({ path: '/articleNotFound' })
  }
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
