<template>
    <div class="user-info-disp">
        <div class="user-info-main">
            <el-avatar class="mr-3" :size="80" src="https://avatars.githubusercontent.com/u/43968296"></el-avatar>
            <div class="user-nameid">
                <el-text class="user-name">Canbohe54</el-text>
                <el-tag type="warning">专家</el-tag>
            </div>

        </div>
        <div>
            <el-button type="info" plain class="followed">已关注</el-button>
        </div>
    </div>
</template>
<script lang="ts">

</script>
<style scoped>
.user-info-disp {
    margin: 50px 0 20px 0;
    display: flex;
    justify-content: space-around;
    align-items: center;
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
.followed {
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
