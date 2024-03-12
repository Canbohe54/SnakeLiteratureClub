<template xmlns="http://www.w3.org/1999/html">
  <el-row>
    <el-col :span="18" :offset="3">
      <el-input
          class="editor-header"
          v-model="articleDetail.title"
          maxlength="50"
          show-word-limit
          :autosize="{ minRows: 1, maxRows: 3}"
          type="textarea"
          placeholder="请输入标题（建议30字以内）"
      />
      <el-upload
          ref="upload"
          class="upload-demo"
          accept=".txt;;*.docx;;*.pdf"
          action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
          :limit="1"
          :on-change="changeInputBox"
          :auto-upload="false"

      >
        <template #trigger>
          <el-tooltip
              class="box-item"
              effect="light"
              content="限制1个.docx或.txt文件，上传新内容将覆盖旧内容"
              placement="top"
          >
            <el-button class="upload-file" type="primary">上传文章</el-button>
          </el-tooltip>
        </template>
        <el-button class="preview_file" v-show="Object.keys(articleDetail.raw).length != 0"
                   @click="handleDocumentPreView(articleDetail.raw)">预览文章
        </el-button>

        <el-dialog id="docContainer" v-model="docDialogVisible" style="width: fit-content;height: fit-content;">
        </el-dialog>
      </el-upload>
<!--                <div id="docContainer" style="width: fit-content;height: fit-content;"></div>-->

      <el-input
          v-model="articleDetail.text"
          class="editor-text"
          :autosize="{ minRows: 10, maxRows: 20}"
          type="textarea"
          placeholder="请输入正文"
      />
      <el-dialog v-model="dialogVisible">
        <img :src="dialogImageUrl" alt="Preview Image">
      </el-dialog>
      <el-card class="more-option-card">
        <el-collapse>
          <el-collapse-item class="more-option" title="更多设置" name="1">
            <div class="more-option-head"><span>文章描述</span></div>
            <el-input
                class="editor-description"
                v-model="articleDetail.description"
                maxlength="150"
                show-word-limit
                :autosize="{ minRows: 4, maxRows: 10}"
                type="textarea"
                placeholder="请输入描述（建议100字以内）"
            />
            <div class="more-option-head"><span>参考插图</span></div>
            <el-upload
                v-model:file-list="imageFileList"
                action="''"
                list-type="picture-card"
                :on-preview="handlePictureCardPreview"
                :auto-upload="false"
                :on-remove="imgRemove"
                :on-success="imgSuccess"
                :on-error="imgError"
                accept="image/jpg,image/jpeg,image/png"
                multiple
            >
              <el-icon>
                <Plus/>

              </el-icon>
            </el-upload>

            <div class="more-option-head"><span>文章标签</span></div>
            <SearchFilter ref="SearchFilterRef" @change="searchFilterChange"/>

          </el-collapse-item>
        </el-collapse>
      </el-card>


      <el-button class="3" :type="saveBtnType" @click="save" :disabled="saveBtnText === '已保存'">{{
          saveBtnText
        }}
      </el-button>
      <el-button class="3" type="success" @click="release">发布</el-button>
      <el-button type="danger" @click="delArticleDialogVisible=true">删除文章</el-button>
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
  </el-row>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import type {UploadFile, UploadInstance} from 'element-plus'
import {ElMessage} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {useRoute} from 'vue-router'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import SearchFilter from '@/components/search/SearchFilter.vue'
import router from '@/router'
import mammoth from 'mammoth'
import {Plus} from '@element-plus/icons-vue'
import {base64ToFile, generateImageName} from '@/scripts/ImageUtil'
import '@vue-office/docx/lib/index.css'
import { fileToBlob } from '@/scripts/DocumentUtil'
import {renderAsync} from 'docx-preview'

const upload = ref<UploadInstance>()
const store = useStore()
const route = useRoute()
const SearchFilterRef = ref()
const saveBtnType = ref('success')
const saveBtnText = ref('保存')
const delArticleDialogVisible = ref(false)
const docDialogVisible = ref(false)
const docBlob = ref<Blob>()

