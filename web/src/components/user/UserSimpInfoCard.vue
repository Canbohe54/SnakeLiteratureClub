<template>
    <div class="user-simp-info">
        <el-avatar :size="64"></el-avatar>
        <div class="user-simp-info-disp">
            <div>用户名
                <el-tag>角色</el-tag>
            </div>
        </div>
        <div class="user-simp-option">
            <router-link class="user-simp-item" to="/user/info">个人中心</router-link>
            <router-link class="user-simp-item" to="/user/info">用户设置</router-link>
        </div>
        <div class="user-simp-option logout-option">
            <router-link class="user-simp-item"  to="/user/info">退出登录</router-link>
        </div>
    </div>
    <div class="user-simp-info">
        <div class="user-simp-option">
            <router-link class="user-simp-item"  to="/login">点击登录</router-link>
            <router-link class="user-simp-item"  to="/register">点击注册</router-link>
        </div>
    </div>
</template>
<script lang="ts" setup>
import router from '@/router';


</script>
<style scoped>

.user-simp-info {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    margin: 10px;
}

.user-simp-info-disp {
    margin: 10px 0;
}

.user-simp-option {
    width: 100%;
}

.user-simp-item {
    display: block;
    width: auto;
    text-decoration: none;
    text-align: center;
    color: #606266;
    padding: 10px 0;
    margin: 5px 0;
    border-radius: 8px;
}

.user-simp-item:hover {
    background-color: #f5f5f5;
}

.logout-option {
    border-top: 1px solid #f0f0f0;
}

</style>