<template>
    <el-card class="article-el-card" body-style="padding:0;">
        <div class="article-card-disp">
            <div>
                <div class="article-status-container" v-if="statusVisible">
                    <el-tooltip :content="handleStatusTag()" placement="bottom" effect="light">
                        <div class="article-status-div" :id="articleInfo.articleId"></div>
                    </el-tooltip>
                </div>
                <div class="article-info-card">
                    <div class="article-info-title"><span class="article-title-span" @click="handleCardClicked">{{
                    articleInfo.title }}</span></div>
                    <div class="article-author-info">
                        <span>{{ articleInfo.contributor }}</span>
                        <span>（{{ articleInfo.grade }}）</span>
                        <span>{{ articleInfo.organization }}</span>
                    </div>
                    <div class="article-author-info">
                        <span class="article-info-mentor">{{ articleInfo.mentor === '' ? '' : '指导老师：' }}{{
                    articleInfo.mentor
                }}</span>
                    </div>
                    <div class="article-info-descrption">{{ articleInfo.description }}</div>
                    <div class="article-info-time">{{ articleInfo.time }}</div>
                </div>
            </div>
            <div class="article-menu" v-if="isArticleMenuOpen" v-on:mouseleave="isArticleMenuOpen = false;">
                <el-button @click="console.log('locked');">进入审阅（锁定稿件）</el-button>
                <el-button @click="isArticleMenuOpen=false;">取消</el-button>
            </div>
        </div>
    </el-card>
</template>

<script setup>
import { reactive, toRefs, ref, onUpdated, onMounted } from 'vue';
import { useStore } from 'vuex';

const store = useStore();

const currentUser = reactive({
    userId: store.getters.getUserInfo.id,
    identity: store.getters.getUserInfo.identity,
})

const props = defineProps({
    articleInfo: {
        articleId: String, //文章ID
        title: String, //标题
        userId: String, //用户ID
        contributor: String, //作者
        organization: String, //单位
        grade: String, //年级
        mentor: String, //导师
        description: String, //描述
        status: String, //稿件
        time: String, //时间
        mark: Number, //分数
    },
    statusVisible: {
        type: Boolean,
        default: true,
    }
})

const { articleInfo, statusVisible } = toRefs(props)

const isArticleMenuOpen = ref(false)
/*
若status为空，此articleInfo为审核员、专家、报刊专员页面使用
刊登稿件
公开稿件
*/
function handleStatusTag() {
    if (articleInfo.value.received_by != '' && articleInfo.value.status === 'PUBLISHED') {
        return '刊登作品：已刊登于 ' + articleInfo.value.received_by
    } else {
        return '公开作品'
    }
}

function handleStatusColor() {
    if (articleInfo.value.status === 'PUBLISHED') {
        if (articleInfo.value.received_by != '') {
            $('#' + articleInfo.value.articleId).css('border-top-color', 'var(--status-published)')
            $('#' + articleInfo.value.articleId).css('border-right-color', 'var(--status-published)')
            $('#' + articleInfo.value.articleId).css('border-left-color', 'var(--status-published)')
        } else {
            $('#' + articleInfo.value.articleId).css('border-top-color', 'var(--status-public)')
            $('#' + articleInfo.value.articleId).css('border-right-color', 'var(--status-public)')
            $('#' + articleInfo.value.articleId).css('border-left-color', 'var(--status-public)')
        }
    }

}
// window.onload = function () {
//     handleStatusColor()
// }
onMounted(() => { // setup语法糖下渲染时周期函数
    handleStatusColor()
    console.log(articleInfo.value)
    console.log(statusVisible.value)
})

function handleCardClicked() { //TODO: 验证用户身份，若为学生/老师，直接进入阅读界面
    if (articleInfo.value.status == '' && (currentUser.identity === 'EXPERT' || currentUser.identity === 'HUNTER')) {
        isArticleMenuOpen.value = !isArticleMenuOpen.value
    } else {

        //TODO: 跳转到文章详情页
    }
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
