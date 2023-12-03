<template>
    <div class="search">
        <NavBar class="navbar" /><!--导航栏-->
        <SearchBar />
      <el-row>
        <el-col :span="18" :offset="3">
          <SearchFilter />
        </el-col>
      </el-row>
        <ArticleDisplay/>
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import SearchBar from '../components/SearchBar.vue'
import ArticleDisplay from "@/components/article/ArticleDisplay.vue";
import SearchFilter from "@/components/search/SearchFilter.vue";
@Options({
  components: {
    SearchFilter,
    NavBar,
    SearchBar,
    ArticleDisplay
  }
})
export default class SearchView extends Vue { }
</script>
<style>
.search {
    margin: 0 50px;
}

.navbar {
    margin: 0 0;
}
</style>
