<template xmlns="http://www.w3.org/1999/html">
  <el-row>
    <el-col :md="3" class="hidden-sm-and-down"></el-col>
    <el-col :md="18" :sm="24">
      <el-card class="upload-file-card">
        <el-form :rules="editRules" ref="editFormRef" :model="articleDetail" class="edit-form">

          <el-form-item prop="title" class="title-form-item">
            <div class="title-head"><span>文章标题</span></div>
            <el-input

              class="editor-header"
              v-model="articleDetail.title"
              maxlength="50"
              show-word-limit
              :autosize="{ minRows: 1, maxRows: 3}"
              type="textarea"
              placeholder="请输入标题（建议30字以内）"

            />
          </el-form-item>
          <el-form-item prop="description">
            <div class="description-head"><span>文章描述</span></div>
            <el-input
              class="editor-description"
              v-model="articleDetail.description"
              maxlength="200"
              show-word-limit
              :autosize="{ minRows: 4, maxRows: 10}"
              type="textarea"
              placeholder="请输入描述（建议100字以内）"
            />
          </el-form-item>
        </el-form>

        <div class="upload-head"><span>上传文章</span></div>
        <!--        </el-tooltip>-->
        <el-tooltip
          class="box-item"
          effect="light"
          content="限制1个.docx或.txt文件，上传新内容将覆盖旧内容"
          placement="top"
        >
          <div>
            <el-upload
              ref="upload"
              class="upload-doc"
              drag
              action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
              :limit="1"
              :on-change="changeInputBox"
              :auto-upload=false
              :show-file-list="false"
              list-type="text"
            >
              <el-icon class="el-icon--upload">
                <upload-filled/>
              </el-icon>
              <div class="el-upload__text">
                将文件拖拽至此区域或<em>点击上传</em>
              </div>

            </el-upload>
          </div>
        </el-tooltip>
        <div class="upload-file-name-list">
          <el-tag class="upload-file-name" v-if="Object.keys(articleDetail.raw).length != 0" closable effect="light"
                  @close="handleFileRemove">
            {{ articleDetail.raw.name }}
          </el-tag>
        </div>

      </el-card>

      <!--      <el-dialog v-model="dialogManager.dialogVisible">-->
      <!--        <img :src="dialogImageUrl" alt="Preview Image">-->
      <!--      </el-dialog>-->
      <el-card class="more-option-card">
        <el-collapse v-model="activeCollapse">
          <el-collapse-item class="more-option" title="更多设置" name="more_option">

            <div class="more-option-head"><span>投稿方式</span></div>
            <div class="contribute-way-container">
              <el-radio-group v-model="contributeManager.contributeWay" class="contribute-way-radio"
                              @change="handleContributeWayChange">
                <el-radio label="PUBLIC" value="PUBLIC">公开投稿</el-radio>
                <el-radio label="PUBLISHED" value="PUBLISHED">指定投稿</el-radio>
              </el-radio-group>
              <!--              <el-input v-if="contributeManager.contributeWay == 'PUBLISHED'" v-model="contributeManager.contributeTo" placeholder="请输入收稿方" />-->
              <el-select
                class="contribute-to-selection"
                v-if="contributeManager.contributeWay == 'PUBLISHED'"
                v-model="contributeManager.contributeTo"
                filterable
                remote
                reserve-keyword
                placeholder="请输入收稿方"
                no-data-text="暂无匹配的收稿方"
                no-match-text="暂无匹配的收稿方"
                default-first-option
                remote-show-suffix
                :remote-method="getExpertAndHunterByName"
                :loading="contributeManager.loading"
                style="width: 240px"
              >
                <el-option
                  v-for="item in contributeManager.options"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                  <span style="float: left">{{ item.name }}</span>
                  <span
                    style="float: right;color: var(--el-text-color-secondary);font-size: 13px;"
                  >{{ item.organization }}</span
                  >
                </el-option>
                <template #loading>
                  <svg class="circular" viewBox="0 0 50 50">
                    <circle class="path" cx="25" cy="25" r="20" fill="none"/>
                  </svg>
                </template>
              </el-select>
            </div>
            <div class="more-option-head" v-if="articleDetail.audited_by === ''"><span>审核方式</span></div>
            <div class="contribute-way-container" v-if="articleDetail.audited_by === ''">
              <el-radio-group v-model="contributeManager.sameAuditor" class="contribute-way-radio"
                              @change="handleAuditWayChange">
                <el-radio label="true" value="true">请相同审核员提建议</el-radio>
                <el-radio label="false" value="false">随机进行审核</el-radio>
              </el-radio-group>
            </div>
            <div class="more-option-head"><span>基本信息</span></div>
            <el-form :model="articleDetail" ref="basicInfoFormRef">
              <el-form-item label="作者名称" prop="authorName">
                <el-input v-model="articleDetail.authorName" placeholder="请输入作者姓名"></el-input>
              </el-form-item>
              <el-form-item label="作者学校" prop="authorOrganization">
                <el-input v-model="articleDetail.authorOrganization" placeholder="请输入作者所在的学校"></el-input>
              </el-form-item>
              <el-form-item label="作者年级" prop="authorGrade">
                <el-input v-model="articleDetail.authorGrade" placeholder="请输入作者所在的年级"></el-input>
              </el-form-item>
              <el-form-item label="指导老师" prop="mentor">
                <el-input v-model="articleDetail.mentor" placeholder="请输入指导老师姓名"></el-input>
              </el-form-item>
            </el-form>

            <div class="more-option-head"><span>文章标签</span></div>
            <SearchFilter ref="SearchFilterRef" @change="searchFilterChange"/>

          </el-collapse-item>
        </el-collapse>
      </el-card>
      <div class="button-container">
        <ArticlePreview class="preview_file" :articleRaw="articleDetail.raw"
                        :previewType="previewType" :disabled="articleDetail.raw.size == 0"/>
        <el-button class="3" :type="saveBtnType" @click="save" :disabled="saveBtnText === '已保存'">{{
            saveBtnText
          }}
        </el-button>
        <el-button class="3" type="success" @click="handleReleaseClicked">发布</el-button>
      </div>
      <!--      <el-button type="danger" @click="delArticleDialogVisible=true">删除文章</el-button>-->
      <el-dialog
        draggable
        v-model="delArticleDialogVisible"
        title="删除文章"
        width="30%"
      >
        <span>确定删除文章？</span>
        <template #footer>
      <span class="dialog-footer">
        <el-button @click="delArticleDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleDelArticleClicked">
          删除
        </el-button>
      </span>
        </template>
      </el-dialog>
    </el-col>
    <el-col :md="3" class="hidden-sm-and-down"></el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import type {FormRules, UploadFile, UploadInstance} from 'element-plus'
