<template>
    <el-form ref="loginRuleFormRef" :model="loginRuleForm" :rules="loginRules" label-width="80px" size="large">
        <el-form-item label="邮箱" prop="email">
            <el-input placeholder="请输入邮箱" v-model="loginRuleForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="passwd">
            <el-input tpye="password" placeholder="请输入密码" show-password v-model="loginRuleForm.passwd" />
        </el-form-item>
    </el-form>
    <div class="loginBottom">
        <div></div>
        <el-button type="primary" @click="onSubmit(loginRuleFormRef)" class="loginButton">立即登录</el-button>
        <router-link to="/register" class="loginLink">还没有账户？点击注册</router-link>
    </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { checkPasswordRule } from './uiScripts/CheckPassword'
import { POST } from '@/scripts/Axios'
import LoginForm from '@/components/LoginForm.vue'
// do not use same name with ref!!!

const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式

interface LoginRuleForm {
    email: string
    passwd: string
} // 验证表单接口

const loginRuleForm = reactive<LoginRuleForm>({
  email: '',
  passwd: ''
}) // 验证表单
const loginRuleFormRef = ref<FormInstance>()

const validatePasswd = (rule: any, value: any, callback: any) => { // 验证密码
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else {
    const name = ''
    const result = checkPasswordRule(value, name)
    if (result === '校验通过') {
      callback()
    } else {
      callback(new Error(result))
    }
    callback()
  }
}

const validateEmail = (rule: any, value: any, callback: any) => { // 验证邮箱
  if (value === '') {
    callback(new Error('邮箱不能为空'))
  } else {
    const isEmail = regExpEmail.test(value)
    if (!isEmail) {
      callback(new Error('请输入正确的邮箱地址'))
    } else {
      callback()
    }
  }
}

const loginRules = reactive<FormRules<LoginRuleForm>>({ // 表单验证规则
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  passwd: [{ required: true, validator: validatePasswd, trigger: 'blur' }]
})

const onSubmit = async (formEl: FormInstance | undefined) => { // 提交表单
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
      POST('/usr/login', { email: loginRuleForm.email, password: loginRuleForm.passwd }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          // TODO: store token
          console.log(response.data.token)
        } else {
          console.log(response.data.statusMsg)
        }
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}

</script>
<style>
.loginBottom {
    display: grid;
    align-items: center;
    grid-template-columns: 1fr 1fr 1fr;
}

.loginButton {
    width: 50%;
    height: 125%;
    justify-self: center;
}

.loginLink {
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    display: grid;
    justify-self: end;
}

.loginLink:hover {
    color: #5999fe;
}
</style>
