<template>
    <!-- 个人作品信息统计：总点赞量、阅读量、发布作品数目、刊登比例 -->
    <el-row class="statistics-row" v-if="userIdentity==='CONTRIBUTOR' || userIdentity==='TEACHER'"><!-- 仅投稿者/教师可见 -->
        <el-col :span="8">
            <el-statistic :title="(userIdentity==='TEACHER'?'指导的':'')+(userStatistics.recievedArticles ? '刊登 / ' : '') + '公开作品数'"
                :value="userStatistics.recievedArticles ? userStatistics.recievedArticles : userStatistics.publishedArticles">
                <template #suffix>
                    <span v-if="userStatistics.recievedArticles">/&nbsp;{{ userStatistics.publishedArticles }}</span>
                </template>
            </el-statistic>
        </el-col>
        <el-col :span="8">
            <el-statistic title="总阅读量" :value="userStatistics.totalReads" />
        </el-col>
        <el-col :span="8">
            <el-statistic title="总点赞量" :value="userStatistics.totalLikes" />
        </el-col>
    </el-row>
    <el-divider border-style="dashed" v-if="userId == store.getters.getUserInfo.id&&(userIdentity==='CONTRIBUTOR' || userIdentity==='TEACHER')"/>
    <el-row class="statistics-row" v-if="userId == store.getters.getUserInfo.id&&(userIdentity==='CONTRIBUTOR' || userIdentity==='TEACHER')">
        <el-col :span="8">
            <el-statistic title="草稿箱" :value="userStatistics.roughCount" class="statistics-number" id="draft" />
        </el-col>
        <el-col :span="8">
            <el-statistic title="审核中" :value="userStatistics.beingAuditedCount" class="statistics-number" id="under_review" />
        </el-col>
        <el-col :span="8">
            <el-statistic title="审核未通过" :value="userStatistics.failAuditedCount" class="statistics-number" id="failed_audit" />
        </el-col>
    </el-row>
    <el-row class="statistics-row" v-if="userId == store.getters.getUserInfo.id&&(userIdentity==='CONTRIBUTOR' || userIdentity==='TEACHER')">
        <el-col :span="8">
            <el-statistic title="有修改建议" :value="userStatistics.reasonCount" class="statistics-number" id="audit_suggested" />
        </el-col>
        <el-col :span="8">
            <el-statistic title="推荐未通过" :value="userStatistics.failedReviewCount" class="statistics-number" id="failed_review" />
        </el-col>
        <el-col :span="8">
            <el-statistic title="需确认录用" :value="userStatistics.postRecordCount" class="statistics-number" id="recieve_confirm" />
        </el-col>
    </el-row>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { SYNC_GET } from "@/scripts/Axios";
import { errorCallback } from "@/scripts/ErrorCallBack";
import { useStore } from "vuex";
import { useRoute } from "vue-router";

const userStatistics = reactive({
    publishedArticles: 0,
    recievedArticles: 1,
    totalReads: 0,
    totalLikes: 0,
    roughCount: 0,
    beingAuditedCount: 0,
    failAuditedCount: 0,
    reasonCount: 0,
    failedReviewCount: 0,
    postRecordCount: 0
})
const store = useStore()
const route = useRoute()

const userId = route.params.id ? route.params.id : store.getters.getUserInfo.id
const userIdentity = ref('')

async function getUserIdentity() {
    await SYNC_GET('/usr/getUserIdentity', {
        user_id: userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userIdentity.value = response.data.data
        }
        else {
            errorCallback(response)
        }
    })
}

// TODO:这里直接写Ajax
async function getAllViewCount() {
    await SYNC_GET('/view/getAllViewCountByContributorID', {
        contributorId: userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userStatistics.totalReads = response.data.data.allViewCount
        }
        else {
            errorCallback(response)
        }
    })
}

async function getAllLikeCount() {
    await SYNC_GET('/like/getAllLikeCountByContributorID', {
        contributorId: userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userStatistics.totalLikes = response.data.data.allLikeCount
        }
        else {
            errorCallback(response)
        }
    })
}

async function getRecievedAndPublishedCount() {
    await SYNC_GET('/article/getRecievedAndPublishedCount', {
        contributorId: userId
    }, async (response) => {
        if (response.status === 200 && response.data.code === 2001) {
            userStatistics.publishedArticles = response.data.data.publishedCount
            userStatistics.recievedArticles = response.data.data.receivedCount
        }
        else {
            errorCallback(response)
        }
    })
}

async function getContributorCenterInformation() {
  await SYNC_GET('/article/getContributorCenterInformation', {
    contributorId: userId
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      userStatistics.roughCount = response.data.data.roughCount
      userStatistics.beingAuditedCount = response.data.data.beingAuditedCount
      userStatistics.failAuditedCount = response.data.data.failAuditedCount
      userStatistics.reasonCount = response.data.data.reasonCount
      userStatistics.failedReviewCount = response.data.data.failedReviewCount
      userStatistics.postRecordCount = response.data.data.postRecordCount
    }
    else {
      errorCallback(response)
    }
  })
}

onMounted(() => {
    getUserIdentity()
    getAllLikeCount()
    getAllViewCount()
    getRecievedAndPublishedCount()
    getContributorCenterInformation()
})
</script>

<style scoped>
.statistics-row {
    margin-top: 10px;
}

.statistics-number:hover {
    cursor: pointer;
    --el-statistic-title-color: #409EFF;
    --el-statistic-content-color: #409EFF;
}

#draft {
    --el-statistic-title-color: #67C23A;
    --el-statistic-content-color: #67C23A;
}

#under_review {
    --el-statistic-title-color: #909399;
    --el-statistic-content-color: #909399;
}

#failed_audit {
    --el-statistic-title-color: #F56C6C;
    --el-statistic-content-color: #F56C6C;
}

#audit_suggested {
    --el-statistic-title-color: #E6A23C;
    --el-statistic-content-color: #E6A23C;
    --el-statistic-title-color: #67C23A;
    --el-statistic-content-color: #67C23A;
}

#receiving {
    --el-statistic-title-color: #E6A23C;
    --el-statistic-content-color: #E6A23C;
}

#recieve_confirm {
    --el-statistic-title-color: #909399;
    --el-statistic-content-color: #909399;
}
</style>
