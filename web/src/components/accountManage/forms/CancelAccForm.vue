<template>
    <div>
        <el-text>您确定要注销账户吗？</el-text>
    </div>
    <div>
        <el-text>账户资料将会永远消失！（真的很久！）</el-text>
    </div>
    <div class="cancel-confirm-form">
        <el-form label-position="top" size="large" ref="cancelAccFormRef" :model="cancelAccForm" :rules="cancelAccRules"
            label-width="120px">
            <el-form-item label="请输入“确认注销”以抹除账户" prop="confirm" class="cancel-items cancel-input">
                <el-input placeholder="确认注销" v-model="cancelAccForm.confirm"></el-input>
            </el-form-item>
        </el-form>
    </div>
    <el-button type="danger" :disabled="cancelAccForm.confirm !== '确认注销'" @click="handleCancel(cancelAccFormRef)">注销账户</el-button>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {POST, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {useRouter, useRoute} from 'vue-router'
const store = useStore()
const router = useRouter()
const route = useRoute()

interface CancelAccForm {
    confirm: string
} // 验证表单接口

const cancelAccForm = reactive<CancelAccForm>({
  confirm: ''
})
const cancelAccButton = reactive<any>({ // 注册按钮
  disabled: false,
  buttonText: '立即注销',
  duration: 2,
  timer: null
})
const validateConfirm = (rule: any, value: any, callback: any) => { // 验证密码
  if (value === '') {
    callback(new Error('请输入“确认注销”以抹除账户'))
  } else if (value !== '确认注销') {
    callback(new Error('请输入“确认注销”以抹除账户'))
  } else {
    callback()
  }
}

const cancelAccRules = reactive<FormRules>({
  confirm: [
    { required: true, validator: validateConfirm, trigger: 'blur' }
  ]
})
const handleCancel = async (formEl: FormInstance | undefined) => { // 提交表单
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // cancelAccButton.disabled = true
      // cancelAccButton.buttonText = '正在注销...'
      let isPosted = false
      // TODO 加入额外选项
      await SYNC_POST('/usr/eraseUser', {
        token: store.getters.getToken,
        hard_token: route.query.tadokoro
      }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          console.log(response.data.statusMsg)
          ElMessage({
            message: '注销成功',
            type: 'warning',
            duration: 2000
          })
          router.push('/')
        }else if (response.data.statusMsg === "Invalid hard token."){
            ElMessage.error('会话已过期，请重新验证')
            router.go(-1)
        }else {
          ElMessage.error('注销失败，请检查网络连接')
          console.log(response.data.statusMsg)
        }
        isPosted = true
      })
      cancelAccButton.timer && clearInterval(cancelAccButton.timer)
      cancelAccButton.timer = setInterval(() => {
        if (cancelAccButton.duration === 0) {
          cancelAccButton.disabled = false
          cancelAccButton.buttonText = '立即注销'
          cancelAccButton.duration = 2
          cancelAccButton.timer && clearInterval(cancelAccButton.timer)
          if (isPosted === false) {
            ElMessage.error('注销失败，请检查网络连接')
          }
        } else {
          cancelAccButton.duration--
          cancelAccButton.buttonText = '正在注销...'
        }
      }, 1000)
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}
const cancelAccFormRef = ref<FormInstance>()

</script>
<style scoped>
.cancel-confirm-form {
  display: flex;
  justify-content: center;
  margin: 100px 50px 0 0;
}
.cancel-confirm-form /deep/ .el-form-item__content {
  justify-content: center;
}
.cancel-input {
  width: 400px;
}
.cancel-input /deep/ .el-input__inner {
  text-align: center;
}
.cancel-items {
  margin: 0 0 30px 30px;
  align-items: center;
}
</style>