import {ElMessage, FormInstance} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {useRoute} from 'vue-router'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import SearchFilter from '@/components/search/SearchFilter.vue'
import router from '@/router'
import mammoth from 'mammoth'
import {UploadFilled} from '@element-plus/icons-vue'
import {base64ToFile, generateImageName} from '@/scripts/ImageUtil'
import {errorCallback, errorMessage} from "@/scripts/ErrorCallBack";
import {acceptFileType} from "@/scripts/common/AcceptFileType";
import ArticlePreview from '@/components/article/ArticlePreview.vue'
import axios from "axios";

const upload = ref<UploadInstance>()
const store = useStore()
const route = useRoute()
const SearchFilterRef = ref()
const saveBtnType = ref('success')
const saveBtnText = ref('保存')
const delArticleDialogVisible = ref(false)
let imageFileList = ref<UploadFile[]>([])
const dialogImageUrl = ref('')
const previewType = ref("info")
const dialogManager = reactive({
  txtContainerVisible: false,
  docxDialogVisible: false,
  dialogVisible: false,
  txtContainerText: ''
})
const contributeManager = reactive({
  contributeWay: 'PUBLIC',
  contributeTo: '',
  loading: false,
  options: [],
  isContributor: false,
  sameAuditor: true,
})

const activeCollapse = ref(['more_option'])
const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  textBy: '',
  title: '',
  description: '',
  status: 'ROUGH',
  attr: '{}',
  raw: {},
  file_type: '',
  received_by: '',
  mentor: '',
  authorName: '',
  authorOrganization: '',
  authorGrade: '',
  audited_by: '',
})
const editFormRef = ref<FormInstance>()
const basicInfoFormRef = ref<FormInstance>()
const editRules = reactive<FormRules>({
  title: [
    {required: true, message: '请输入标题', trigger: 'blur'},
  ],
  description: [
    {required: true, message: '请输入文章描述', trigger: 'blur'},
  ],
})
watch(articleDetail, () => {
  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
})
const searchFilterChange = () => {
  articleDetail.tags = SearchFilterRef.value.filterSelection
}
const handleContributeWayChange = () => {

}

