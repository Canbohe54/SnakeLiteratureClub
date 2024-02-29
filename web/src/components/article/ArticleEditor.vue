<template>
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
      <el-input
        v-model="articleDetail.text"
        class="editor-text"
        :autosize="{ minRows: 10, maxRows: 20}"
        type="textarea"
        placeholder="请输入正文"
      />
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

            <div class="more-option-head"><span>文章标签</span></div>
            <SearchFilter ref="SearchFilterRef" @change="searchFilterChange"/>

          </el-collapse-item>
        </el-collapse>
      </el-card>


      <el-upload
        ref="upload"
        class="upload-demo"
        accept=".txt;;*.docx"
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
        :limit="1"
        :on-change="changeInputBox"
        :auto-upload="false"
        :show-file-list="false"
      >

        <template #trigger>
          <el-button class="upload-file" type="primary">上传文件</el-button>
        </template>
        <template #tip>
          <div class="el-upload__tip text-red">
            限制1个文件，新文件将覆盖旧文件
          </div>
        </template>

        <el-button class="3" :type="saveBtnType" @click="save" :disabled="saveBtnText === '已保存'">{{ saveBtnText }}</el-button>
        <el-button class="3" type="success" @click="release">发布</el-button>
        <el-button type="danger" @click="delArticleDialogVisible=true">删除文章</el-button>
      </el-upload>
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
import type {UploadInstance} from 'element-plus'
import {SYNC_GET, SYNC_POST} from '@/scripts/Axios'
import {useStore} from 'vuex'
import {useRoute} from 'vue-router'
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {ElMessage} from 'element-plus'
import SearchFilter from '@/components/search/SearchFilter.vue'
import router from '@/router'
import mammoth from 'mammoth'

const upload = ref<UploadInstance>()
const store = useStore()
const route = useRoute()
const SearchFilterRef = ref()
const saveBtnType = ref('success')
const saveBtnText = ref('保存')
const delArticleDialogVisible = ref(false)


const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  textBy: '',
  title: '',
  description: '',
  status: '',
  attr: '{}'
})

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
      articleDetail.attr=JSON.parse(articleDetail.attr).tags
    } else {
      errorCallback(response)
    }
  })
})()

// 保存草稿
const save = async () => {
  // to do:获取token测试
  await SYNC_POST('/contributor/save', {
    token: store.getters.getToken,
    id: articleDetail.id,
    text: articleDetail.text,
    time: null,
    textBy: '',
    title: articleDetail.title,
    description: articleDetail.description,
    status: 1, // 保存成功
    attr: `{"tags":${articleDetail.attr}}`
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
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
  const fileReader = new FileReader();
  fileReader.onload = async (event) =>{
    const arrayBuffer = event.target?.result as ArrayBuffer
    //...
    articleDetail.text = (await mammoth.extractRawText({arrayBuffer: arrayBuffer})).value
  }
  fileReader.readAsArrayBuffer(file.raw);
}
// 上传文件后显示到inputBox
const changeInputBox = (file: any) => {
  upload.value!.clearFiles()
  let subFileNames = file.name.split('.')
  if(subFileNames[subFileNames.length - 1] == 'txt'){
    analyzeTXT(file)
  }else if (subFileNames[subFileNames.length - 1] == 'docx'){
    analyzeDOCX(file)
  }

  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
}

// 发布文章
const release = async () => {
  await SYNC_POST('/contributor/save', {
    token: store.getters.getToken,
      id: articleDetail.id,
    text: articleDetail.text,
    time: null,
    textBy: '',
    title: articleDetail.title,
    description: articleDetail.description,
    status: 3, // 已发布
    attr: `{"tags":${articleDetail.attr}}`
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log('Release successfully!')
      ElMessage({
        showClose: true,
        message: '已成功发布文章!',
        type: 'success'
      })
    } else {
      errorCallback(response)
    }
  }
  )}

const handleDelArticleClicked =async () => {
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
</script>

<style scoped>
.upload-demo {
  margin: 20px 0;
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
</style>
