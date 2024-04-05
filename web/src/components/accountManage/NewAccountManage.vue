<template>
    <el-row>
        <el-col :lg="16" :md="20" :sm="24" style="margin: auto;">
            <el-card class="account-manage-card">
                <el-text
                    style="font: bold 20px 'Microsoft YaHei'; margin-bottom: 20px; display: inline-block;">个人信息</el-text>
                <el-form :model="changeForm" label-width="auto" ref="changeFormRef" :rules="changeRules"
                    class="regForm">
                    <el-form-item label="用户ID" class="info-input">
                        <el-tooltip effect="dark" content="用户ID是您除手机号外登录的唯一凭证，请妥善保管" placement="top" trigger="hover">
                            <el-input v-model="store.getters.getUserInfo.id" disabled></el-input>
                        </el-tooltip>
                    </el-form-item>
                    <el-form-item label="头像">
                        <el-avatar :size="64" @click="showAvatarSelection" class="changeAvatar"><img
                                :src="'avatars/' + changeForm.avatar.avatar + '.png'" /></el-avatar>
                    </el-form-item>
                    <el-form-item label="姓名" prop="username" class="info-input">
                        <el-input v-model="changeForm.username" placeholder="请输入姓名"></el-input>
                    </el-form-item>

                    <el-form-item label="身份" prop="identity">
                        <el-tooltip effect="dark" content="身份不能更改，如需修改身份请重新注册" placement="top" trigger="click">
                            <el-radio-group v-model="changeForm.identity" disabled>
                                <el-radio label="CONTRIBUTOR" value="CONTRIBUTOR">学生作者</el-radio>
                                <el-radio label="TEACHER" value="TEACHER">教师</el-radio>
                                <el-radio label="ADMINISTRATOR" value="ADMINISTRATOR">学校管理员</el-radio>
                                <el-radio label="EXPERT" value="EXPERT">专家</el-radio>
                                <el-radio label="HUNTER" value="HUNTER">报刊专员</el-radio>
                                <el-radio label="VOLUNTEER" value="VOLUNTEER">审稿志愿者</el-radio>
                            </el-radio-group>
                        </el-tooltip>

                    </el-form-item>
                    <el-form-item label="单位" prop="organization" class="info-input">
                        <el-input v-model="changeForm.organization" placeholder="请输入学校/单位"></el-input>
                    </el-form-item>
                    <el-form-item :label="pageAttr" prop="attribute" class="info-input">
                        <el-input v-model="changeForm.attribute" :placeholder="'请输入' + pageAttr"></el-input>
                    </el-form-item>
                    <el-form-item label="个人简介" class="info-introduction">
                        <el-input type="textarea" v-model="changeForm.information" placeholder="请输入信息"></el-input>
                    </el-form-item>
                    <el-form-item label="手机号" class="info-input">
                        <el-input v-model="changeForm.phone" placeholder="可绑定手机号"></el-input>
                    </el-form-item>
                </el-form>
                <el-row>
                    <el-col :sm="8" class="hidden-xs-and-down">
                    </el-col>
                    <el-col :sm="8" :xs="24" style="text-align: center;">
                        <el-button type="primary" @click="onChangeInfo(changeFormRef)" :disabled="changeButton.disabled">{{
            changeButton.buttonText }}</el-button>
                    </el-col>
                    <el-col :sm="8" class="hidden-xs-and-down">
                    </el-col>
                </el-row>
                <AvatarSelection :visible="avatarSelectionVisible" @close="handleAvatarSelectionClose"
                    @selection="handleAvatarSelection" />
            </el-card>
        </el-col>
    </el-row>
    <el-row>
        <el-col :lg="16" :md="20" style="margin: auto;">
            <el-card class="account-manage-card">
                <el-text
                    style="font: bold 20px 'Microsoft YaHei'; margin-bottom: 20px; display: inline-block;">修改密码</el-text>
                <el-form :model="passwdForm" label-width="auto" ref="passwdFormRef" :rules="passwdRules"
                    class="regForm">
                    <el-form-item label="原密码" prop="oldPassword">
                        <el-input type="password" v-model="passwdForm.oldPassword" placeholder="请输入原密码"></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                        <el-input type="password" v-model="passwdForm.newPassword" placeholder="请输入新密码"></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword">
                        <el-input type="password" v-model="passwdForm.confirmPassword" placeholder="请再次输入密码"></el-input>
                    </el-form-item>
                </el-form>
                <el-row>
                    <el-col :sm="8" class="hidden-xs-and-down">
                    </el-col>
                    <el-col :sm="8" :xs="24" style="text-align: center;">
                        <el-button type="primary" @click="onChangePasswd(passwdFormRef)" :disabled="passwdButton.disabled">{{
            passwdButton.buttonText }}</el-button>
                    </el-col>
                    <el-col :sm="8" class="hidden-xs-and-down">
                    </el-col>
                </el-row>
            </el-card>
        </el-col>
    </el-row>
</template>

<script lang="ts" setup>
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import AvatarSelection from '../user/AvatarSelection.vue';
import { SnachResponse } from '@/scripts/types/ResponseObject';
import { getCookie } from '@/scripts/cookie';
import { ChineseLanguageMap } from '@/scripts/common/ChineseLanguageMap';
import {BASE_URL} from "@/scripts/config/BaseURL";

const router = useRouter()
const store = useStore()

interface ChangeForm {
    username: string;
    avatar: { avatar: string, color: string };
    identity: string;
    organization: string;
    attribute: string;
    information: string;
    phone: string;
}

