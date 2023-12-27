<template>
    <div class="confirm">
        <h3>需要邮箱验证</h3>
        <div class="email-info-confirm-form" v-if="store.state.token!=='' && store.state.token!==undefined">
            <el-avatar class="user-avatar-preview" :size="50" :src="userInfo.avatar"></el-avatar>
            <el-text class="signed-text">已登录邮箱</el-text>
            <el-text class="signed-email">{{ userInfo.email }}</el-text>
        </div>
        <div class="email-confirm-form">
            <el-form ref="emailConfirmRef" :model="emailConfirm" :rules="emailChangeRules" label-width="80px" label-position="top" size="large">
                <el-form-item label="绑定邮箱地址" prop="email" class="email-items email-input"  v-if="store.state.token==='' || store.state.token===undefined">
                    <el-input v-model="emailConfirm.email" placeholder="请输入绑定邮箱地址"></el-input>
                </el-form-item>
                <el-form-item label="邮箱验证码" prop="emailCaptcha" class="email-items email-input">
                    <el-input placeholder="请输入验证码" v-model="emailConfirm.emailCaptcha">
                        <template #append>
                            <el-button type="primary" @click="sendEmailCaptcha" class="sendEmailCaptchaButton"
                                :disabled="emailCaptchaButton.disabled">{{ emailCaptchaButton.buttonText }}</el-button>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
            <div class="loginBottom">
                <div></div>
                <el-button type="primary" class="loginButton">验证</el-button>
                <div></div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { POST } from '@/scripts/Axios'
import router from '@/router'
import { useStore } from 'vuex'
import { checkPasswordRule, level } from '../../uiScripts/CheckPassword'
const store = useStore()
const userInfo = reactive(store.getters.getUserInfo)
const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式
interface ConfirmEmail {
  email: string
  emailCaptcha: string
} // 验证表单接口

const emailConfirm = reactive<ConfirmEmail>({
  email: userInfo.email,
  emailCaptcha: ''
})
const emailCaptchaButton = reactive<any>({ // 邮箱验证码按钮
  disabled: false,
  buttonText: '发送验证码',
  duration: 59,
  timer: null
})
const emailConfirmRef = ref<FormInstance>()

const sendEmailCaptcha = () => { // 发送邮箱验证码
  emailConfirmRef.value?.validateField('email', (valid) => {
    if (valid) {
      emailCaptchaButton.disabled = true
      emailCaptchaButton.buttonText = `${emailCaptchaButton.duration}秒后重新发送`
      emailCaptchaButton.timer && clearInterval(emailCaptchaButton.timer)
      emailCaptchaButton.timer = setInterval(() => {
        if (emailCaptchaButton.duration === 0) {
          emailCaptchaButton.disabled = false
          emailCaptchaButton.buttonText = '重新发送'
          emailCaptchaButton.duration = 59
          emailCaptchaButton.timer && clearInterval(emailCaptchaButton.timer)
        } else {
          emailCaptchaButton.duration--
          emailCaptchaButton.buttonText = `${emailCaptchaButton.duration}秒后重新发送`
        }
      }, 1000)
      POST('/usr/sendvcode', { email: emailConfirm.email }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          console.log(response.data.statusMsg)
        } else {
          console.log(response.data.statusMsg)
        }
      })
    } else {
      console.log('邮箱不能为空!')
    }
  })
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

const validateEmailCaptcha = (rule: any, value: any, callback: any) => { // 验证验证码
  if (value === '') {
    callback(new Error('验证码不能为空'))
  } else {
    callback()
  }
}

const emailChangeRules = reactive<FormRules<ConfirmEmail>>({ // 表单验证规则
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  emailCaptcha: [{ required: true, validator: validateEmailCaptcha, trigger: 'blur' }]
})
</script>
<style scoped>
.confirm {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
}

.email-confirm-form {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #fafafa;
    border-radius: 20px;
    box-shadow: 0 0 1px 0;
    width: 420px;
    padding: 30px 10px;
}

.email-confirm-form /deep/ .el-form-item__content {
    justify-content: center;
}

.email-info-confirm-form {
    display: flex;
    align-items: center;
    background-color: #ffffff;
    border-radius: 20px;
    box-shadow: 0 0 1px 0;
    width: 360px;
    padding: 30px 40px;
    margin: 20px 0;
}

.signed-text {
    margin: 0 0 0 10px;
}

.signed-email {
    margin: 0 10px;
    font-weight: bold;
}
.loginBottom {
    display: grid;
    align-items: center;
    grid-template-columns: 1fr 1fr 1fr;
}

.email-input {
  width: 300px;
}
.email-input /deep/ .el-input__inner {
  text-align: center;
}

</style>
