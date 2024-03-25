<template>
    <el-row>
        <el-col :md="12" :sm="24" v-for="articleInfo in TestArticleList">
            <ArticleInfo :articleInfo="articleInfo" :statusVisible="true"/>
        </el-col>
    </el-row>
</template>

<script setup>
import ArticleInfo from '@/components/article/ArticleInfo.vue';
import { reactive, toRefs, ref } from 'vue';
import { useStore } from 'vuex';

const TestArticleList = reactive([
    {
        articleId: 'a1',
        title: '关于沼气动力学的研究',
        contributor: '田所浩二',
        userId: '114514',
        organization: '下北泽中学',
        grade: '五年级',
        mentor: '野兽先辈',
        description: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
        status: 'PUBLISHED',
        time: '1919年8月10日',
        received_by: '下北泽日报'
    }, 
    {
        articleId: 'a2',
        title: '关于生物制沼的研究',
        contributor: 'Mizuro_',
        userId: '1919810',
        organization: '下北泽师范大学',
        grade: '大三',
        mentor: 'mky',
        description: '这是一篇关于生物制沼的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者Mizuro_潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为生物制沼的研究提供了新的思路和方法。',
        status: 'PUBLISHED',
        time: '1919年8月10日',
        received_by: ''
    }
])
</script>