const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  textBy: '',
  title: '',
  description: '',
  status: 'ROUGH',
  attr: '{}',
  raw: {}
})
let imageFileList = ref<UploadFile[]>([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
watch(articleDetail, () => {
  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
})
const searchFilterChange = () => {
  articleDetail.attr = JSON.stringify(SearchFilterRef.value.filterSelection)
}

function errorCallback(response: any) {
  console.log(response)
  if (response.status === 200) {
    ElMessage({
      showClose: true,
      message: response.data.statusMsg,
      type: 'error'
    })
  } else {
    ElMessage({
      showClose: true,
      message: 'Network Error!',
      type: 'error'
    })
  }
}

// 有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET('/article/articleDetail', {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      for (const dataKey in response.data.article) {
        articleDetail[dataKey] = response.data.article[dataKey]
      }
      SearchFilterRef.value.loadSelection(JSON.parse(articleDetail.attr))
      articleDetail.attr = JSON.parse(articleDetail.attr).tags
    } else {
      errorCallback(response)
    }
  })
})()

// 保存草稿
const save = async () => {
  let param = new FormData()
  param.append("raw_file", articleDetail.raw, generateImageName(articleDetail.raw.name))
  param.append("token", 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjExNDUxNCJ9.AzE55n2_JDJolF-UQ94Qgun_szDCqsu_KYDDD6Tcebw')
  // param.append("token", store.getters.getToken)
  param.append("id", articleDetail.id)
  param.append("text", articleDetail.text)
  param.append("textBy", '')
  param.append("title", articleDetail.title)
  param.append("description", articleDetail.description)
  param.append("status", 'ROUGH')
  param.append("attr", `{"tags":${articleDetail.attr}}`)
  param.append("imageURL", '{}')

  await SYNC_POST('/contributor/contribute', param, async (response) => {
    if (response.status === 200 && response.data.message === 'Success.') {
      console.log('Save successfully!')
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
  upload.value!.clearFiles()
  articleDetail.raw = file.raw
  let subFileNames = file.name.split('.')
  if (subFileNames[subFileNames.length - 1] == 'txt') {
    analyzeTXT(file)
  } else if (subFileNames[subFileNames.length - 1] == 'docx') {
    analyzeDOCX(file)
  }

  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
}

// 发布文章
const release = async () => {
  let param = new FormData();

  if (imageFileList.value.length > 0) {
    imageFileList.value.forEach((val: any, index: any) => {
      const newImageName = generateImageName(val.raw.name)
      console.log(val.raw)
      param.append("image_list", val.raw as Blob, newImageName)
    })
  } else {
    param.append("image_list", "")
  }
  param.append("token", store.getters.getToken)
  param.append("id", articleDetail.id)
  param.append("text", articleDetail.text)
  param.append("textBy", '')
  param.append("title", articleDetail.title)
  param.append("description", articleDetail.description)
  param.append("status", '3')
  param.append("attr", `{"tags":${articleDetail.attr}}`)
  param.append("imageURL", '{}')

  await SYNC_POST('/contributor/save', param, async (response) => {
        if (response.status === 200 && response.data.statusMsg === 'Success.') {
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

const handleDelArticleClicked = async () => {
  await SYNC_POST('/contributor/delArticle', {
    token: store.getters.getToken,
    article_id: articleDetail.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
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

function imgSuccess(response: any, file: any, fileList: any) {
  imageFileList = fileList
}

function imgError(response: any) {
  errorCallback(response)
}

function imgRemove(file: any, fileList: any) {
  imageFileList = fileList
}

const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

const handleDocumentPreView = async (file: any) => {
  fileToBlob(file).then((result) => {
    docBlob.value = result as Blob

    let docContainer = document.getElementById("docContainer");
    renderAsync(
        result,
        docContainer, // HTMLElement 渲染文档内容的元素,
        null, // HTMLElement, 用于呈现文档样式、数字、字体的元素。如果为 null，则将使用 reportContainer。
    ).then(res => {
      // console.log("res---->", res)
    })
  })

  docDialogVisible.value = true
}
const renderedHandler = async () => {
  console.log("渲染完成")
}
const errorHandler = async () => {
  console.log("渲染失败")
}
</script>

<style scoped>
.upload-demo {
  display: flex;
  margin: 0 20px 10px 0;
}

.preview_file {
  display: flex;
  margin: 0 10px 0 0;
}

.editor-header {
  font-size: 25px;
  margin-bottom: 10px;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 10px;
}

.editor-header :deep(.el-textarea__inner) {
  border: 1px solid var(--el-card-border-color);
  border-radius: 10px;
}

.editor-text {
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

.more-option-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
}


.editor-description {
  font-size: 18px;
  margin-bottom: 20px;
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

#docContainer {
  background-color: transparent;
  box-shadow: none;
}
</style>
