<template>
    <!-- 个人作品信息统计：总点赞量、阅读量、发布作品数目、刊登比例 -->
    <el-row class="statistics-row">
        <el-col :span="8">
            <el-statistic :title="(userStatistics.recievedArticles?'刊登 / ':'')+'公开作品数'" :value="userStatistics.recievedArticles?userStatistics.recievedArticles:userStatistics.publishedArticles">
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
    <el-divider border-style="dashed"/>
    <el-row class="statistics-row">
        <el-col :span="8">
            <el-statistic title="草稿箱" class="statistics-number" id="draft"/>
        </el-col>
        <el-col :span="8">
            <el-statistic title="审核中" class="statistics-number" id="under_review"/>
        </el-col>
        <el-col :span="8">
            <el-statistic title="审核未通过" class="statistics-number" id="failed_audit"/>
        </el-col>
    </el-row>
    <el-row class="statistics-row">
        <el-col :span="8">
            <el-statistic title="有修改建议" class="statistics-number" id="audit_suggested"/>
        </el-col>
        <el-col :span="8">
            <el-statistic title="待推荐录用" class="statistics-number" id="receiving"/>
        </el-col>
        <el-col :span="8">
            <el-statistic title="需确认录用" class="statistics-number" id="recieve_confirm"/>
        </el-col>
    </el-row>
</template>

<script setup>
import { reactive } from 'vue';

const userStatistics = reactive({
    publishedArticles: 268500,
    recievedArticles: 114,
    totalReads: 328900,
    totalLikes: 112893
})

// TODO:这里直接写Ajax

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