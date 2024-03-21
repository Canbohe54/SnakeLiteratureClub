<template>
    <el-card @click="handleCardClicked" body-style="padding:0;">
        <div class="article-status-container">
            <el-tooltip content="刊登作品" placement="bottom" effect="light">
                <div class="article-status-div"></div>
            </el-tooltip>
        </div>
        <div class="article-info-card">
            <div class="article-info-title">{{ testInfo.title }}</div>
            <div class="article-author-info">
                <span>{{ testInfo.contributor }}</span>
                <span>（{{ testInfo.grade }}）</span>
                <span>{{ testInfo.organization }}</span>
            </div>
            <div class="article-author-info">
                <span class="article-info-mentor">{{ testInfo.mentor===''?'':'指导老师：' }}{{ testInfo.mentor }}</span>
            </div>
            <div class="article-info-descrption">{{ testInfo.descrption }}</div>
            <div class="article-info-time">{{ testInfo.time }}</div>
        </div>
    </el-card>
</template>

<script setup>
import { reactive } from 'vue';

// const props = defineProps({
//     articleInfo: {
//         title: String, //标题
//         contributor: String, //作者
//         organization: String, //单位
//         grade: String, //年级
//         mentor: String, //导师
//         descrption: String, //描述
//         status: String, //状态
//         time: String, //时间
//         mark: Number, //分数
//     }
// })

// const { articleInfo } = toRefs(props)

const testInfo = reactive({
    title: '关于沼气动力学的研究',
    contributor: '田所浩二',
    organization: '下北泽中学',
    grade: '五年级',
    mentor: '野兽先辈',
    descrption: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。',
    status: 'PUBLIC',
    time: '1919年8月10日',
    mark: 15,
})

/*
若status为空，此articleInfo为审核员、专家、报刊专员页面使用
刊登稿件
公开稿件
*/

function handleCardClicked() {
    if (testInfo.status === '公开稿件') {
        console.log('跳转到文章详情页')
    } else {
        console.log('跳转到文章编辑页')
    }
}
</script>

<style scoped>
.article-status-container {
    display: flex;
    justify-content: flex-end;
    margin-right: 20px;
    height: 0;
}

.article-status-div {
    position: relative;
    --status-public: #f56c6c;
    --status-published: #67c23a;
    width: 0;
    height: 0;
    border: 10px solid transparent;
    border-top: 25px solid var(--status-public);
    border-right: 10px solid var(--status-public);
    border-left : 10px solid var(--status-public);
}

.article-info-card {
    text-align: start;
    padding: 20px;
}

.article-info-title {
    font: bold 20px 'Microsoft YaHei';
}

.article-author-info {
    margin: 5px 0;
    height: 16px;
    font: 14px 'Microsoft YaHei';
}
.article-info-descrption {
    margin-top: 10px;
    height: 2.5rem;
}
.article-info-time {
    margin-top: 10px;
    font: 12px 'Microsoft YaHei';
}
</style>