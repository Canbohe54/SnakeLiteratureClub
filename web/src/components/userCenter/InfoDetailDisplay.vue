<template>
    <el-row class="user-info-disp">
        <div class="user-info-table">
            <div class="user-info-main">
                <el-avatar class="mr-3" :size="80" src="https://avatars.githubusercontent.com/u/43968296"></el-avatar>
                <div class="user-nameid">
                    <el-text class="user-name">Canbohe54</el-text>
                    <el-tag type="warning">专家</el-tag>
                </div>
            </div>
            <div class="user-info-detail">
                <p class="user-info-detail-p">
                    <el-text>单位：</el-text>
                <el-text>South China Normal University</el-text>
                </p>
                <p class="user-info-detail-p">
                    <el-text>邮箱：</el-text>
                <el-text>Canbohe54@snake.club</el-text>
                </p>
                <p class="user-info-detail-p">
                    <el-text>个人简介</el-text>
                </p>
            </div>
        </div>
        <div>
            <el-button type="info" plain class="followed">已关注</el-button>
        </div>
    </el-row>
</template>
<script lang="ts">

</script>
<style scoped>
.user-info-disp {
    margin: 50px 0 0 0;
    display: flex;
    justify-content: space-around;
}

.user-info-detail {
    text-align: start;
    margin: 0 0 20px 0;
}

.user-info-main {
    display: flex;
}

.user-nameid {
    display: flex;
    align-items: center;
}

.user-name {
    margin: 0 10px 0 20px;
    font: bold 20px "Microsoft-YaHei";
}

.user-info-detail-p {
    margin: 0 0 6px 0;
}

.followed {
    margin-top: 24px;
    /*hover */
    --el-button-hover-text-color: var(--el-color-info);
    --el-button-hover-bg-color: var(--el-color-info-light-9);
    --el-button-hover-border-color: var(--el-color-info-light-5);
    /*active */
    --el-button-active-text-color: var(--el-color-info);
    --el-button-active-bg-color: var(--el-color-info-light-9);
    --el-button-active-border-color: var(--el-color-info-light-5);
}
</style>
