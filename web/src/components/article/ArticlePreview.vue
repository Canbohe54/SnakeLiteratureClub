<template>
  <span class="preview_file">
<!--    :disabled="Object.keys(props.articleRaw).length == 0"-->
  <el-button  :type="props.previewType"
             @click="handleDocumentPreView(props.articleRaw)">预览文章
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
import {reactive} from 'vue';
import {fileToBlob} from '@/scripts/DocumentUtil'
import {renderAsync} from 'docx-preview'

const props = defineProps({
  articleRaw: {
    type: Object,
    required: true
  },
  previewType: {
    type: String,
    required: false
  }
})
const dialogManager = reactive({
  txtContainerVisible: false,
  docxDialogVisible: false,
  txtContainerText: ''
})
const handleTXTPreview = (file: any) => {
  const fileReader = new FileReader()
  fileReader.onload = async (e) => {
    let text = e.target?.result as string
    dialogManager.txtContainerText  = text
    console.log(text)
    // dialogManager.txtContainerText  = text.replace(/\n/g, '<br/>').replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')

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
  //文件为空对象则返回
  // if (Object.keys(props.articleRaw).length == 0) {
  //   return
  // }
  let subFileNames = file.name.split('.')
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
  margin-right: 15px;
}
.txtPreview {
  display: flex;
  white-space: pre-wrap;
  margin: 20px 20px;
}
</style>
