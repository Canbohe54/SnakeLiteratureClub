<template>
    <el-card class="article-el-card" body-style="padding:0;">
        <div class="article-card-disp">
            <div>
                <div class="article-status-container" v-if="statusVisible">
                    <el-tooltip :content="handleStatusTag()" placement="bottom" effect="light">
                        <div class="article-status-div"></div>
                    </el-tooltip>
                </div>
                <div class="article-info-card">
                    <div class="article-info-title"><span class="article-title-span" @click="handleCardClicked">{{ testInfo.title }}</span></div>
                    <div class="article-author-info">
                        <span>{{ testInfo.contributor }}</span>
                        <span>（{{ testInfo.grade }}）</span>
                        <span>{{ testInfo.organization }}</span>
                    </div>
                    <div class="article-author-info">
                        <span class="article-info-mentor">{{ testInfo.mentor === '' ? '' : '指导老师：' }}{{ testInfo.mentor
                            }}</span>
                    </div>
                    <div class="article-info-descrption">{{ testInfo.descrption }}</div>
                    <div class="article-info-time">{{ testInfo.time }}</div>
                </div>
            </div>
            <div class="article-menu" v-if="articleClicked" v-on:mouseleave="articleClicked=false;">
                <el-button @click="console.log('locked');">进入审阅（锁定稿件）</el-button>
                <el-button  @click="articleClicked=false;">取消</el-button>
            </div>
        </div>
    </el-card>
</template>

<script setup>
import { reactive, toRefs, ref } from 'vue';

const props = defineProps({
    articleInfo: {
        title: String, //标题
        contributor: String, //作者
        organization: String, //单位
        grade: String, //年级
        mentor: String, //导师
        descrption: String, //描述
        status: String, //状态
        time: String, //时间
        mark: Number, //分数
    },
    statusVisible: {
        type: Boolean,
        default: true,
    }
})

const { articleInfo, statusVisible } = toRefs(props)

const testInfo = reactive({
    articleId: '',
    title: '关于沼气动力学的研究',
    contributor: '田所浩二',
    organization: '下北泽中学',
    grade: '五年级',
    mentor: '野兽先辈',
    descrption: '这是一篇关于沼气动力学的研究论文，主要研究了沼气的产生、利用和储存等方面的问题。这世间有关沼气动力学的资料少之又少，为了填补这方面的空缺，作者田所浩二潜心研究，不断付出实践，终于完成了这一篇大作。在这篇论文中，作者详细介绍了沼气的产生原理、利用方法和储存技术，为沼气动力学的研究提供了新的思路和方法。',
    status: 'PUBLISHED',
    time: '1919年8月10日',
    journal: '下北泽日报'
})

const articleClicked = ref(false)
/*
若status为空，此articleInfo为审核员、专家、报刊专员页面使用
刊登稿件
公开稿件
*/
function handleStatusTag() {
    if (testInfo.status === 'PUBLISHED') {
        return '刊登作品：已刊登于 ' + testInfo.journal
    } else if (testInfo.status === 'PUBLIC') {
        return '公开作品'
    } else {
        return ''
    }
}

function handleStatusColor() {
    if (testInfo.status === 'PUBLISHED') {
        $('.article-status-div').css('border-top-color', 'var(--status-published)')
        $('.article-status-div').css('border-right-color', 'var(--status-published)')
        $('.article-status-div').css('border-left-color', 'var(--status-published)')
    } else if (testInfo.status === 'PUBLIC') {
        $('.article-status-div').css('border-top-color', 'var(--status-public)')
        $('.article-status-div').css('border-right-color', 'var(--status-public)')
        $('.article-status-div').css('border-left-color', 'var(--status-public)')
    }
    console.log(testInfo.status)
}
window.onload = function () {
    handleStatusColor()
}

function handleCardClicked() { //TODO: 验证用户身份，若为学生/老师，直接进入阅读界面
    if (testInfo.status === '公开稿件') {
        console.log('跳转到文章详情页')
    } else {
        console.log('跳转到文章编辑页')
    }
    articleClicked.value = !articleClicked.value
}
</script>

<style scoped>
.article-card-disp {
    position: relative;
    z-index: 1;
}

.article-el-card {
    margin: 10px;
    border-radius: 10px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.article-el-card:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.2);
}

.article-status-container {
    display: flex;
    justify-content: flex-end;
    margin-right: 20px;
    height: 0;
}

.article-status-div {
    position: relative;
    --status-published: #f56c6c;
    --status-public: #67c23a;
    width: 0;
    height: 0;
    border: 10px solid transparent;
    border-top: 25px solid transparent;
    border-right: 10px solid transparent;
    border-left: 10px solid transparent;
}

.article-info-card {
    text-align: start;
    padding: 20px;
}

.article-info-title {
    font: bold 20px 'Microsoft YaHei';
}

.article-title-span {
    cursor: pointer;
}

.article-title-span:hover {
    color: #757575;
}

.article-author-info {
    margin: 5px 0;
    height: 16px;
    font: 14px 'Microsoft YaHei';
}

.article-info-descrption {
    margin-top: 10px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
}

.article-info-time {
    margin-top: 10px;
    font: 12px 'Microsoft YaHei';
}

.article-menu {
    position: absolute;
    z-index: 2;
    top: 0;
    right: 0;
    width: -webkit-fill-available;
    height: -webkit-fill-available;
    /* background-color: rgba(255, 255, 255, 0.7); */
    background: transparent;
    backdrop-filter: blur(3px);
    transition: all 2s;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>