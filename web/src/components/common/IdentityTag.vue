<template>
    <el-tag :type="getIndentityTagType(identity)" :effect="getIndentityTagEffect(identity)" disable-transitions>{{ getIdentityChinese(identity) }}</el-tag>
</template>
<script setup>
import { getIdentityChinese, getIndentityTagType, getIndentityTagEffect } from '@/scripts/common/userIdentityMatch';

const props = defineProps({
    identity: String,
})
</script>