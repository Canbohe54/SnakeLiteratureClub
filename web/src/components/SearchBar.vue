<template>
    <h1>你的搜索内容：{{ searchWord }}</h1>
</template>
<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { ref } from 'vue'
const route = useRoute()
const searchWord = ref(route.query.wd)
</script>
