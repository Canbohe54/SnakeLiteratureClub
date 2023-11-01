<template>
    <div class="email-change-form">
        <el-form label-width="120px" label-position="top" size="large">
          <div class="ori-email-box">
            <el-form-item label="已绑定邮箱" prop="oriEmail" class="email-items email-input email-org">
          <el-input v-model="oriEmail" placeholder="邮箱地址" :disabled="true"></el-input>
        </el-form-item>
          </div>
          <el-text class="email-warning">邮箱是您账户身份的重要凭证，当账号发生异常时，将会通过邮箱第一时间通知您</el-text>
        <el-button type="primary" class="mt-4" @click="toConfirm">修改邮箱</el-button>
      </el-form>
      </div>
</template>
<script lang="ts" setup>
import router from '@/router'
import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
const store = useStore()
const oriEmail = ref(store.state.userInfo.email) // 原邮箱地址
const toConfirm = () => {
  router.push('/account/email/verify')
}
</script>
<style scoped>
.email-change-form {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 150px 50px;
}
.ori-email-box {
  display: flex;
  justify-content: center;
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

.email-org /deep/ .el-input__wrapper {
  box-shadow: 0 0 0 0px;
}
.email-warning {
  display: block;
  font-size: 11px;
  margin-bottom: 20px;
}
</style>
