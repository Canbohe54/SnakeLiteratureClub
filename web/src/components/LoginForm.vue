<template>
      <el-form ref="loginRuleFormRef" :model="loginRuleForm" :rules="loginRules" label-width="60px" size="large">
        <el-form-item label="邮箱" prop="email">
            <el-input placeholder="请输入邮箱" v-model="loginRuleForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="passwd">
            <el-input type="password" placeholder="请输入密码" show-password v-model="loginRuleForm.passwd" />
        </el-form-item>
    </el-form>
    <div class="loginBottom">
        <div>
          <router-link to="/forget" class="forget-password">忘记密码？</router-link>
        </div>
        <el-button type="primary" @click="onSubmit(loginRuleFormRef)" class="loginButton" :disabled="loginButton.disabled">{{ loginButton.buttonText }}</el-button>
        <div>
          <router-link to="/register" class="loginLink">还没有账户？点击注册</router-link>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { checkPasswordRule } from './uiScripts/CheckPassword'
import { POST } from '@/scripts/Axios'
import router from '@/router'
import { useStore } from "vuex";
// do not use same name with ref!!!

const store = useStore()

const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式

interface LoginRuleForm {
    email: string
    passwd: string
} // 验证表单接口

const loginRuleForm = reactive<LoginRuleForm>({
  email: '',
  passwd: ''
}) // 验证表单

const loginButton = reactive<any>({ // 登录按钮
  disabled: false,
  buttonText: '立即登录',
  duration: 2,
  timer: null
})

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
      loginButton.disabled = true
      loginButton.buttonText = '正在登录...'
      let isPosted = false
      POST('/usr/login', { email: loginRuleForm.email, password: loginRuleForm.passwd }, async (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          store.commit('setToken', response.data.token)
          store.commit('setUserInfo', response.data.userInfo)
          ElMessage.success('登录成功')
          console.log(document.cookie)
          console.log('push')
          await router.push('/')
          location.reload()
        } else {
          if (response.data.statusMsg === 'Nonexistent'){
            ElMessage.error('用户不存在')
          }else if(response.data.statusMsg === 'Password error.'){
            ElMessage.error('用户名或密码错误')
          } else {
            ElMessage.error(response.data.statusMsg)
            console.log(response.data.statusMsg)
          }
        }
        isPosted = true
      })
      loginButton.timer && clearInterval(loginButton.timer)
      loginButton.timer = setInterval(() => {
        if (loginButton.duration === 0) {
          loginButton.disabled = false
          loginButton.buttonText = '立即登录'
          loginButton.duration = 2
          loginButton.timer && clearInterval(loginButton.timer)
          if (!isPosted) {
            ElMessage.error('登录超时，请检查网络连接')
          }
        } else {
          loginButton.duration--
          loginButton.buttonText = '正在登录...'
        }
      }, 1000)
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
    width: 60%;
    height: 125%;
    justify-self: center;
}
.forget-password {
  text-decoration: none;
  color: #606266;
  font: 14px "Microsoft YaHei";
  display: flex;
  justify-content: flex-begin;
  margin-left: 50px;
}
.forget-password:hover {
    color: #5999fe;
}

.loginLink {
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    display: flex;
    justify-content: flex-end;
}

.loginLink:hover {
    color: #5999fe;
}
</style>