const handleAuditWayChange = () => {

}
// 有article_id时初始化ArticleDetail
const getArticleDetail = async () => {
  if (route.query.id === '' || route.query.id === undefined) {
    return
  }
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      for (const dataKey in response.data.data.article) {
        if (dataKey == 'raw') {
          continue
        }
        articleDetail[dataKey] = response.data.data.article[dataKey]
      }
      SearchFilterRef.value.loadSelection(articleDetail.tags)
      if (articleDetail.received_by !== '') {
        contributeManager.contributeWay = 'PUBLISHED'
        contributeManager.contributeTo = articleDetail.received_by
      }
      await getRaw(articleDetail.id)
    } else {
      errorCallback(response)
    }
  })
}

async function getRaw(articleId: String) {
  axios({
    url: '/article/getArticleFileById',
    method: 'GET',
    headers: {'Content-Type': 'multipart/form-data'},
    params: {article_id: articleId},
    responseType: 'arraybuffer'

  }).then(response => {
    const blob = new Blob([response.data], {type: articleDetail.file_type})
    articleDetail.raw = new File([blob], articleDetail.title, {type: articleDetail.file_type})
    if (articleDetail.raw.size == 0) {
      previewType.value = 'info'
    } else {
      previewType.value = 'primary'
    }
  }).catch(error => {
    console.error(error);
  });
}

