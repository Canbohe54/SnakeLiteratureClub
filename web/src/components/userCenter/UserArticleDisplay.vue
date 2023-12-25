<template>
    <el-row>
      <el-col :span="18" :offset="3">
        <div>
          <el-card class="box-card result-list-card">
            <el-empty v-if="articleList.artList.length === 0" description="暂无结果" />
            <el-row gutter="12">
              <el-col v-for="(articleInfo, index) in articleList.artList" :key="index" :span="12">
                <el-card class="box-card result-card-body" shadow="hover">
                  <div>
                    {{ articleInfo['text_by'] }} - {{ articleInfo.time }}
                    <h2>{{ articleInfo.title }}</h2>
                    <div style="min-height: 40px;">
                        {{ articleInfo.description.length > 20 ? articleInfo.description.slice(0, 20) + '...' : articleInfo.description }}
                    </div>
                  </div>
  
                  <div style="margin: 10px 0 0 0;">
                    <el-text v-if="avgGradeMap.get(articleInfo.id) != ''" class="result-rate-text">评分：{{
                      avgGradeMap.get(articleInfo.id) }}/15</el-text>
                    <el-text v-else class="result-rate-text">暂无评分</el-text>
                  </div>
                </el-card>
              </el-col>
            </el-row>
  
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
              :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
              layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total" class="search-result-pageination">
            </el-pagination>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </template>
  <script lang="ts" setup>
  import ArticleInfoCard from "@/components/article/ArticleInfoCard.vue";
  import { reactive, watch } from 'vue'
  import { SYNC_GET, SYNC_POST } from '@/scripts/Axios'
  import { useRoute } from 'vue-router'
  import router from '@/router'
  import { AttributeAddableObject } from "@/scripts/ArticleTagFilter";
  
  // const props = defineProps(['queryRoute', 'queryParams'])
  
  const avgGradeMap = new Map()
  const route = useRoute()
  
  // 不知道为什么不能监听searchWord
  // const searchWord = ref(route.query.wd)
  const pageInfo = {
    currentPage: 1,
    pageSize: 10,
    total: 0
  }
  const articleList = reactive({
    artList: [{ 'id': 'a1', 'text_by': 'Mizuiro', 'time': '1145-1-4-19:19', 'title': '关于沼气动力学的若干研究', 'description': '沼气动力学沼气动力学沼气动力学沼气动力学沼气动力学沼气动力学沼气动力学沼气动力学', 'attr': '{"tags": {}}' },
    { 'id': 'a1', 'text_by': '田所 浩二', 'time': '1919-8-10-11:45', 'title': '关于生物制沼的若干研究', 'description': '生物制沼', 'attr': '{"tags": {}}' }],
    originalArticleList: [{ 'id': 'a1', 'text_by': 'Mizuiro', 'time': '1145-1-4-19:19', 'title': '关于沼气动力学的若干研究', 'description': '沼气动力学', 'attr': '{"tags": {}}' },
    { 'id': 'a1', 'text_by': '田所 浩二', 'time': '1919-8-10-11:45', 'title': '关于生物制沼的若干研究', 'description': '生物制沼', 'attr': '{"tags": {}}' }]
  })
  const articleStatus = [3]
  
  watch(() => route.query.wd, () => {
  })
  // 监听 page size 改变的事件
  function handleSizeChange(newSize: any) {
    pageInfo.pageSize = newSize
  }
  
  // 监听 页码值 改变的事件
  function handleCurrentChange(newPage: any) {
    pageInfo.currentPage = newPage
  }
  
  </script>
  <style scoped>
  .result-list-card :deep(.el-card__body) {
    width: -webkit-fill-available;
  }
  
  .result-list-card {
    border-radius: 10px;
  }

  .result-card-body {
    height: 200px;
  }
  
  .result-rate-text {
    font-size: 16px;
    font-weight: bold;
    color: #ffd500;
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
  