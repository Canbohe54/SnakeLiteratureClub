<template>
    <div class="info-change-form">
        <el-form label-width="80px" label-position="top">
            <el-form-item label="验证密码" class="info-items info-input">
                <el-input v-model="confirmPasswd" placeholder="请输入密码"></el-input>
            </el-form-item>
        </el-form>
    </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { POST } from '@/scripts/Axios'
import router from '@/router'
import { checkPasswordRule, level } from '../../uiScripts/CheckPassword'
interface ConfirmPasswd {
  passwd: string
}
const confirmPasswd = reactive<ConfirmPasswd>({
  passwd: ''
})
const confirmPasswdRef = ref<FormInstance>()

const validatePasswd = (rule: any, value: any, callback: any) => { // 验证密码
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else {
    const name = '空'
    const result = checkPasswordRule(value, name)
    if (result === '校验通过') {
      callback()
    } else {
      callback(new Error(result))
    }
    callback()
  }
}
const confirmPasswdRules = reactive<FormRules<ConfirmPasswd>>({ // 表单验证规则
  passwd: [{ required: true, validator: validatePasswd, trigger: 'blur' }]
})
</script>
<style scoped>
.info-change-form {
  display: flex;
  justify-content: center;
}
.info-change-form /deep/ .el-form-item__content {
  justify-content: center;
}
.info-input {
  width: 400px;
}
.info-input /deep/ .el-input__wrapper {
  box-shadow: 0 0 0 0px;
}
.info-input /deep/ .el-input__inner {
  text-align: center;
}
.info-input /deep/ .el-input__inner:focus {
  border-bottom: #a9abb2 1px solid;
}
.info-items {
  display: flex;
  align-items: center;
}
</style>
