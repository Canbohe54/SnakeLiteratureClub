<template>
<!--  <div class="passwd-change-form">-->
<!--    <el-form ref="passwdChangeFormRef" :model="passwdChangeForm" :rules="passwdChangeRules" label-width="120px"-->
<!--      label-position="right" size="large">-->
<!--      <el-form-item label="新密码" prop="passwd" class="passwd-items passwd-input">-->
<!--        <el-input v-model="passwdChangeForm.passwd" placeholder="请输入新密码" show-password></el-input>-->
<!--      </el-form-item>-->
<!--      <el-form-item v-if="passwdChangeForm.passwd !== '' && passwdChangeForm.passwd !== undefined" label="" algin="center"-->
<!--        justify-content="start" style="height: 25px">-->
<!--        &lt;!&ndash; 展示长度条 &ndash;&gt;-->
<!--        <div class="paschange-bar" v-if="passwdChangeForm.passwd !== '' && passwdChangeForm.passwd !== undefined"-->
<!--          :style="{ background: barColor, width: width + '%' }">-->
<!--          &lt;!&ndash; 展示文字 &ndash;&gt;-->
<!--          <div class="paschange-strength" :style="{ color: barColor }"-->
<!--            v-if="passwdChangeForm.passwd !== '' && passwdChangeForm.passwd !== undefined">-->
<!--            {{ strength }}-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="确认密码" prop="passwd2" class="passwd-items passwd-input">-->
<!--        <el-input v-model="passwdChangeForm.passwd2" placeholder="请再次输入密码"></el-input>-->
<!--      </el-form-item>-->
<!--      <el-button type="primary" class="mt-4" @click="onSubmit">提交修改</el-button>-->
<!--    </el-form>-->
<!--  </div>-->
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { POST } from '@/scripts/Axios'
import router from '@/router'
// import { checkPasswordRule, level } from '../../uiScripts/CheckPassword'
import store from '@/store'
import { useRoute } from 'vue-router'
import { pa } from 'element-plus/es/locale'

const route = useRoute()

interface PasswdChangeForm {
  passwd: string
  passwd2: string
} // 验证表单接口

const passwdChangeForm = reactive<PasswdChangeForm>({
  passwd: '',
  passwd2: ''
})

// const validatePasswd = (rule: any, value: any, callback: any) => { // 验证密码
//   if (value === '') {
//     callback(new Error('密码不能为空'))
//   } else {
//     const name = '空'
//     const result = checkPasswordRule(value, name)
//     if (result === '校验通过') {
//       callback()
//     } else {
//       callback(new Error(result))
//     }
//     if (passwdChangeForm.passwd2 !== '') {
//       if (!passwdChangeFormRef.value) return
//       passwdChangeFormRef.value.validateField('passwd2', () => null)
//     }
//     callback()
//   }
// }

const validatePasswd2 = (rule: any, value: any, callback: any) => { // 验证验证密码
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwdChangeForm.passwd) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// const passwdChangeRules = reactive<FormRules>({
//   passwd: [
//     { required: true, validator: validatePasswd, trigger: 'blur' }
//   ],
//   passwd2: [
//     { required: true, validator: validatePasswd2, trigger: 'blur' }
//   ]
// })

const passwdChangeFormRef = ref<FormInstance>()

const onSubmit = () => { // 提交修改
  passwdChangeFormRef.value?.validate((valid) => {
    console.log(route.query.kouji)
    console.log(route.query.tadokoro)
    console.log(passwdChangeForm.passwd)
    if (valid) {
      POST('/usr/changePasswd', {
        user_id: route.query.kouji,
        new_password: passwdChangeForm.passwd,
        hard_token: route.query.tadokoro
      }, (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
          ElMessage.success('密码修改成功')
          store.commit('clear')
          router.push('/login')
        } else {
          console.log(response.data.statusMsg)
          if (response.data.statusMsg === "Invalid hard token."){
            ElMessage.error('会话已过期，请重新验证')
            router.go(-1)
          }else{
            ElMessage.error('密码修改失败')
          }

        }
      })
    } else {
      console.log('error submit!!')
      return false
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
// watch(
//   () => passwdChangeForm.passwd,
//   (newVal) => {
//     if (newVal !== '') {
//       const res: string = level(newVal)
//       if (res === '非常弱') {
//         barColor.value = 'red'
//         width.value = '20'
//         strength.value = '非常弱'
//       } else if (res === '弱') {
//         barColor.value = '#ee795c'
//         width.value = '40'
//         strength.value = '弱'
//       } else if (res === '一般') {
//         barColor.value = 'orange'
//         width.value = '60'
//         strength.value = '一般'
//       } else if (res === '强') {
//         barColor.value = 'green'
//         width.value = '80'
//         strength.value = '强'
//       } else if (res === '非常强') {
//         barColor.value = '#1B8EF8'
//         width.value = '100'
//         strength.value = '非常强'
//       }
//     }
//   }
// )
</script>
<style scoped>
.passwd-change-form {
  display: flex;
  justify-content: center;
  margin: 100px 50px;
}

.passwd-change-form /deep/ .el-form-item__content {}

.passwd-input {
  width: 400px;
}

.passwd-input /deep/ .el-input__inner {
  text-align: center;
}

.passwd-items {
  display: flex;
  align-items: center;
}

.paschange-strength {
  font-size: 13px;
  color: #271E25;
  position: relative;
  top: 5px;
  left: 0;
  transition: 0.5s all ease;
}

.paschange-bar {
  /* width: 400px; */
  height: 5px;
  background: red;
  transition: 0.5s all ease;
  max-width: 274px;
  margin: 2px 0 5px 5px;
  position: absolute;
  border-radius: 2px;
}</style>
