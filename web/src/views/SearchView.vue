<template>
  <el-scrollbar height="100vh">
    <div class="container">
      <NavBar class="navbar" /><!--导航栏-->
      <el-row>
        <el-col :span="8" :offset="8">
          <div class="mt-4 search-bar">
            <SearchBar />
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="18" :offset="3">
          <SearchFilter ref="SearchFilterRef" @change="() => articleFiltrate()" />
        </el-col>
      </el-row>
      <ArticleDisplay ref="ArticleDisplayRef"/>
    </div>
  </el-scrollbar>
</template>
<script lang="ts" setup>
import { ref } from "vue"
import NavBar from "@/components/NavBarResponsive.vue";
import SearchBar from '../components/SearchBar.vue'
import ArticleDisplay from '@/components/article/ArticleDisplay.vue'
import SearchFilter from '@/components/search/SearchFilter.vue'
import { ArticleInfo, articleTagFilter } from "@/scripts/ArticleTagFilter";
import { useRoute } from "vue-router";

const route = useRoute()

const SearchFilterRef = ref()
const ArticleDisplayRef = ref()
let filtratedArticleList = ref<ArticleInfo[]>()

function articleFiltrate() {
  const articleList = ArticleDisplayRef.value.articleList
  articleList.artList = articleTagFilter(articleList.originalArticleList, SearchFilterRef.value.filterSelection)
  filtratedArticleList = articleList.artList
}
</script>
<style>
.navbar {
  margin: 0 0;
}

.search-bar {
  margin: 20px 0 10px 0;
  padding: 5px 0 5px 3px;
}
</style>
