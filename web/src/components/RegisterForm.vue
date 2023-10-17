<template>
  <el-form ref="regRuleFormRef" :model="regRuleForm" :rules="regRules" label-width="80px" size="large">
    <el-form-item label="邮箱" prop="email">
      <el-input placeholder="请输入邮箱" v-model="regRuleForm.email" />
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
    <el-form-item>
      <el-checkbox>我已阅读并同意《用户协议》</el-checkbox>
    </el-form-item>
  </el-form>
  <div class="regBottom">
    <div></div>
    <el-button type="primary" @click="onSubmit(regRuleFormRef)" class="regButton">立即注册</el-button>
    <router-link to="/Login" class="regLink">已经有账户了？点击登录</router-link>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { checkPasswordRule, level } from './uiScripts/CheckPassword'
// do not use same name with ref!!!

const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式

interface RegRuleForm {
  email: string
  passwd: string
  passwd2: string
  name: string
  identity: string
  unit: string
} // 验证表单接口

const regRuleForm = reactive<RegRuleForm>({
  email: '',
  passwd: '',
  passwd2: '',
  name: '',
  identity: '',
  unit: ''
}) // 验证表单
const regRuleFormRef = ref<FormInstance>()

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

const regRules = reactive<FormRules<RegRuleForm>>({ // 表单验证规则
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  passwd: [{ required: true, validator: validatePasswd, trigger: 'blur' }],
  passwd2: [{ required: true, validator: validatePasswd2, trigger: 'blur' }],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  identity: [{ required: true, message: '请选择身份', trigger: 'blur' }],
  unit: [{ required: true, message: '单位不能为空', trigger: 'blur' }]
})

const onSubmit = async (formEl: FormInstance | undefined) => { // 提交表单
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
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
  justify-self: end;
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
