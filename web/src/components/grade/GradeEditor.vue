<template>
  <!-- Form -->
    <el-button type="success" @click="() => {dialogFormVisible = true;getGrade()}" round>
      评分与评价
    </el-button>

  <el-dialog v-model="dialogFormVisible" title="评分与评价">
    <el-form :model="form">
      <div class="demo-rate-block">
        <span class="demonstration">文章评分</span>
        文字与表达：<el-rate v-model="gradeData.grade_expr" size="large" :colors="colors" show-score />
        <br/>
        内容与结构：<el-rate v-model="gradeData.grade_struct" size="large" :colors="colors" show-score />
        <br/>
        主题相关性：<el-rate v-model="gradeData.grade_theme" size="large" :colors="colors" show-score />
        <div class="demo2">
        <span >文章评价</span>
        </div>
        <el-input v-model="gradeData.advice" :autosize="{ minRows: 4, maxRows: 6 }" type="textarea" placeholder="Please input"
                  clearable maxlength="100" show-word-limit/>

        <br/>
<!--        <el-row class="mb-4">-->
<!--          <el-button type="cancle" round >取消</el-button>-->
<!--          <el-button type="certain" round @click="save">确定</el-button>-->

<!--        </el-row>-->
      </div>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="() => {dialogFormVisible = false; save()}" >
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>

</template>

<script lang="ts" setup>
import {reactive, ref} from "vue"
import {SYNC_GET, SYNC_POST} from "@/scripts/Axios";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import {AttributeAddableObject} from "@/scripts/ArticleTagFilter";

const store = useStore()
const route = useRoute()

const dialogFormVisible = ref(false)
const formLabelWidth = '140px'
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


const gradeData = reactive({
  expert_id: '',
  article_id: '',
  grade_expr: 0,
  grade_struct: 0,
  grade_theme: 0,
  grade_all: '',
  advice: ''
})
const value1 = ref(0)
const value2 = ref(0)
const value3 = ref(0)
const colors = ref(['#99A9BF', '#F7BA2A', '#FF9900']) // same as { 2: '#99A9BF', 4: { value: '#F7BA2A', excluded: true }, 5: '#FF9900' }

const input = ref('')


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
const save = async () => {
  console.log("save")
  //to do:获取token测试
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
    }else {
      errorCallback(response)
    }
  })
}

async function getGrade(){
  await (SYNC_GET('/grade/getGradeByExpertIdAndArticleId', {
    article_id: route.query.id,
    expert_id: store.getters.getUserInfo.id,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'success') {
      gradeData.expert_id = response.data.grade.expert_id;
      gradeData.article_id = response.data.grade.article_id;
      gradeData.grade_expr = ref(response.data.grade.grade_expr);
      gradeData.grade_struct =  ref(response.data.grade.grade_struct);
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
  //color: var(--el-text-color-secondary);
  font-size: 14px;

}

.el-button--text {
  margin-right: 15px;
}
.el-select {
  width: 300px;
}
.el-input {
  width: 300px;
}
.dialog-footer button:first-child {
  margin-right: 10px;
}
.demo2 {
  margin-bottom: 10px;
}
</style>
