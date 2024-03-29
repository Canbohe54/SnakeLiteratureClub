<template>
    <!-- 画大饼，此页面包含：稿件列表（草稿箱、审核结果、已公开作品、已刊登作品） -->
    <!-- 个人信息 -->
    <el-row class="user-center">
        <el-col :lg="6" :md="7" :sm="24">
            <el-card class="visiting-card">
                <UserVisitingCard :userId="userId" />
            </el-card>
        </el-col>
        <el-col :lg="18" :md="17" :sm="24">
            <el-card class="user-center-main">
                <UserStatistics />
            </el-card>
            <el-card class="user-center-main">
                <PublicList />
            </el-card>
            <el-divider border-style="dashed"  style="margin: 24px 20px;width: auto;"><el-text type="info">我也是有底线的~</el-text></el-divider>
        </el-col>
    </el-row>
</template>

<script setup>
import UserVisitingCard from './UserVisitingCard.vue';
import UserStatistics from './UserStatistics.vue';
import PublicList from './articleList/PublicList.vue';
</script>

<style scoped>
.user-center {
    margin-top: 20px;
    display: flex;
    flex-direction: row-reverse;
}

.visiting-card {
    margin-right: 20px;
    border-radius: 10px;
}
.user-center-main {
    margin: 0 20px 20px;
    border-radius: 10px;
}
</style>
