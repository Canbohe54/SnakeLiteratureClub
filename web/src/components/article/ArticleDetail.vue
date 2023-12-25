<template>
  <div>
    <el-container>
      <el-main>
        <el-card>
          <el-row class="box-card"><el-text>{{articleDetail.title}}</el-text></el-row>
          <el-row class="box-card"><el-text>({{articleDetail.text_by}})</el-text></el-row>
          <el-divider />
          <el-text>{{articleDetail.text}}</el-text>
        </el-card>
      </el-main>

      <div class="gradeEdit" v-if="store.getters.getUserInfo.identity=='专家'">
        <GradeEditor class="graedit"/>
      </div>

      <div class="gradeDis">
        <GradeDisplay class="graDis"/>
      </div>

      <el-footer>
        <el-card>
          <el-row>
            <el-avatar> user </el-avatar>
            <div class="blank"></div>
            <el-text>username </el-text>
            <div class="e"></div>
            <el-text>comments</el-text>
          </el-row>
          <el-divider />
          <el-row>
            <el-avatar> user </el-avatar>
            <div class="blank"></div>
            <el-text>username </el-text>
            <div class="e"></div>
            <el-text>comments</el-text>
          </el-row>
        </el-card>
      </el-footer>
    </el-container>
  </div>
</template>
<script lang="ts" setup>
import {reactive} from "vue";
import {AttributeAddableObject} from "@/scripts/ArticleTagFilter";
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";
import {SYNC_GET} from "@/scripts/Axios";
import GradeEditor from "@/components/grade/GradeEditor.vue";
import GradeDisplay from "@/components/grade/GradeDisplay.vue";
import store from "@/store";

const route = useRoute()

const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  text_by: '',
  title: '',
  description: '',
  status: '',
  attr: ''
})

function errorCallback(response: any) {
  console.log(response)
  if (response.status === 200) {
    ElMessage({
      showClose: true,
      message: response.data.statusMsg,
      type: 'error'
    })
  } else {
    ElMessage({
      showClose: true,
      message: 'Network Error!',
      type: 'error'
    })
  }
}
// 有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      for (const dataKey in response.data.article) {
        articleDetail[dataKey] = response.data.article[dataKey]
      }
      await getTextBy()
    } else {
      errorCallback(response)
    }
  })
})()
async function getTextBy () {
      await SYNC_GET('/usr/getUserBasicInfo', {
        user_id: articleDetail.text_by
      }, async (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          articleDetail.text_by = response.data.user_info.name
        } else {
          console.log(response)
        }
      })
}
</script>
<style>
.box-card{
  display: flex;
  justify-content: center;
}

.e{
  border-right: 1px solid var(--el-border-color);
  margin-right: 10px;
  margin-left: 10px;
}

.blank{
  margin-right: 10px;
}

.gradeDis{
  text-align:center;
  margin: 20px 20px;
}
</style>
