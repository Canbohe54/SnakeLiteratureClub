<template>
<div class="email-change-form">
    <el-form ref="emailChangeFormRef" :model="emailChangeForm" :rules="emailChangeRules" label-width="120px" label-position="right" size="large">
    <el-form-item label="原绑定邮箱" prop="oriEmail" class="email-items email-input email-org">
      <el-input v-model="oriEmail" placeholder="原邮箱地址" :disabled="true"></el-input>
    </el-form-item>
    <el-form-item label="新邮箱地址" prop="newEmail" class="email-items email-input">
      <el-input v-model="emailChangeForm.newEmail" placeholder="请输入新邮箱地址"></el-input>
    </el-form-item>
    <el-form-item label="新邮箱验证码" prop="emailCaptcha" class="email-items email-input">
      <el-input placeholder="请输入验证码" v-model="emailChangeForm.emailCaptcha">
        <template #append>
          <el-button type="primary" @click="sendEmailCaptcha" class="sendEmailCaptchaButton" :disabled="emailCaptchaButton.disabled">{{ emailCaptchaButton.buttonText }}</el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-button type="primary" class="mt-4" @click="onSubmit">提交修改</el-button>
  </el-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { POST } from '@/scripts/Axios'
import { useStore } from 'vuex'
import router from '@/router'
const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式
const store = useStore()
const oriEmail = ref(store.getters.getUserInfo.email) // 原邮箱地址
interface EmailChangeForm {
  newEmail: string
  emailCaptcha: string
} // 验证表单接口

const emailChangeForm = reactive<EmailChangeForm>({
  newEmail: '',
  emailCaptcha: ''
})
const emailCaptchaButton = reactive<any>({ // 邮箱验证码按钮
  disabled: false,
  buttonText: '发送验证码',
  duration: 59,
  timer: null
})
const emailChangeFormRef = ref<FormInstance>()

const sendEmailCaptcha = () => { // 发送邮箱验证码
  emailChangeFormRef.value?.validateField('newEmail', (valid) => {
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
      POST('/usr/sendvcode', { email: emailChangeForm.newEmail }, (response) => {
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

const emailChangeRules = reactive<FormRules<EmailChangeForm>>({ // 表单验证规则
  newEmail: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  emailCaptcha: [{ required: true, validator: validateEmailCaptcha, trigger: 'blur' }]
})

const onSubmit = async (formEl: FormInstance | undefined) => { // 提交表单
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
      POST('/usr/changeEmail', { email: emailChangeForm.newEmail, vcode: emailChangeForm.emailCaptcha }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          ElMessage.success('邮箱修改成功')
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
<style scoped>
.email-change-form {
  display: flex;
  justify-content: center;
  margin: 150px 50px;
}
.email-change-form /deep/ .el-form-item__content {
  justify-content: center;
}
.email-input {
  width: 400px;
}
.email-input /deep/ .el-input__inner {
  text-align: center;
}
.email-items {
  display: flex;
  align-items: center;
}

.email-org /deep/ .el-input__wrapper {
  box-shadow: 0 0 0 0px;
}
</style>
