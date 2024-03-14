<template>
    <div class="regPage">
        <section class="loginContainer">
            <header>登录</header>
            <el-form :model="loginForm" label-width="auto" class="regForm">
                <el-form-item label="账号">
                    <el-input v-model="loginForm.userid" placeholder="请输入账号或绑定手机号"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
                </el-form-item>
            </el-form>
            <el-row>
                <el-col :sm="8" class="hidden-xs-and-down">
                    <!--忘记密码，以后再做-->
                </el-col>
                <el-col :sm="8" :xs="24" style="text-align: center;">
                    <el-button type="primary" >立即登录</el-button>
                </el-col>
                <el-col :sm="8" :xs="24" class="switchContainer">
                    <router-link to="/register" class="switchLogin">还没有账户？点击注册</router-link>
                </el-col>
            </el-row>
        </section>
    </div>
</template>

<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { ref, reactive } from 'vue';
import { POST } from '@/scripts/Axios';
import { useRouter } from 'vue-router';

const router = useRouter()

const loginForm = reactive({
    userid: '',
    password: ''
})

</script>
<style lang="scss">
.regPage {
    min-height: 80vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

.loginContainer {
    position: relative;
    max-width: 500px;
    width: 100%;
    background: #fafafa;
    padding: 25px 10px;
    margin: 10px;
    border-radius: 8px;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
}

.loginContainer header {
    font-size: 1.5rem;
    color: #333;
    font-weight: 400;
    text-align: center;
}

.loginContainer .regForm {
    margin-top: 30px;
    margin-right: 10px;
}

.switchContainer {
    text-align: center;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
}

.switchLogin {
  text-decoration: none;
  color: #606266;
  font: 14px "Microsoft YaHei";
}

@media screen and (max-width: 767px){
    .switchLogin {
        margin-top: 10px;
    }
}
</style>