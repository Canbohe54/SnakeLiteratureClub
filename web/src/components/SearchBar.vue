<template>
  <!--  <h1>你的搜索内容：{{ searchWord }}</h1>-->
  <el-row>
    <el-col :span="8" :offset="8">

      <div class="mt-4 search-input">
        <el-input v-model="searchWord" class="input-with-select search-input-text" @keydown.enter="handleSearch"
                  placeholder="请输入关键词搜索" size="large">
          <template #append>
            <el-button class="search-btn" @click="handleSearch">
              <el-icon color="#00AEEC" class="el-icon-right">
                <Search/>
              </el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </el-col>

  </el-row>

</template>
<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { ref } from 'vue'
import { useStore } from 'vuex'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const store = useStore()
const router = useRouter()
const searchWord = ref(route.query.wd)

const handleSearch = async () => {
  if (searchWord.value !== '' && searchWord.value !== undefined) {
    router.push({ path: '/search', query: { wd: searchWord.value } })
  } else {
    router.push({ path: '/search' })
  }
}

</script>
<style scoped>
.search-input {
  margin: 20px 0 10px 0;
  padding: 5px 0 5px 3px;
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-input-text input {
  font-size: 20px !important;
}

.search-input-text /deep/ .el-input-group__append {
  background-color: white;
}
</style>
