<template>
  <div class="confirm">
    <h3>需要验证身份</h3>
    <div class="info-confirm-form">
      <el-avatar class="user-avatar-preview" :size="50" :src="userInfo.avatar"></el-avatar>
      <el-text class="signed-text">已登录账户</el-text>
      <el-tag :type="userTagType"  disable-transitions>{{ userInfo.name }} · {{ userInfo.identity }}</el-tag>
    </div>
    <div class="passwd-confirm-form">
      <el-form ref="confirmPasswdRef" :model="confirmPasswd" :rules="confirmPasswdRules" label-width="80px" label-position="top" size="large" >
        <el-form-item label="请输入密码" class="confirm-passwd" prop="passwd">
          <el-input v-model="confirmPasswd.passwd" show-password></el-input>
        </el-form-item>
      </el-form>
      <div class="loginBottom">
        <div></div>
        <el-button type="primary" class="loginButton" @click="onSubmit(confirmPasswdRef)">验证</el-button>
        <div>
          <router-link to="/forget/confirm" class="forget-password-confirm">忘记密码？</router-link>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { POST } from '@/scripts/Axios'
import router from '@/router'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { checkPasswordRule, level } from '../../uiScripts/CheckPassword'
const store = useStore()
const route = useRoute()
interface ConfirmPasswd {
  passwd: string
}
const confirmPasswd = reactive<ConfirmPasswd>({
  passwd: ''
})
const confirmPasswdRef = ref<FormInstance>()

const avatarUrl = ref('https://avatars.githubusercontent.com/u/43968296')

const identityTagType = (userIdentity: string) => {
  switch (userIdentity) {
    case '专家':
      return 'warning'
    case '学生':
      return 'success'
    case '管理员':
      return 'warning'
    default:
      return 'info'
  }
}
const userInfo = reactive(store.getters.getUserInfo)
const userTagType = ref(identityTagType(userInfo.identity))

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

const onSubmit = async (formEl: FormInstance | undefined) => { // 提交表单
  console.log(formEl)
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    
    if (valid) {
      POST('/usr/verifyPasswd', { 
        token: store.getters.getToken,
        password: confirmPasswd.passwd
      }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'pass') {
          ElMessage({
            message: '密码验证成功',
            type: 'success',
            duration: 2000
          })
          console.log(route.path)
          if (route.path.split('/')[2] === 'password') {
            router.push({path:'/account/password/change',query:{kouji: store.getters.getUserInfo.id, tadokoro:response.data.hard_token}})
          } else if (route.path.split('/')[2] === 'cancel') {
            router.push({path:'/account/cancel/confirm',query:{kouji: store.getters.getUserInfo.id, tadokoro:response.data.hard_token}})
          } else if (route.path.split('/')[2] === 'email'){
            router.push({path:'/account/email/change',query:{kouji: store.getters.getUserInfo.id, tadokoro:response.data.hard_token}})
          }
            
        } else {
          ElMessage.error('密码验证失败，请检查网络连接')
          console.log(response.data.statusMsg)
        }
      })
    } else {
      console.log('error submit!!')
      return false
    }
  })
}
</script>
<style scoped>
.confirm {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.passwd-confirm-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  border-radius: 20px;
  box-shadow: 0 0 1px 0;
  width: 360px;
  padding: 30px 10px;
}

.passwd-confirm-form /deep/ .el-form-item__content {
  justify-content: center;
}

.info-confirm-form {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border-radius: 20px;
  box-shadow: 0 0 1px 0;
  width: 300px;
  padding: 30px 40px;
  margin: 20px 0;
}
.signed-text {
  margin: 0 10px;
}

.confirm-passwd {
  width: 300px;
}

.forget-password-confirm {
  text-decoration: none;
  color: #606266;
  font: 14px "Microsoft YaHei";
  display: flex;
  justify-content: flex-end;
  margin-left: 30px;
}

.forget-password-confirm:hover {
  color: #5999fe;
}

.loginBottom {
  display: grid;
  align-items: center;
  grid-template-columns: 1fr 1fr 1fr;
}</style>