const handleReleaseClicked = () => {
  editFormRef.value?.validate(async (valid) => {
    if (!valid) {
      ElMessage({
        showClose: true,
        message: '请填写文章标题和文章描述',
        type: 'error'
      })
      return false
    }
    release()
  })
}
// 保存草稿
const save = async () => {

  let param = new FormData()
  param.append("raw_file", articleDetail.raw)
  param.append("token", store.getters.getToken)
  param.append("id", articleDetail.id)
  param.append("text", articleDetail.text)
  param.append("textBy", '')
  param.append("title", articleDetail.title)
  param.append("description", articleDetail.description)
  param.append("status", 'ROUGH')
  param.append("attr", `{"tags":${articleDetail.attr}}`)
  param.append("fileType", articleDetail.file_type)
  if (contributeManager.contributeWay == 'PUBLISHED') {
    console.log(contributeManager.contributeTo)
    param.append("receivedBy", contributeManager.contributeTo)
  }
  if (contributeManager.sameAuditor) {
    param.append('auditedBy', articleDetail.audited_by)
  }
  await SYNC_POST('/contributor/contribute', param, async (response) => {
    if (response.status === 200 && response.data.message === 'Success.') {
      articleDetail.id = response.data.data.id
      ElMessage({
        showClose: true,
        message: '草稿已保存',
        type: 'info'
      })
      saveBtnType.value = 'info'
      saveBtnText.value = '已保存'
    } else {
      errorCallback(response)
    }
  })
}
// 发布文章
const release = async () => {
  let param = new FormData();

  param.append("raw_file", articleDetail.raw)
  param.append("token", store.getters.getToken)
  param.append("id", articleDetail.id)
  param.append("text", articleDetail.text)
  param.append("textBy", '')
  param.append("title", articleDetail.title)
  param.append("description", articleDetail.description)
  param.append("status", 'SUBMITTED')
  param.append("attr", `{"tags":${articleDetail.attr}}`)
  param.append("fileType", articleDetail.file_type)
  if (contributeManager.contributeWay == 'PUBLISHED') {
    param.append("receivedBy", contributeManager.contributeTo)
  }
  if (contributeManager.sameAuditor) {
    param.append('auditedBy', articleDetail.audited_by)
  }


  await SYNC_POST('/contributor/contribute', param, async (response) => {
      if (response.status === 200 && response.data.message === 'Success.') {
        console.log('Release successfully!')
        ElMessage({
          showClose: true,
          message: '已成功发布文章!',
          type: 'success'
        })
        location.href = '/#/user/' + store.getters.getUserInfo.id
      } else {
        errorCallback(response)
      }
    }
  )
}
const analyzeTXT = (file: any) => {
  const fileReader = new FileReader()
  fileReader.onload = async (e) => {
    articleDetail.text = e.target?.result as string
  }
  fileReader.readAsText(file.raw)
}
const analyzeDOCX = (file: any) => {
  const fileReader = new FileReader()
  fileReader.onload = async (event) => {
    const arrayBuffer = event.target?.result as ArrayBuffer
    mammoth.convertToHtml({arrayBuffer: arrayBuffer})
      .then(async function (result) {
        let html = result.value; // The generated HTML
        let match = html.match(/<img(.|\n)*?\/>/mg)

        //提取src="后的base64图片
        const base64List = (match?.map((item: any) => {
          return item.toString().match(/src=".*"/mg)[0].toString().slice(5, -1)
        })) as Array<string>

        imageFileList.value = base64List.map((item: any, index: any) => {
          let file: File = base64ToFile(item, index)
          const newImageName = generateImageName(file.name)

          let upLoadFile: UploadFile = {
            name: newImageName,
            uid: file.uid,
            status: 'ready',
            size: file.size,
            url: URL.createObjectURL(file),
            percentage: 0,
            raw: file
          }
          return upLoadFile
        })

        let content = (await mammoth.extractRawText({arrayBuffer: arrayBuffer})).value
        articleDetail.text = content.replace(/(\n\n)/gm, '\n')
        let messages = result.messages; // Any messages, such as warnings during conversion
      })
      .catch(function (error) {
        console.error(error);
      });
  }
  fileReader.readAsArrayBuffer(file.raw);
}
// 上传文件后显示到inputBox
const changeInputBox = (file: any) => {
  let subFileNames = file.name.split('.')
  const suffix = subFileNames[subFileNames.length - 1]
  if (!acceptFileType.has(suffix)) {
    errorMessage('仅能支持.docx和.txt结尾的文件喔')
    return
  }
  articleDetail.file_type = acceptFileType.get(suffix)
  upload.value!.clearFiles()
  articleDetail.raw = file.raw

  if (articleDetail.raw.size == 0) {
    previewType.value = 'info'
  } else {
    previewType.value = 'primary'
  }
  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
  if (articleDetail.title == '') {
    if (file.raw.name != undefined && file.raw.name.includes('.')) {
      articleDetail.title = file.name.split('.')[0]
    }
  }
}

