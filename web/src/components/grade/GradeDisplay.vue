<template>
  <el-row>
    <el-col>
      <div>
        <el-card >
          <el-empty v-if="gradeList.graList.length === 0" description="暂无评分" />
          <div v-else>
            <el-table :data="gradeList.graList" stripe class="grade-table" flexible>
              <el-table-column  label="评分人">
                <template #default="scope">
                  <div class="grade-user-info">
                    <el-avatar :src="scope.row.pictureUrl" @click="toUserPage(scope.row.expertId)"/>
                    <span class="CommenterName">{{ scope.row.text_by }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="评分">
                <template #default="scope">
                  <el-popover effect="light" trigger="hover" placement="top" width="auto">
                    <template #default>
                      <div>
                        文字与表达：<el-rate v-model="scope.row.grade_expr" size="large" :colors="colors" show-score disabled />
                      </div>
                      <div>
                        内容与结构：<el-rate v-model="scope.row.grade_struct" size="large" :colors="colors" show-score disabled />
                      </div>
                      <div>
                        主题相关性：<el-rate v-model="scope.row.grade_theme" size="large" :colors="colors" show-score disabled />
                      </div>
                    </template>
                    <template #reference>
                      <el-tag>{{ scope.row.grade_all }}</el-tag>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>

              <el-table-column prop="advice" label="评价"/>
            </el-table>

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
          </div>

        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { reactive, watch } from 'vue'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import { useRoute } from 'vue-router'
import { ref } from "vue"
import router from "@/router";

const colors = ref(['#99A9BF', '#F7BA2A', '#FF9900'])

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

function toUserPage (id: string) {
  router.push(`/user/${id}/article`)
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
          item.pictureUrl = response.data.user_info.pictureUrl
          item.expertId = response.data.user_info.id
        } else {
          console.log(response)
        }
      })
    })
  )
  gradeList.graList = graList
}

async function getGradeList () {
  await (SYNC_POST('/grade/getGradeByArticleId', {
    article_id: route.query.id,
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize,
  }, async (response) => {
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
.grade-table {
  display: flex;
  justify-content: center;
}

.grade-user-info {
  display: flex;
  align-items: center;
}

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

.CommenterName {
  width: 70px;
  margin-left: 20px;
}
</style>
