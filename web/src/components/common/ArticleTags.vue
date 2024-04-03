<template>
    <span v-for="tagValue, tagGroup in thisTags">
        <el-tooltip :content="tagGroup + '：' + tag" placement="bottom" effect="light"
            v-for="(tag) in thisTags[tagGroup]">
            <el-tag class="article-info-tag" effect="light" :type="handleTagType(tagGroup)" disable-transitions round>{{
        tag }}</el-tag>
        </el-tooltip>
    </span>
</template>

<script lang="ts" setup>
import { onMounted, ref, toRefs, defineExpose } from 'vue'

const props = defineProps({
    tagsJsons: {
        type: Object,
        required: true
    }
})

const { tagsJsons } = toRefs(props)

const thisTags = ref(null)

const handleTagType = (tagGroup) => {
    switch (tagGroup) {
        case '体裁':
            return 'primary'
        case '题材':
            return 'success'
        case '情感':
            return 'warning'
        case '风格':
            return 'danger'
        case '主题':
            return 'plain'
        case '其他':
            return 'info'
        default:
            return 'info'
    }
}

const setTags = (tagsJsons) => {
    thisTags.value = tagsJsons
}

onMounted(() => {
    setTags(tagsJsons.value)
})

defineExpose({ thisTags, setTags })

</script>

<style scoped>
.article-info-tag {
    margin-right: 5px;
}
</style>
