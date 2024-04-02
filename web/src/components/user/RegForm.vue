<template>
    <div class="regPage">
        <section class="regContainer">
            <header>注册</header>
            <el-form :model="regForm" label-width="auto" ref="regFormRef" :rules="regRules" class="regForm">
                <el-form-item label="头像">
                    <el-avatar :size="64" @click="showAvatarSelection" class="regAvatar"><img :src="'avatars/'+regForm.avatar.avatar+'.png'"/></el-avatar>
                </el-form-item>
                <el-form-item label="姓名" prop="username">
                    <el-input v-model="regForm.username" placeholder="请输入姓名"></el-input>
                </el-form-item>

                <el-form-item label="身份" prop="identity">
                    <el-radio-group v-model="regForm.identity" :change="handleIdentityChange(regForm.identity)">
                        <el-radio label="CONTRIBUTOR" value="CONTRIBUTOR">学生作者</el-radio>
                        <el-radio label="TEACHER" value="TEACHER">教师</el-radio>
                        <el-radio label="ADMINISTRATOR" value="ADMINISTRATOR">学校管理员</el-radio>
                        <el-radio label="EXPERT" value="EXPERT">专家</el-radio>
                        <el-radio label="HUNTER" value="HUNTER">报刊专员</el-radio>
                        <el-radio label="VOLUNTEER" value="VOLUNTEER">审稿志愿者</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="单位" prop="organization">
                    <el-input v-model="regForm.organization" placeholder="请输入学校/单位"></el-input>
                </el-form-item>
                <el-form-item :label="pageAttr" prop="attribute">
                    <el-input v-model="regForm.attribute" :placeholder="'请输入' + pageAttr"></el-input>
                </el-form-item>
                <el-form-item label="个人简介">
                    <el-input type="textarea" v-model="regForm.information" placeholder="请输入信息"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="regForm.phone" placeholder="可绑定手机号"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input type="password" v-model="regForm.password" placeholder="请输入密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input type="password" v-model="regForm.confirmPassword" placeholder="请再次输入密码"></el-input>
                </el-form-item>
            </el-form>
            <el-row>
                <el-col :sm="8" class="hidden-xs-and-down">
                </el-col>
                <el-col :sm="8" :xs="24" style="text-align: center;">
                    <el-button type="primary" @click="onSubmit(regFormRef)" :disabled="regButton.disabled">{{ regButton.buttonText }}</el-button>
                </el-col>
                <el-col :sm="8" :xs="24" class="switchContainer">
                    <router-link to="/login" class="switchLogin">已经有账户了？点击登录</router-link>
                </el-col>
            </el-row>
        </section>
        <AvatarSelection :visible="avatarSelectionVisible" @close="handleAvatarSelectionClose" @selection="handleAvatarSelection" />
        <el-dialog 
        v-model="regSuccessDialogVisible" title="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注册成功！" style="max-width: 500px; border-radius: 10px;"
        @close="handleRegSuccessClose" :close-on-click-modal="false">
            <div style="margin-bottom: 10px;"><el-text>已为您生成唯一用户ID，是用于登录的凭证，请妥善保管</el-text></div>
            <el-form label-width="auto">
                <el-form-item label="用户ID">
                    <el-input v-model="gettedNewUserId" readonly></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="regForm.password" type="password" readonly id="newUserPasswd">
                        <template #append>
                            <el-button type="primary" @click="changePasswdHidden"><el-icon><View v-if="isPasswordHidden" /><Hide v-else /></el-icon></el-button>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
            <el-button @click="copyUserIDPasswd">复制到剪切板</el-button>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { ref, reactive } from 'vue';
import { POST } from '@/scripts/Axios';
import { useRouter } from 'vue-router';
import AvatarSelection from './AvatarSelection.vue';
import {ChineseLanguageMap} from "@/scripts/common/ChineseLanguageMap";
import { View, Hide } from "@element-plus/icons-vue";

const router = useRouter()

interface RegForm {
    username: string;
    avatar: {avatar: string, color: string};
    identity: string;
    organization: string;
    attribute: string;
    information: string;
    password: string;
    confirmPassword: string;
    phone: string;
}

const regFormRef = ref<FormInstance>()
const regForm = reactive<RegForm>({
    username: '',
    avatar: {avatar:'1', color:'#e9f3e2'},
    identity: 'CONTRIBUTOR',
    organization: '',
    attribute: '',
    information: '',
    password: '',
    confirmPassword: '',
    phone: ''
});

