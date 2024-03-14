<template>
    <el-row class="resp-nav">
        <el-col :lg="3" :md="4" :sm="2" :xs="2" class="logo-title">
            <div>
                <router-link to="/">
                    <span class="nav-logo"></span>
                    <span class="nav-title-link hidden-sm-and-down">蛇拾文学社</span>
                </router-link>
            </div>
        </el-col>
    </el-row>

</template>
<style>
.resp-nav {
    display: flex;
    justify-content: space-between;
}

.logo-title {
    display: flex;
    align-items: center;
    height: 60px;
}

.nav-logo {
    display: flex;
    float: left;
    height: 32px;
    width: 32px;
    margin: 0.5px 10px 0 16px;
    background: url("../assets/logo.png") no-repeat center center;
    background-size: contain;
}

.nav-title-link {
    display: flex;
    float: left;
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    margin-top: 10px;
}
</style>
