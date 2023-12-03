<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <el-input
        class="header"
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
          class="text"
          :rows="20"
          :autosize="{ minRows: 10, maxRows: 20}"
          type="textarea"
          placeholder="请输入正文"
      />

      <SearchFilter />

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
        <el-button type="primary">上传文件</el-button>
        <el-button class="3" type="success" @click="save">保存</el-button>
        <el-button class="3" type="success" @click="dialogFormVisible=true">发布</el-button>


          <el-dialog v-model="dialogFormVisible" title="SHipping address" draggable center>
            <el-form ref="formRef" :model="form" :label-width="formWidth">
              <el-form-item label="promotion_name" :label-width="formLabelWidth">
                <el-input v-model="form.name" autocomplete="off"/>
              </el-form-item>
              <el-form-item label="region" :label-width="formLabelWidth">
                <el-input v-model="form.region" autocomplete="off"/>
              </el-form-item>
            </el-form>
            <template #footer>
            <span class="dialog-footer">
              <el-button @click="dialogFormVisible = false">Cancel</el-button>
              <el-button type="primary" @click="release">
                Confirm
              </el-button>
            </span>
            </template>
          </el-dialog>


        <template #tip>
          <div class="el-upload__tip text-red">
            限制1个文件，新文件将覆盖旧文件
          </div>
        </template>
      </el-upload>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {reactive, ref} from "vue"
import type {UploadInstance} from 'element-plus'
import {SYNC_GET, SYNC_POST} from "@/scripts/Axios";
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import {AttributeAddableObject} from '@/scripts/ArticleTagFilter'
import {ElMessage, FormInstance} from "element-plus";
import SearchFilter from "@/components/search/SearchFilter.vue";

const upload = ref<UploadInstance>()
const store = useStore()
const route = useRoute()
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'
const formWidth = '20%'
const formRef = ref<FormInstance>()

interface releaseForm {
  name: string
  region: string
} // 验证表单接口
const form = reactive<releaseForm>({
  name: '',
  region: '',
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
});

function errorCallback(response: any) {
  console.log(response)
  if (response.status == 200) {
    ElMessage({
      showClose: true,
      message: response.data.statusMsg,
      type: 'error',
    })
  } else {
    ElMessage({
      showClose: true,
      message: "Network Error!",
      type: 'error',
    })
  }
}

//有article_id时初始化ArticleDetail
(async () => {
  if (route.query.id === '' || route.query.id === undefined) return
  await SYNC_GET("/article/articleDetail", {
    article_id: route.query.id
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      for (const dataKey in response.data.article) {
        articleDetail[dataKey] = response.data.article[dataKey]
      }
    } else {
      errorCallback(response)
    }
  })
})();

//保存草稿
const save = async () => {
  //to do:获取token测试
  await SYNC_POST("/contributor/save", {
    token: store.getters.getToken,
    id: articleDetail.id,
    text: articleDetail.text,
    time: null,
    textBy: '',
    title: articleDetail.title,
    description: articleDetail.description,
    status: 1, //保存成功
    attr: articleDetail.attr
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log("Save successfully!")
      ElMessage({
        showClose: true,
        message: 'Save successfully!',
        type: 'success',
      })
    } else {
      errorCallback(response)
    }
  })
}
//上传文件后显示到inputBox
const changeInputBox = (file: any) => {
  upload.value!.clearFiles()
  let fileReader = new FileReader()
  fileReader.onload = async (e) => {
    articleDetail.text = e.target?.result as string
  }
  fileReader.readAsText(file.raw);
}

//发布文章
const release = async () => {
  dialogFormVisible.value = false
  await SYNC_POST("/contributor/save", {
    token: store.getters.getToken,
    id: articleDetail.id,
    text: articleDetail.text,
    time: null,
    textBy: '',
    title: articleDetail.title,
    description: articleDetail.description,
    status: 3, //已发布
    attr: articleDetail.attr
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      console.log("Release successfully!")
      ElMessage({
        showClose: true,
        message: 'Release successfully!',
        type: 'success',
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

.header {
  font-size: 25px;
}
.text{
  font-size: 18px;
}
</style>
