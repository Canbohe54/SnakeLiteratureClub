<template>
  <div class="common-layout">
    <el-container>
      <el-main>
        <el-card class="box-card">
          <el-row><el-text>{{articleDetail.title}}</el-text></el-row>
          <el-row><el-text>{{articleDetail.description}}</el-text></el-row>
          <el-text>{{articleDetail.text}}</el-text>
        </el-card>
      </el-main>

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

const route = useRoute()

const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  textBy: '',
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
    } else {
      errorCallback(response)
    }
  })
})()
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

</style>
