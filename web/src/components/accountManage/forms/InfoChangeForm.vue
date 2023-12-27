<template>
  <div class="info-change-form">
    <el-form label-width="80px" label-position="right" size="large">
      <el-form-item label="用户头像" class="info-items user-avatar">
        <el-tooltip class="tooltip" effect="dark" content="点击上传" placement="top">
          <el-avatar class="user-avatar-preview" :size="150" :src="userInfo.avatar"
            @click="handleAvatarPreview"></el-avatar>
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
        <el-select v-model="userInfo.stuGrade" placeholder="请选择学生年级">
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
      <el-button type="primary" class="mt-4" :onclick="handleInfoChange">保存修改</el-button>
    </el-form>

    <el-dialog v-model="avatarDialogVisible" title="上传头像" width="30%">
      <el-form>
      <el-form-item label="图片链接：" label-width=100px>
        <el-input v-model="userInfo.avatar"  />
      </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="avatarDialogVisible = false">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
// do not use same name with ref!!!
import { useStore } from 'vuex'
const store = useStore()
const userInfo = reactive({
  avatar: store.getters.getUserInfo.avatar,
  name: store.getters.getUserInfo.name,
  unit: store.getters.getUserInfo.unit,
  identity: store.getters.getUserInfo.identity,
  introduction: store.getters.getUserInfo.introduction,
  stuGrade: store.getters.getUserInfo.stuGrade
})
// 缓存，点击提交后，通过后端修改，返回数据到store修改
const regExpEmail = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])+$/ // 邮箱正则表达式

const userInfoInit = ref('')

const handleAvatarPreview = () => {
  avatarDialogVisible.value = true
  console.log('preview avatar')
}

const avatarDialogVisible = ref(false)

const handleInfoChange = () => {
  if (userInfo.name === '') {
    ElMessage({
      message: '用户名不能为空',
      type: 'error'
    })
    return
  }
  if (userInfo.unit === '') {
    ElMessage({
      message: '单位不能为空',
      type: 'error'
    })
    return
  }
  if (userInfo.name === store.getters.getUserInfo.name && userInfo.unit === store.getters.getUserInfo.unit && userInfo.introduction === store.getters.getUserInfo.introduction && userInfo.avatar === store.getters.getUserInfo.avatar && userInfo.stuGrade === store.getters.getUserInfo.stuGrade) {
    ElMessage({
      message: '您未修改任何信息',
      type: 'error'
    })
    return
  }
  const data = {
    name: userInfo.name,
    unit: userInfo.unit,
    introduction: userInfo.introduction,
    avatar: userInfo.avatar,
    attr: userInfo.stuGrade === '' ? '' : JSON.stringify({
      grade: userInfo.stuGrade
    })
  }
}

</script>
<style scoped>
.info-change-form {
  display: flex;
  justify-content: center;
  margin: 100px 0 0 0;
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
