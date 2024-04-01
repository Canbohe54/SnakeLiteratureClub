<template>
    <div class="article-card-disp">
        <div>
            <div class="article-status-container"
                v-if="statusVisible && (articleInfo.auditStatus == 'SUBMITTED' || (articleInfo.auditStatus == 'AUDITED' && (articleInfo.publishStatus == 'POSTED' || articleInfo.publishStatus == 'PUBLIC')))">
                <el-tooltip :content="handleStatusTag()" placement="bottom" effect="light">
                    <div class="article-status-div" :id="'status' + articleInfo.articleId"></div>
                </el-tooltip>
            </div>
            <div class="article-info-card">
                <div class="article-info-title"><span :id="'title' + articleInfo.articleId" class="article-title-span"
                        @click="handleCardClicked">
                        {{ articleInfo.title }}</span>
                    <el-tooltip :content="handleStatusIconTip()" placement="bottom" effect="light"
                        v-if="iconVisible && !(articleInfo.auditStatus == 'AUDITED' && (articleInfo.publishStatus == 'PUBLIC' || articleInfo.publishStatus == 'POSTED'))">
                        <el-icon :id="'statusIcon' + articleInfo.articleId" @click="handleCardClicked">
                            <Edit v-if="articleInfo.auditStatus == 'ROUGH'" />
                            <Lock v-else />

                        </el-icon>
                    </el-tooltip>
                </div>
                <div class="article-author-info" v-if="authorInfoVisible">
                    <span>{{ articleInfo.contributor }}</span>
                    <span>（{{ articleInfo.grade }}）</span>
                    <span>{{ articleInfo.organization }}</span>
                </div>
                <div class="article-author-info" v-if="authorInfoVisible">
                    <span class="article-info-mentor">{{ articleInfo.mentor === '' ? '' : '指导老师：' }}{{
                    articleInfo.mentor
                }}</span>
                </div>
                <div class="article-info-descrption">{{ articleInfo.description }}</div>
                <div class="article-info-tags" v-if="tagsVisible">
                    <span v-for= "tagValue,tagGroup in articleInfo.tags">
                        <el-tooltip :content="tagGroup+'：'+tag" placement="bottom" effect="light"  v-for="(tag) in articleInfo.tags[tagGroup]">
                            <el-tag class="article-info-tag" effect="light" :type="handleTagType(tagGroup)"
                                disable-transitions round>{{ tag }}</el-tag>
                        </el-tooltip>
                    </span>
                    
                    
                </div>
                <div class="article-info-bottom">
                    <div class="article-info-time">{{ articleInfo.time }}</div>
                    <div class="article-info-view-count" v-if="viewcountVisible"><el-icon>
                            <View />
                        </el-icon>&nbsp;{{ articleInfo.viewcount }}</div>
                </div>
            </div>
        </div>
        <div class="article-menu" v-if="isArticleMenuOpen" v-on:mouseleave="isArticleMenuOpen = false;">
            <el-button v-for="options in getMenu()" @click="options.onClick" :type="options.type">{{ options.text
                }}</el-button>
            <el-button @click="isArticleMenuOpen = false;" type="info">取消</el-button>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import { reactive, toRefs, ref, onUpdated, onMounted } from 'vue';
import { useStore } from 'vuex';
import { Lock, Edit, Comment, View } from '@element-plus/icons-vue';
import { SYNC_GET } from "@/scripts/Axios";
import { lockArticleById } from "@/scripts/ArticleLocker";

const store = useStore();

const currentUser = reactive({
    userId: store.getters.getUserInfo.id,
    identity: store.getters.getUserInfo.identity,
})

