<template>
    <el-row>
        <el-col :md="12" :sm="24" v-for="articleInfo in TestArticleList">
            <ArticleInfo :articleInfo="articleInfo" :statusVisible="currentSettings.statusVisible" :iconVisible="currentSettings.iconVisible" :tagsVisible="currentSettings.tagsVisible"/>
        </el-col>
    </el-row>
    <el-pagination class="article_list_pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
                   layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total">
    </el-pagination>
</template>

<script setup>
import ArticleInfo from '@/components/article/ArticleInfo.vue';
import { reactive, toRefs, ref } from 'vue';
import { useStore } from 'vuex';
const props = defineProps({
    mode: {
        type: String,
        default: 'received',
    }
})

const currentSettings = reactive({
    statusVisible: true,
    iconVisible: true,
    tagsVisible: true
})

const modeSettings = {
    USER_OWN: {
        statusVisible: true,
        iconVisible: true,
        tagsVisible: true
    },
    AUDIT: {
        statusVisible: true,
        iconVisible: false,
        tagsVisible: true
    },
    LOBBY: {
        statusVisible: true,
        iconVisible: false,
        tagsVisible: true
    },
    RECEIVED: {
        statusVisible: false,
        iconVisible: false,
        tagsVisible: true
    }
}

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
        received_by: '下北泽日报',
        tags: ['生物制沼', '沼气动力学']
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
        received_by: '',
        tags: ['生物制沼', '沼气动力学']
    },
    {
        articleId: 'a3',
        title: '关于生物制沼的研究',
        contributor: 'Mizuro_',
        userId: '1919810',
        organization: '下北泽师范大学',
        grade: '大三',
        mentor: 'mky',
        description: '这是一篇关于生物制沼的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者Mizuro_潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为生物制沼的研究提供了新的思路和方法。',
        status: 'FAIL_AUDITED',
        time: '1919年8月10日',
        received_by: '',
        tags: ['生物制沼', '沼气动力学']
    },
    {
        articleId: 'a4',
        title: '关于生物制沼的研究',
        contributor: 'Mizuro_',
        userId: '1919810',
        organization: '下北泽师范大学',
        grade: '大三',
        mentor: 'mky',
        description: '这是一篇关于生物制沼的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者Mizuro_潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为生物制沼的研究提供了新的思路和方法。',
        status: 'ROUGH',
        time: '1919年8月10日',
        received_by: '',
        tags: ['生物制沼', '沼气动力学']
    },
    {
        articleId: 'a5',
        title: '关于生物制沼的研究',
        contributor: 'Mizuro_',
        userId: '1919810',
        organization: '下北泽师范大学',
        grade: '大三',
        mentor: 'mky',
        description: '这是一篇关于生物制沼的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者Mizuro_潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为生物制沼的研究提供了新的思路和方法。',
        status: 'SUBMITTED',
        time: '1919年8月10日',
        received_by: '',
        tags: ['生物制沼', '沼气动力学'],
        audit_by: ''
    }
])

const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})
</script>

<style scoped>
.article_list_pagination {
  margin: 20px 0 10px 0;
  display: flex;
  justify-content: center;
}
</style>
