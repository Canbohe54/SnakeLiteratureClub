<template>
  <span class="preview_file">
<!--    :disabled="Object.keys(props.articleRaw).length == 0"-->
  <el-button :type="props.previewType" :disabled="props.disabled"
             @click="handleDocumentPreView(props.articleRaw)">{{ props.text }}
  </el-button>
  <el-dialog id="docxContainer" v-model="dialogManager.docxDialogVisible"
             style="width: fit-content;height: fit-content;">
  </el-dialog>
  <el-dialog id="txtContainer" v-model="dialogManager.txtContainerVisible" close-on-press-escape
             :show-close="false">
<!--              <div class="txtPreview" style="white-space: pre-wrap;">{{ dialogManager.txtContainerText }}</div>-->
    <el-text class="txtPreview" size="large">{{ dialogManager.txtContainerText }}</el-text>
  </el-dialog>
  </span>
</template>
<script lang="ts" setup>
import {reactive, toRef} from 'vue';
import {fileToBlob} from '@/scripts/DocumentUtil'
import {renderAsync} from 'docx-preview'

const props = defineProps({
  text: { // 按钮名称
    type: String,
    default: '预览文章'
  },
  articleRaw: { // File类型
    type: Object,
    required: true
  },
  previewType: { // 按钮样式 e.g., 'info', 'primary', 'success'
    type: String,
    required: false
  },
  disabled: { // 是否禁用, 默认否
    type: Boolean,
    required: false,
    default: false
  },
  lockBeforePreview: { // 预览前是否上锁处理
    type: Boolean,
    required: false,
    default: false
  },
  articleId: { //若要上锁，需要传入articleId
    type: String,
    required: false
  }
})
// const emit = defineEmits(['lockBeforePreview'])
let articleRaw = toRef(props, 'articleRaw')

const dialogManager = reactive({
  txtContainerVisible: false,
  docxDialogVisible: false,
  txtContainerText: ''
})
const lockBeforePreview = () => {
  if (props.articleId === '' || props.articleId == undefined) {
    return
  }
  console.log(props.articleId)
}
const handleTXTPreview = (file: any) => {
  const fileReader = new FileReader()
  fileReader.onload = async (e) => {
    let text = e.target?.result as string
    dialogManager.txtContainerText = text
    dialogManager.txtContainerVisible = true
  }
  fileReader.readAsText(file)
}
const handleDOCXPreview = (file: any) => {
  fileToBlob(file).then((result) => {

    let docContainer = document.getElementById("docxContainer");
    renderAsync(
        result,
        docContainer as HTMLElement // HTMLElement 渲染文档内容的元素,
        //    null,  HTMLElement, 用于呈现文档样式、数字、字体的元素。如果为 null，则将使用 reportContainer。
    ).then(res => {
      // dialogManager.docxDialogVisible = true 不能放在这
    })
  })
  dialogManager.docxDialogVisible = true
}

const handleDocumentPreView = async (file: any) => {
  if (props.lockBeforePreview) {
    lockBeforePreview()
  }
  //文件为空对象则返回
  // if (Object.keys(props.articleRaw).length == 0) {
  //   return
  // }
  let fileName: string = file.name as string
  let subFileNames = ['']
  if (file.name != undefined && fileName.includes('.')) {
    subFileNames = file.name.split('.')
  }
  // 获取文件类型
  if (subFileNames[subFileNames.length - 1] == 'txt' || file.type == 'text/plain') {
    handleTXTPreview(file)
  } else if (subFileNames[subFileNames.length - 1] == 'docx' || file.type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
    handleDOCXPreview(file)
  }
}

</script>


<style>
.preview_file {
  /*margin-right: 15px;*/
  text-align: center;
}

.txtPreview {
  display: flex;
  white-space: pre-wrap;
  margin: 20px 20px;
  text-align: start !important;
}

#docxContainer {
    max-width: 1000px;
}

#docxContainer .docx-wrapper {
  background: #ecf9ef;
  border-radius: 5px;
}

#docxContainer .docx {
  width: 100%!important;
  min-height: 100%!important;
  display: table;
  padding: 2em 2em!important;
  text-align: start !important;
  box-shadow: none;
  background: none;
}

#docxContainer .docx p span {

}
</style>
