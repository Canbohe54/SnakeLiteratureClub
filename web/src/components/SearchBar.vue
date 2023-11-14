<template>
    <h1>你的搜索内容：{{ searchWord }}</h1>
</template>
<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { ref } from 'vue'
import {useStore} from "vuex";
const route = useRoute()
const store = useStore()
const searchWord = ref(route.query.wd)
store.commit("setSearchKey", searchWord)
</script>