const props = defineProps({
    articleInfo: {
        articleId: String, //文章ID **必须**
        title: String, //标题 **必须**
        userId: String, //用户ID
        contributor: String, //作者 **必须**
        organization: String, //单位 **必须**
        grade: String, //年级 **必须**
        mentor: String, //导师
        description: String, //描述 **必须**
        auditStatus: String, //审核状态 **依mode决定**
        publishStatus: String, //公开状态 **依mode决定**
        time: String, //时间 **必须**
        tags: Object, //标签 **必须** TODO: 改成map
        received_by: String, //刊登报刊 **依mode决定**
        audit_by: String, //审核员 **依mode决定**
        audit_suggestion: String, //审核意见 **依mode决定**
        viewcount: Number, //浏览量 **依mode决定**
    },
    statusVisible: {
        type: Boolean,
        default: true,
    },
    authorInfoVisible: {
        type: Boolean,
        default: true,
    },
    iconVisible: {
        type: Boolean,
        default: true,
    },
    tagsVisible: {
        type: Boolean,
        default: true,
    },
    menuVisible: {
        type: Boolean,
        default: true,
    },
    viewcountVisible: {
        type: Boolean,
        default: true,
    }
})

const menuOnStatus = reactive({
    user_failed_audited: [
        {
            text: '查看审核意见',
            onClick: handleArticleDetail,
            type: 'primary'
        },
        {
            text: '删除稿件',
            onClick: handleArticleDeleate,
            type: 'danger'
        },
        {
            text: '编辑稿件',
            onClick: handleArticleEdit,
            type: 'success'
        }
    ],
    user_rough: [
        {
            text: '删除稿件',
            onClick: handleArticleDeleate,
            type: 'danger'
        },
        {
            text: '编辑稿件',
            onClick: handleArticleEdit,
            type: 'success'
        }
    ],
    user_submitted: [
        {
            text: '查看稿件',
            onClick: handleArticleDetail,
            type: 'primary'
        }
    ],
    user_audited_public: [
        {
            text: '查看稿件',
            onClick: handleArticleDetail,
            type: 'primary'
        },
        {
            text: '不再公开稿件',
            onClick: handleArticlePrivate,
            type: 'danger'
        }
    ],
    user_audited_locked: [
        {
            text: '查看稿件',
            onClick: handleArticleDetail,
            type: 'primary'
        },
        {
            text: '编辑稿件',
            onClick: handleArticleEdit,
            type: 'success'
        },
        {
            text: '公开稿件',
            onClick: handleArticlePublic,
            type: 'danger'
        }
    ],
    expert_hunter: [
        {
            text: '进入审阅（锁定稿件）',
            onClick: handleArticleDetail,
            type: 'primary'
        }
    ],
})

function getMenu() {
    if (articleInfo.value.userId === currentUser.userId) { // 文章是用户自己的文章
        switch (articleInfo.value.auditStatus) {
            case 'AUDITED':
                return menuOnStatus.user_audited_public
            case 'LOCK':
                return menuOnStatus.user_audited_locked
            case 'FAIL_AUDITED':
                return menuOnStatus.user_failed_audited
            case 'ROUGH':
                return menuOnStatus.user_rough
            case 'SUBMITTED':
                return menuOnStatus.user_submitted
        }
    } else {
        if (currentUser.identity === 'EXPERT' || currentUser.identity === 'HUNTER' || currentUser.identity === 'ADMINISTRATOR') {
            return menuOnStatus.expert_hunter
        }
    }
}

const { articleInfo, statusVisible, authorInfoVisible, tagsVisible, menuVisible } = toRefs(props)

const isArticleMenuOpen = ref(false)

/*
若status为空，此articleInfo为审核员、专家、报刊专员页面使用
刊登稿件
公开稿件
*/
function handleStatusTag() {
    switch (articleInfo.value.auditStatus) {
        case 'AUDITED':
            if (articleInfo.value.publishStatus === 'POSTED') {
                return '刊登作品：已刊登于 ' + articleInfo.value.received_by
            } else if (articleInfo.value.publishStatus === 'PUBLIC') {
                return '公开作品'
            }
            break
        case 'SUBMITTED':
            if (articleInfo.value.audit_by == '' || articleInfo.value.audit_by == null) {
                return '初审稿件'
            } else {
                return '复审稿件'
            }
            break
    }
}