const changeFormRef = ref<FormInstance>()
const changeForm = reactive<ChangeForm>({
    username: store.getters.getUserInfo.name,
    avatar: { avatar: '1', color: '#e9f3e2' },
    identity: store.getters.getUserInfo.identity,
    organization: store.getters.getUserInfo.organization,
    attribute: '',
    information: store.getters.getUserInfo.information,
    phone: store.getters.getUserInfo.phone
});

const pageAttr = ref('年级');

onMounted(() => {
    handleIdentityChange(changeForm.identity)
    let avatarJson = store.getters.getUserInfo.pictureUrl
    if (avatarJson) {
        changeForm.avatar = JSON.parse(avatarJson)
    }
    let attrsJson :Object = store.getters.getUserInfo.attrs
    if (attrsJson) {
        changeForm.attribute = attrsJson[ChineseLanguageMap.get(pageAttr.value) as string]
    }
})

const avatarSelectionVisible = ref(false)

function showAvatarSelection() {
    avatarSelectionVisible.value = true
}

const handleAvatarSelectionClose = () => {
    avatarSelectionVisible.value = false
}

const handleAvatarSelection = (selection: any) => {
    console.log(selection.avatar)
    changeForm.avatar = selection
    $('.changeAvatar').css('background-color', selection.color)
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

const changeRules = reactive<FormRules<ChangeForm>>({
    username: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    organization: [
        { required: true, message: '请输入学校/单位', trigger: 'blur' }
    ],
    attribute: [
        { required: true, message: '请输入' + pageAttr.value, trigger: 'blur' }
    ]
})

const changeButton = reactive<any>({ // 修改信息按钮
    disabled: false,
    buttonText: '保存修改',
    duration: 2,
    timer: null
})

console.log(store.getters.getUserInfo.attrs)

const onChangeInfo = async (formEl: FormInstance | undefined) => { // 提交表单
    if (formEl) {
        formEl.validate(async (valid: boolean) => {
            if (valid) {
                const requestData = {
                    token: getCookie('token'),
                    id: store.getters.getUserInfo.id,
                    name: changeForm.username,
                    organization: changeForm.organization,
                    information: changeForm.information,
                    phone: changeForm.phone,
                    pictureUrl: JSON.stringify(changeForm.avatar),
                    attrs: `{ "${ChineseLanguageMap.get(pageAttr.value)}": "${changeForm.attribute}" }`
                }
                $.post({
                    url: BASE_URL + '/usr/updateUserInfo',
                    async: false,
                    enctype: 'multipart/form-data',
                    data: requestData,
                    success: (data: SnachResponse<boolean>) => {
                        console.log(data)
                        if(data.code===2001){
                            ElMessage.success('修改成功')
                            router.push('/user')
                        }else{
                            if(data.message==='Invalid token.'){
                                // TODO: 调用token失效函数
                            }else{
                                ElMessage.error('修改失败，请稍后再试')
                            }
                        }
                    }
                })
            } else {
                return false
            }
        })
    }
}


interface PasswdForm {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}

const passwdFormRef = ref<FormInstance>()

const passwdForm = reactive<PasswdForm>({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const confirmPassword = (rule: any, value: any, callback: any) => { // 验证验证密码
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== passwdForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

const passwdRules = reactive<FormRules<PasswdForm>>({
    oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: confirmPassword, trigger: 'blur' }
    ]
})

const passwdButton = reactive<any>({ // 修改密码按钮
    disabled: false,
    buttonText: '修改密码',
    duration: 2,
    timer: null
})

const onChangePasswd = async (formEl: FormInstance | undefined) => { // 提交表单
    if (formEl) {
        formEl.validate(async (valid: boolean) => {
            if (valid) {
                const requestData = {
                    token: getCookie('token'),
                    oldPassword: passwdForm.oldPassword,
                    newPassword: passwdForm.newPassword
                }
                $.post({
                    url: BASE_URL + '/usr/updateUserPassword',
                    async: false,
                    enctype: 'multipart/form-data',
                    data: requestData,
                    success: (data: SnachResponse<boolean>) => {
                        console.log(data)
                        // TODO: success @Canbohe54
                        if(data.code===2001){
                            ElMessage.success('修改成功')
                            store.commit('clear')
                            router.push('/login')
                        }else{
                            if(data.message==='Wrong old password.'){
                                ElMessage.error('原密码错误')
                            }else if(data.message==='Invalid token.'){
                                // TODO: 调用token失效函数
                            }else{
                                ElMessage.error('修改失败，请稍后再试')
                            }
                        }
                    }
                })
            } else {
                return false
            }
        })
    }
}


</script>

<style scoped>
.changeAvatar {
    cursor: pointer;
    background-color: #e9f3e2;
}

.changeAvatar:hover {
    border: 2px solid #409eff;
}

.account-manage-container {
    display: flex;
    justify-content: center;
    flex-direction: column;
}

.account-manage-card {
    margin: 20px;
    border-radius: 10px;
}

.info-input /deep/ .el-input__wrapper {
    box-shadow: 0 0 0 0px;
}

.info-input /deep/ .el-input__inner:focus {
    border-bottom: #a9abb2 1px solid;
}


.info-introduction /deep/ .el-textarea__inner {
    box-shadow: 0 0 0 0px;
}

.info-introduction /deep/ .el-textarea__inner:focus {
    box-shadow: 0 0 0 1px;
}
</style>
