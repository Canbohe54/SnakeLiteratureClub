<template>
  <!--  <h1>你的搜索内容：{{ searchWord }}</h1>-->
      <div>
        <el-input v-model="searchWord" class="search-input-text" @keydown.enter="handleSearch"
                  placeholder="输入关键词搜索" size="large">
          <template #append>
            <el-button class="search-btn" @click="handleSearch">
                <Search class="search-button" />
            </el-button>
          </template>
        </el-input>
      </div>

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

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 15px 0 12px;
}

.search-input-text :deep(.el-input__wrapper) {
  border-radius: 1rem;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.search-input-text :deep(.el-input-group__append) {
  background-color: white;
  border-radius: 1rem;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.search-input-text :deep(.el-input-group__append):hover {
  background-color: #f4f4f5;
  border-color: var(--el-input-hover-border-color);
  box-shadow: 0 0 0 0.05rem var(--el-input-hover-border-color) inset;
}

.search-input-text :deep(.is-focus){
  border-color: var(--el-input-hover-border-color);
  box-shadow: 0 0 0 1px var(--el-input-hover-border-color) inset;
}

.search-button {
    width: 1.25rem;
    height: 1.25rem;
}

</style>