const handleDelArticleClicked = async () => {
  await SYNC_POST('/contributor/delArticle', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      ElMessage({
        showClose: true,
        message: '删除文章成功',
        type: 'warning'
      })
      router.back()
    } else {
      errorCallback(response)
    }
  })
  delArticleDialogVisible.value = false
  router.back()
}
const getExpertAndHunterByName = (userName: string) => {
  if (userName == '') {
    return
  }
  contributeManager.loading = true
  SYNC_GET('/usr/getUserBasicInfoByNameNoPagination', {
    name: userName,
    identity: ['EXPERT', 'HUNTER']
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      contributeManager.options = response.data.data
    } else {
      contributeManager.options = []
      errorCallback(response)
    }
    contributeManager.loading = false
  })
}

const handleFileRemove = () => {
  articleDetail.raw = {}
  previewType.value = "info"
}

// 初始化
(async () => {
  if (store.getters.getUserInfo.identity == '未登录') {
    ElMessage({
      showClose: true,
      message: '上传稿件前请先登录哦',
      type: 'info'
    })
    router.push({name: 'login'})
    return
  }
  let userInfo = store.getters.getUserInfo
  if (userInfo.identity == 'CONTRIBUTOR' || userInfo.identity == 'ADMINISTRATOR') {
    contributeManager.isContributor = true
    articleDetail.authorName = userInfo.name
    articleDetail.authorOrganization = userInfo.organization
    if (userInfo.identity == 'CONTRIBUTOR') {
      articleDetail.authorGrade = userInfo.attrs.grade
    } else {
      articleDetail.authorGrade = userInfo.attrs.duties
    }
  }
  // 有article_id时初始化ArticleDetail
  await getArticleDetail();
})()
</script>

<style scoped>
.upload-demo {
  /*display: flex;*/
  margin: 0 0 10px 0;
}

.title-form-item {
  margin-bottom: 10px;
}

.title-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.preview_file {
  margin: 0 10px 0 0 !important;
}

.editor-header {
  font-size: 25px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
  margin-bottom: 10px;
}

.editor-header :deep(.el-textarea__inner) {
  border: 1px solid var(--el-card-border-color);
  border-radius: 10px;
}

.editor-mentor {
  font-size: 18px;
  margin-bottom: 20px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}

.editor-text :deep(.el-textarea__inner) {
  border: 1px solid var(--el-card-border-color);
  border-radius: 10px;
}

.upload-file {
  margin-right: 15px;
}

.description-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}

.more-option-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}


.editor-description {
  font-size: 18px;
  margin-bottom: 10px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}

:deep(.el-collapse,el-collapse-item__wrap) {
  border-style: none;
}

.more-option :deep(.el-collapse-item__header) {
  border-style: none;
}

.more-option-card:deep(.el-card__body) {
  padding: 5px 20px;
}

.more-option-card {
  margin-bottom: 15px;
}

:deep(.docx-wrapper) {
  background-color: transparent; /* 去除黑边 */
  padding: 0;
}

:deep(.docx-wrapper section.docx) {
  margin: 0;
}

.sm-menu-items {
  width: 100%;
  justify-content: center;
  text-align: center;
  padding: 10px 0;
}

#docxContainer {
  background-color: transparent;
  box-shadow: none;
}

#txtContainer {
  background-color: transparent;
  box-shadow: none;
  padding: 0;
}

.txtPreview {
  display: flex;
  white-space: pre-wrap;
  margin: 20px 20px;
}

.upload-doc {
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
  /* height: 200px;*/
}

.button-container {
  margin-bottom: 20px;
}

.upload-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
  line-height: 32px;
}

.upload-file-card:deep(.el-card__body) {
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}

.upload-file-card {
  margin-bottom: 15px;
}

.edit-form:deep(.el-form-item) {
  margin-bottom: unset;
}

.upload-file-name-list {
  display: flex;

}

.upload-file-name {
  margin-top: 10px;
}

.contribute-way-radio {
  display: flex;
}

.contribute-way-container {
  margin-bottom: 20px;
}

.contribute-to-selection {
}
</style>
