<template>
  <el-form ref="regRuleFormRef" :model="regRuleForm" :rules="regRules" label-width="80px" size="large">
    <el-form-item label="邮箱" prop="email">
      <el-input placeholder="请输入邮箱" v-model="regRuleForm.email" />
    </el-form-item>
    <el-form-item label="验证码" prop="emailCaptcha">
      <el-input placeholder="请输入验证码" v-model="regRuleForm.emailCaptcha">
        <template #append>
          <el-button type="primary" @click="sendEmailCaptcha" class="sendEmailCaptchaButton" :disabled="emailCaptchaButton.disabled">{{ emailCaptchaButton.buttonText }}</el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="密码" prop="passwd">
      <el-input tpye="password" placeholder="请输入密码" show-password v-model="regRuleForm.passwd" />
    </el-form-item>
    <el-form-item v-if="regRuleForm.passwd !== '' && regRuleForm.passwd !== undefined" label="" algin="center" style="height: 25px">
      <!-- 展示长度条 -->
      <div class="bar" v-if="regRuleForm.passwd !== '' && regRuleForm.passwd !== undefined"
        :style="{ background: barColor, width: width + '%' }">
        <!-- 展示文字 -->
        <div class="strength" :style="{ color: barColor }" v-if="regRuleForm.passwd !== '' && regRuleForm.passwd !== undefined">
          {{ strength }}
        </div>
      </div>
    </el-form-item>
    <el-form-item label="确认密码" prop="passwd2">
      <el-input type="password" placeholder="请再次输入密码" show-password v-model="regRuleForm.passwd2" />
    </el-form-item>
    <el-form-item label="姓名" prop="name">
      <el-input placeholder="请输入姓名" v-model="regRuleForm.name" />
    </el-form-item>
    <el-form-item label="身份" prop="identity">
      <el-radio-group v-model="regRuleForm.identity" class="ml-4">
        <el-radio label="学生" size="large">学生</el-radio>
        <el-radio label="专家" size="large">专家</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="单位" prop="unit">
      <el-input placeholder="请输入所在单位" v-model="regRuleForm.unit" />
    </el-form-item>
    <el-form-item label="年级" v-if="regRuleForm.identity === '学生'">
      <el-select v-model="stuGrade" placeholder="请选择学生年级">
        <el-option label="一年级" value="一年级" />
        <el-option label="二年级" value="二年级" />
        <el-option label="三年级" value="三年级" />
        <el-option label="四年级" value="四年级" />
        <el-option label="五年级" value="五年级" />
        <el-option label="六年级" value="六年级" />
        <el-option label="七年级" value="七年级" />
        <el-option label="八年级" value="八年级" />
        <el-option label="九年级" value="九年级" />
        <el-option label="高一" value="高一" />
        <el-option label="高二" value="高二" />
        <el-option label="高三" value="高三" />
      </el-select>
    </el-form-item>
    <el-form-item prop="checkUserAgreement">
      <el-checkbox v-model="regRuleForm.checkUserAgreement" :checked="regRuleForm.checkUserAgreement">我已阅读并同意《用户协议》</el-checkbox>
    </el-form-item>
  </el-form>
  <div class="regBottom">
    <div></div>
    <el-button type="primary" @click="onSubmit(regRuleFormRef)" class="regButton" :disabled="regButton.disabled">{{ regButton.buttonText }}</el-button>
    <router-link to="/login" class="regLink">已经有账户了？点击登录</router-link>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { checkPasswordRule, level } from './uiScripts/CheckPassword'
import { POST } from '@/scripts/Axios'
import router from '@/router'
// do not use same name with ref!!!

const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式

interface RegRuleForm {
  email: string
  emailCaptcha: string
  passwd: string
  passwd2: string
  name: string
  identity: string
  unit: string
  checkUserAgreement: boolean
} // 验证表单接口

const regRuleForm = reactive<RegRuleForm>({
  email: '',
  emailCaptcha: '',
  passwd: '',
  passwd2: '',
  name: '',
  identity: '',
  unit: '',
  checkUserAgreement: false
}) // 验证表单
const stuGrade = ref('') // 学生年级

const emailCaptchaButton = reactive<any>({ // 邮箱验证码按钮
  disabled: false,
  buttonText: '发送验证码',
  duration: 59,
  timer: null
})
const regButton = reactive<any>({ // 注册按钮
  disabled: false,
  buttonText: '立即注册',
  duration: 2,
  timer: null
})

const regRuleFormRef = ref<FormInstance>()