const pageAttr = ref('年级');

const gettedNewUserId = ref('')

const avatarSelectionVisible = ref(false)
const regSuccessDialogVisible = ref(false)
const isPasswordHidden = ref(true)

const changePasswdHidden = () => {
    if (isPasswordHidden.value) {
        isPasswordHidden.value = false
        $('#newUserPasswd').attr('type', 'text')
    } else {
        isPasswordHidden.value = true
        $('#newUserPasswd').attr('type', 'password')
    }
}

const copyUserIDPasswd = () => {
    navigator.clipboard.writeText("用户ID："+gettedNewUserId.value+"\n密码："+regForm.password)
    ElMessage.success('用户ID及密码已复制到剪切板')
}

function showAvatarSelection() {
    avatarSelectionVisible.value = true
}

const handleAvatarSelectionClose = () => {
    avatarSelectionVisible.value = false
}

const handleAvatarSelection = (selection: any) => {
    console.log(selection.avatar)
    regForm.avatar = selection
    $('.regAvatar').css('background-color', selection.color)
}

function handleIdentityChange(val: string) {
    switch (val) {
        case 'CONTRIBUTOR':
            pageAttr.value = '年级';
            break;
        case 'TEACHER':
            pageAttr.value = '职称';
            break;
        case 'ADMINISTRATOR':
            pageAttr.value = '职务';
            break;
        case 'EXPERT':
            pageAttr.value = '职称';
            break;
        case 'HUNTER':
            pageAttr.value = '职位';
            break;
        case 'VOLUNTEER':
            pageAttr.value = '职业';
            break;
    }
}

const confirmPassword = (rule: any, value: any, callback: any) => { // 验证验证密码
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== regForm.password) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

const regRules = reactive<FormRules<RegForm>>({
    username: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    identity: [
        { required: true, message: '请选择身份', trigger: 'change' }
    ],
    organization: [
        { required: true, message: '请输入学校/单位', trigger: 'blur' }
    ],
    attribute: [
        { required: true, message: '请输入' + pageAttr.value, trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: confirmPassword, trigger: 'blur' }
    ]
})

const regButton = reactive<any>({ // 注册按钮
  disabled: false,
  buttonText: '立即注册',
  duration: 2,
  timer: null
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
      POST('/usr/reg', {
        name: regForm.username,
        phone: regForm.phone,
        password: regForm.password,
        identity: regForm.identity,
        introduction: regForm.information,
        organization: regForm.organization,
        pictureUrl: `{ "avatar": "${regForm.avatar.avatar}", "color": "${regForm.avatar.color}" }`,
        attrs: `{ "${ChineseLanguageMap.get(pageAttr.value)}": "${regForm.attribute}" }`
      }, (response) => {
        console.log(response)
        if (response.status === 200 && response.data.code === 2001) {
          ElMessage({
            message: '注册成功',
            type: 'success',
            duration: 2000
          })
          gettedNewUserId.value = response.data.data.newUserId
          regSuccessDialogVisible.value = true

        } else if (response.status === 200 && response.data.message === 'Email already exists.') { //
          ElMessage.error('邮箱已存在')
        } else if (response.status === 200 && response.data.message === 'Wrong Verifying Code.') { // 验证码错误
          ElMessage.error('验证码错误')
        } else {
          ElMessage.error('注册失败，请稍后再试')
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

const handleRegSuccessClose = () => {
    regSuccessDialogVisible.value = false
    router.push('/login')
}

</script>
<style lang="scss">
.regPage {
    min-height: 80vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

.regContainer {
    position: relative;
    max-width: 700px;
    width: 100%;
    background: #fafafa;
    padding: 25px 10px;
    margin: 10px;
    border-radius: 8px;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
}

.regContainer header {
    font-size: 1.5rem;
    color: #333;
    font-weight: 400;
    text-align: center;
}

.regContainer .regForm {
    margin-top: 30px;
    margin-right: 10px;
}

.regAvatar {
    cursor: pointer;
    background-color: #e9f3e2;
}

.regAvatar:hover {
    border: 2px solid #409eff;
}

.switchContainer {
    text-align: center;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
}

.switchLogin {
  text-decoration: none;
  color: #606266;
  font: 14px "Microsoft YaHei";
}

@media screen and (max-width: 767px){
    .switchLogin {
        margin-top: 10px;
    }
}
</style>
