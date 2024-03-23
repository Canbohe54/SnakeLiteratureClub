<template>
  <div class="preview_file">
    <el-card id="docxContainer" v-if="dialogManager.docxDialogVisible">
    </el-card>
    <el-card v-if="dialogManager.txtContainerVisible">
    <el-text v-if="dialogManager.txtContainerVisible" class="txtPreview" size="large">{{ dialogManager.txtContainerText }}</el-text>
    </el-card>
  </div>
</template>
<script lang="ts" setup>
import {reactive, toRef, watch} from 'vue';
import {fileToBlob} from '@/scripts/DocumentUtil'
import {renderAsync} from 'docx-preview'
const props = defineProps({
  articleRaw: { // File类型
    type: Object,
    required: true
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
watch(props, () => {
  handleDocumentPreView(props.articleRaw)
})
const lockBeforePreview = () => {
  if (props.articleId === '' || props.articleId == undefined) {
    return
  }
  // TODO: lock file
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
  if(props.lockBeforePreview){
    lockBeforePreview()
  }

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
handleDocumentPreView(props.articleRaw)

</script>


<style>
.preview_file {
  text-align: center;
}

.txtPreview {
  display: flex;
  white-space: pre-wrap;
  margin: 20px 20px;
  text-align: start !important;

  /*background-color: gray;*/
}
</style>