const sendEmailCaptcha = () => { // 发送邮箱验证码
  regRuleFormRef.value?.validateField('email', (valid) => {
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
      POST('/usr/sendvcode', { email: regRuleForm.email }, (response) => {
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

const validatePasswd = (rule: any, value: any, callback: any) => { // 验证密码
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else {
    let name = ''
    if (regRuleForm.name === '') {
      name = '空'
    } else {
      name = regRuleForm.name
    }
    const result = checkPasswordRule(value, name)
    if (result === '校验通过') {
      callback()
    } else {
      callback(new Error(result))
    }
    if (regRuleForm.passwd2 !== '') {
      if (!regRuleFormRef.value) return
      regRuleFormRef.value.validateField('passwd2', () => null)
    }
    callback()
  }
}

const validatePasswd2 = (rule: any, value: any, callback: any) => { // 验证验证密码
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== regRuleForm.passwd) {
    callback(new Error('两次输入密码不一致'))
  } else {
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

const validateEmailCaptcha = (rule: any, value: any, callback: any) => { // 验证验证码
  if (value === '') {
    callback(new Error('验证码不能为空'))
  } else {
    callback()
  }
}

const validateCheckUserAgreement = (rule: any, value: any, callback: any) => { // 验证用户协议
  if (value === false) {
    callback(new Error('请阅读并同意《用户协议》'))
  } else {
    callback()
  }
}

const regRules = reactive<FormRules<RegRuleForm>>({ // 表单验证规则
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  emailCaptcha: [{ required: true, validator: validateEmailCaptcha, trigger: 'blur' }],
  passwd: [{ required: true, validator: validatePasswd, trigger: 'blur' }],
  passwd2: [{ required: true, validator: validatePasswd2, trigger: 'blur' }],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  identity: [{ required: true, message: '请选择身份', trigger: 'blur' }],
  unit: [{ required: true, message: '单位不能为空', trigger: 'blur' }],
  checkUserAgreement: [{ required: true, validator: validateCheckUserAgreement, trigger: 'change' }]
})

const onSubmit = async (formEl: FormInstance | undefined) => { // 提交表单
  // 不要忘记传学生年级
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      regButton.disabled = true
      regButton.buttonText = '正在注册...'
      let isPosted = false
      // TODO 加入额外选项
      POST('/usr/reg', { name: regRuleForm.name, email: regRuleForm.email, password: regRuleForm.passwd, organization: regRuleForm.unit, attr: { grade: stuGrade }, group: regRuleForm.identity, vCode: regRuleForm.emailCaptcha }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          console.log(response.data.statusMsg)
          ElMessage({
            message: '注册成功',
            type: 'success',
            duration: 2000
          })
          router.push('/login')
        } else if (response.status === 200 && response.data.statusMsg === 'Email already exists.') { // 邮箱已存在
          ElMessage.error('邮箱已存在')
          console.log(response.data.statusMsg)
        } else if (response.status === 200 && response.data.statusMsg === 'Wrong Verifying Code.') { // 验证码错误
          ElMessage.error('验证码错误')
          console.log(response.data.statusMsg)
        } else {
          ElMessage.error('注册失败，请检查网络连接')
          console.log(response.data.statusMsg)
        }
        isPosted = true
      })
      regButton.timer && clearInterval(regButton.timer)
      regButton.timer = setInterval(() => {
        if (regButton.duration === 0) {
          regButton.disabled = false
          regButton.buttonText = '立即注册'
          regButton.duration = 2
          regButton.timer && clearInterval(regButton.timer)
          if (isPosted === false) {
            ElMessage.error('注册失败，请检查网络连接')
          }
        } else {
          regButton.duration--
          regButton.buttonText = '正在注册...'
        }
      }, 1000)
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}

// 密码强度显示
// 强度条颜色
const barColor = ref('')
// 强度条长度
const width = ref('')
// 强度条说明
const strength = ref('')
// 监听注册页面的新密码变化状态，来改变密码强弱显示
watch(
  () => regRuleForm.passwd,
  (newVal) => {
    if (newVal !== '') {
      const res: string = level(newVal)
      if (res === '非常弱') {
        barColor.value = 'red'
        width.value = '20'
        strength.value = '非常弱'
      } else if (res === '弱') {
        barColor.value = '#ee795c'
        width.value = '40'
        strength.value = '弱'
      } else if (res === '一般') {
        barColor.value = 'orange'
        width.value = '60'
        strength.value = '一般'
      } else if (res === '强') {
        barColor.value = 'green'
        width.value = '80'
        strength.value = '强'
      } else if (res === '非常强') {
        barColor.value = '#1B8EF8'
        width.value = '100'
        strength.value = '非常强'
      }
    }
  }
)

</script>
<style>
.regBottom {
  display: grid;
  align-items: center;
  grid-template-columns: 1fr 1fr 1fr;
}

.regButton {
  width: 50%;
  height: 125%;
  justify-self: center;
}

.regLink {
  text-decoration: none;
  color: #606266;
  font: 14px "Microsoft YaHei";
  display: grid;
  justify-content: end;
  margin-right: 4px;
}

.regLink:hover {
  color: #5999fe;
}

.strength {
  font-size: 13px;
  color: #271E25;
  position: relative;
  top: 5px;
  left: 0;
  transition: 0.5s all ease;
}

.bar {
  /* width: 400px; */
  height: 5px;
  background: red;
  transition: 0.5s all ease;
  max-width: 510px;
  margin: 2px 0 5px 5px;
  position: absolute;
  border-radius: 2px;
}
</style>
