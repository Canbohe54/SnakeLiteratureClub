<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <el-input
        class="editor-header"
        v-model="articleDetail.title"
        maxlength="50"
        show-word-limit
        :rows="20"
        :autosize="{ minRows: 1, maxRows: 3}"
        type="textarea"
        placeholder="请输入标题（建议30字以内）"
      />
      <el-input
        v-model="articleDetail.text"
        class="editor-text"
        :rows="20"
        :autosize="{ minRows: 10, maxRows: 20}"
        type="textarea"
        placeholder="请输入正文"
      />

      <SearchFilter ref="SearchFilterRef"/>


      <el-upload
        ref="upload"
        class="upload-demo"
        accept=".txt"
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

        <el-button class="3" :type="saveBtnType" @click="save">{{ saveBtnText }}</el-button>
        <el-button class="3" type="success" @click="dialogFormVisible=true">发布</el-button>
      </el-upload>
      <el-dialog v-model="dialogFormVisible" title="SHipping address" draggable center>
        <el-form ref="formRef" :model="form" :label-width="formWidth">
          <el-form-item label="promotion_name" :label-width="formLabelWidth">
            <el-input v-model="form.name" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="region" :label-width="formLabelWidth">
            <el-input v-model="form.region" autocomplete="off"/>
          </el-form-item>
        </el-form>
        <span class="dialog-footer">
              <el-button @click="dialogFormVisible = false">Cancel</el-button>
              <el-button type="primary" @click="release">
                Confirm
              </el-button>
        </span>
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
import {ElMessage, FormInstance} from 'element-plus'
import SearchFilter from '@/components/search/SearchFilter.vue'

const upload = ref<UploadInstance>()
const store = useStore()
const route = useRoute()
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'
const formWidth = '20%'
const formRef = ref<FormInstance>()
const SearchFilterRef = ref()
const saveBtnType = ref('success')
const saveBtnText = ref('保存')

interface releaseForm {
  name: string
  region: string
} // 验证表单接口
const form = reactive<releaseForm>({
  name: '',
  region: ''
})
const articleDetail: AttributeAddableObject = reactive({
  id: null,
  text: '',
  time: '',
  textBy: '',
  title: '',
  description: '',
  status: '',
  attr: ''
})
watch( articleDetail ,() => {
  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
})
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
    attr: articleDetail.attr
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log('Save successfully!')
      ElMessage({
        showClose: true,
        message: 'Save successfully!',
        type: 'success'
      })
      saveBtnType.value = 'info'
      saveBtnText.value = '已保存'
    } else {
      errorCallback(response)
    }
  })
}
// 上传文件后显示到inputBox
const changeInputBox = (file: any) => {
  upload.value!.clearFiles()
  const fileReader = new FileReader()
  fileReader.onload = async (e) => {
    articleDetail.text = e.target?.result as string
  }
  fileReader.readAsText(file.raw)
  saveBtnType.value = 'success'
  saveBtnText.value = '保存'
}

// 发布文章
const release = async () => {
  dialogFormVisible.value = false
  await SYNC_POST('/contributor/save', {
    token: store.getters.getToken,
    id: articleDetail.id,
    text: articleDetail.text,
    time: null,
    textBy: '',
    title: articleDetail.title,
    description: articleDetail.description,
    status: 3, // 已发布
    attr: articleDetail.attr
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log('Release successfully!')
      ElMessage({
        showClose: true,
        message: 'Release successfully!',
        type: 'success'
      })
    } else {
      errorCallback(response)
    }
  })
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
</style>
