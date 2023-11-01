<template>
  <div class="info-change-form">
    <el-form label-width="80px" label-position="right" size="large">
      <el-form-item label="用户头像" class="info-items user-avatar">
        <el-tooltip class="tooltip" effect="dark" content="点击上传" placement="top">
          <el-avatar class="user-avatar-preview" :size="150" :src="avatarUrl" @click="handleAvatarPreview"></el-avatar>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="姓名" class="info-items info-input">
        <el-input v-model="userInfo.name" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="身份" class="info-items info-input">
        <el-tooltip effect="dark" content="身份不能更改，如需修改身份请重新注册" placement="top" trigger="click">
          <el-radio-group v-model="userInfo.identity" class="ml-4" disabled>
            <el-radio label="学生">学生</el-radio>
            <el-radio label="专家">专家</el-radio>
          </el-radio-group>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="单位" class="info-items info-input">
        <el-input v-model="userInfo.unit" placeholder="请输入单位"></el-input>
      </el-form-item>
      <el-form-item label="年级" v-if="userInfo.identity === '学生'" class="info-items info-grade">
        <el-select v-model="stuGrade" placeholder="请选择学生年级">
          <el-option label="一年级" value="一年级" class="stu-grade-option" />
          <el-option label="二年级" value="二年级" class="stu-grade-option" />
          <el-option label="三年级" value="三年级" class="stu-grade-option" />
          <el-option label="四年级" value="四年级" class="stu-grade-option" />
          <el-option label="五年级" value="五年级" class="stu-grade-option" />
          <el-option label="六年级" value="六年级" class="stu-grade-option" />
          <el-option label="七年级" value="七年级" class="stu-grade-option" />
          <el-option label="八年级" value="八年级" class="stu-grade-option" />
          <el-option label="九年级" value="九年级" class="stu-grade-option" />
          <el-option label="高一" value="高一" class="stu-grade-option" />
          <el-option label="高二" value="高二" class="stu-grade-option" />
          <el-option label="高三" value="高三" class="stu-grade-option" />
        </el-select>
      </el-form-item>
      <el-form-item label="个人简介" class="info-introduction">
        <el-input type="textarea" v-model="userInfo.introduction" placeholder="请输入个人简介" class="info-textarea"></el-input>
      </el-form-item>
      <el-button type="primary" class="mt-4">保存修改</el-button>
    </el-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
// do not use same name with ref!!!
const avatarUrl = ref('https://avatars.githubusercontent.com/u/43968296')
const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式
const userInfo = reactive({
  name: 'Canbohe54',
  unit: 'South China Normal University',
  identity: '学生',
  introduction: '这个人很懒，什么都没有留下~'
})
const stuGrade = ref('九年级') // 学生年级
const userInfoInit = ref('')

const handleAvatarPreview = () => {
  console.log('preview avatar')
}
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

.info-grade /deep/ .el-input__wrapper {
  box-shadow: 0 0 0 0px;
}

.info-grade /deep/ .el-input__inner {
  text-align: center;
}

.info-introduction /deep/ .el-textarea__inner {
  box-shadow: 0 0 0 0px;
  text-align: center;
}

.info-introduction /deep/ .el-textarea__inner:focus {
  box-shadow: 0 0 0 1px;
}

.info-textarea {
  margin-left: 20px;
}

.info-items {
  display: flex;
  align-items: center;
}

.user-avatar-preview {
  cursor: pointer;
}

.user-avatar-preview:hover {
  opacity: 0.8;
}

.tooltip:hover::after {
  content: "点击上传";
  position: absolute;
  /*设置悬停文字的方向*/
  top: 100%;
  left: 50%;
  /*设置悬停文字的位置*/
  transform: translateX(-50%);
  border-width: 5px;
  border-style: solid;
  border-color: transparent transparent #000 transparent;
}

.stu-grade-option {
  display: flex;
  justify-content: center;
}
</style>