function handleStatusIconTip() {
    switch (articleInfo.value.auditStatus) {
        case 'ROUGH':
            return '草稿'
        case 'SUBMITTED':
        case 'BEING_AUDITED':
            return '已提交待审核'
        case 'FAIL_AUDITED':
            return '审核未通过'
        default:
            return '不公开稿件'
    }
}

function handleStatus() {
    switch (articleInfo.value.auditStatus) {
        case 'ROUGH':
            break
        case 'SUBMITTED': // 给审核员标识的是否初审
            if (articleInfo.value.audit_by == '' || articleInfo.value.audit_by == null) {
                $('#status' + articleInfo.value.articleId).css('border-top-color', 'var(--status-public)')
                $('#status' + articleInfo.value.articleId).css('border-right-color', 'var(--status-public)')
                $('#status' + articleInfo.value.articleId).css('border-left-color', 'var(--status-public)')
            } else {
                $('#status' + articleInfo.value.articleId).css('border-top-color', 'var(--status-published)')
                $('#status' + articleInfo.value.articleId).css('border-right-color', 'var(--status-published)')
                $('#status' + articleInfo.value.articleId).css('border-left-color', 'var(--status-published)')
            }
            break
        case 'BEING_AUDITED':
            break
        case 'FAIL_AUDITED':
            $('#title' + articleInfo.value.articleId).css('color', '#f56c6c')
            $('#statusIcon' + articleInfo.value.articleId).css('color', '#f56c6c')
            break
        case 'AUDITED':
            if (articleInfo.value.publishStatus === 'POSTED') {
                $('#status' + articleInfo.value.articleId).css('border-top-color', 'var(--status-published)')
                $('#status' + articleInfo.value.articleId).css('border-right-color', 'var(--status-published)')
                $('#status' + articleInfo.value.articleId).css('border-left-color', 'var(--status-published)')
            } else if (articleInfo.value.publishStatus === 'PUBLIC') {
                $('#status' + articleInfo.value.articleId).css('border-top-color', 'var(--status-public)')
                $('#status' + articleInfo.value.articleId).css('border-right-color', 'var(--status-public)')
                $('#status' + articleInfo.value.articleId).css('border-left-color', 'var(--status-public)')
            }
            break
    }
}

function handleTagType(tagGroup) {
    switch (tagGroup) {
        case '体裁':
            return 'primary'
        case '题材':
            return 'success'
    }
}


onMounted(() => { // setup语法糖下渲染时周期函数
    handleStatus()
})

function handleCardClicked() { //TODO: 验证用户身份，若为学生/老师，直接进入阅读界面
    if (menuVisible.value && currentUser.userId && !(currentUser.identity =='CONTRIBUTOR' || currentUser.identity == 'TEACHER')) {
        isArticleMenuOpen.value = !isArticleMenuOpen.value
    } else {
        router.push({
            path: '/articleDetail',
            query: {
                id: articleInfo.value.articleId
            }
        })
    }
}



async function handleArticleDetail() {
    await SYNC_GET('/article/getPermissions', {
        articleId: articleInfo.value.articleId,
        requester: currentUser.userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            // 锁2小时
            await lockArticleById(articleInfo.value.articleId, currentUser.userId, 7200)
            router.push({
                path: '/articleDetail',
                query: {
                    id: articleInfo.value.articleId
                }
            })
        }
    })
}

function handleArticleSubmit() {

}

function handleArticleDeleate() {

}

function handleArticleEdit() {

}

function handleArticlePrivate() {

}

function handleArticlePublic() {

}

</script>

<style scoped>
.article-card-disp {
    position: relative;
    z-index: 1;
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
    display: flex;
    align-items: center;
}

.article-title-span {
    cursor: pointer;
}

.article-title-span:hover {
    color: #1375d7;
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

.article-info-tags {
    margin-top: 10px;
}

.article-info-tag {
    margin-right: 5px;
}

.article-info-bottom {
    margin-top: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.article-info-time {
    font: 12px 'Microsoft YaHei';
}

.article-info-view-count {
    font: 12px 'Microsoft YaHei';
    color: #909399;
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
