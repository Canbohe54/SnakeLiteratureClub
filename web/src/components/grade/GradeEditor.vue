<template>
    <el-collapse v-model="dialogFormVisible">
      <el-collapse-item title="评分、评价与推荐" name="1">
        <el-form :model="form">
          <div class="demo-rate-block">
            <span class="demonstration">文章评分</span>
            文字与表达：<el-rate v-model="gradeData.grade_expr" size="large" :colors="colors" show-score />
            <br />
            内容与结构：<el-rate v-model="gradeData.grade_struct" size="large" :colors="colors" show-score />
            <br />
            主题相关性：<el-rate v-model="gradeData.grade_theme" size="large" :colors="colors" show-score />
            <div class="demo2">
              <span>文章评价</span>
            </div>
            <el-input v-model="gradeData.advice" :autosize="{ minRows: 4, maxRows: 6 }" type="textarea"
              placeholder="请输入您的评价" clearable maxlength="100" show-word-limit />
            <br />
            <el-checkbox v-model="checked1" label="推荐该文章刊登出版" size="large" />
          </div>

        </el-form>
        <el-button type="danger" style="float: left;"
          @click="() => { dialogFormVisible = false; deleteGrade() }">删除</el-button>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="() => { dialogFormVisible = false; save() }">
          确定
        </el-button>
      </el-collapse-item>
    </el-collapse>

</template>

<script lang="ts" setup>
import { reactive, ref } from "vue"
import { SYNC_POST } from "@/scripts/Axios";
import { ElMessage } from "element-plus";
import { useStore } from "vuex";
import { useRoute } from "vue-router";


const store = useStore()
const route = useRoute()

const dialogFormVisible = ref(false)
const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})

const gradeData = reactive({
  expert_id: '',
  article_id: '',
  grade_expr: 0,
  grade_struct: 0,
  grade_theme: 0,
  grade_all: '',
  advice: ''
})

const colors = ref(['#99A9BF', '#F7BA2A', '#FF9900']) // same as { 2: '#99A9BF', 4: { value: '#F7BA2A', excluded: true }, 5: '#FF9900' }

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

const save = async () => {
  console.log("save")
  //to do:获取token测试
  // if(gradeData.grade_expr == undefined || gradeData.grade_struct == undefined || gradeData.grade_theme == undefined){
  //   ElMessage({
  //     showClose: true,
  //     message: '请先进行评分！',
  //     type: 'warning',
  //   })
  //   return
  // }
  if (gradeData.grade_expr == undefined) {
    gradeData.grade_expr = 0;
  }
  if (gradeData.grade_struct == undefined) {
    gradeData.grade_struct = 0;
  }
  if (gradeData.grade_theme == undefined) {
    gradeData.grade_theme = 0;
  }
  if (gradeData.advice == undefined) gradeData.advice = ''
  await SYNC_POST("/grade/add", {
    token: store.getters.getToken,
    article_id: route.query.id,
    expert_id: store.getters.getUserInfo.id,
    grade_expr: gradeData.grade_expr,
    grade_struct: gradeData.grade_struct,
    grade_theme: gradeData.grade_theme,
    advice: gradeData.advice,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'success') {
      console.log("Save successfully!")
      ElMessage({
        showClose: true,
        message: 'Save successfully!',
        type: 'success',
      })
      location.reload()
    } else {
      errorCallback(response)
    }
  })
}

async function deleteGrade() {
  await SYNC_POST('/grade/delete', {
    token: store.getters.getToken,
    article_id: route.query.id,
    expert_id: store.getters.getUserInfo.id
  }, async (response) => {
    if (response.data.statusMsg === 'Grades do not exist.') {
      ElMessage({
        message: '尚未评分与评价！',
        type: 'warning'
      })
    } else if (response.data.statusMsg === 'success') {
      ElMessage({
        message: '评分与评价删除成功！',
        type: 'success'
      })
      location.reload()
    } else {
      ElMessage({
        message: response.data.statusMsg,
        type: 'error'
      })
    }
  })
}

async function getGrade() {
  await (SYNC_POST('/grade/getGradeByExpertIdAndArticleId', {
    article_id: route.query.id,
    expert_id: store.getters.getUserInfo.id,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'success') {
      gradeData.expert_id = response.data.grade.expert_id;
      gradeData.article_id = response.data.grade.article_id;
      gradeData.grade_expr = ref(response.data.grade.grade_expr);
      gradeData.grade_struct = ref(response.data.grade.grade_struct);
      gradeData.grade_theme = ref(response.data.grade.grade_theme);
      gradeData.grade_all = response.data.grade.grade_all;
      gradeData.advice = ref(response.data.grade.advice);
    } else {
      console.log(response)
    }
  }))
}
</script>

<style scoped>
.demo-rate-block {
  padding: 10px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  display: inline-block;
  width: 49%;
  box-sizing: border-box;
}

.demo-rate-block:last-child {
  border-right: none;
}

.demo-rate-block .demonstration {
  display: block;
  font-size: 14px;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}

.demo2 {
  margin-bottom: 10px;
}
</style>
