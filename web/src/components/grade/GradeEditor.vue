<template>
  <div class="demo-rate-block">
    <span class="demonstration">文章评分</span>
    <el-rate v-model="value1" size="large" :colors="colors" allow-half show-score />
    <el-rate v-model="value2" size="large" :colors="colors" allow-half show-score />
    <el-rate v-model="value3" size="large" :colors="colors" allow-half show-score />
    <span class="demo2">文章评价</span>
    <el-input v-model="input" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea" placeholder="Please input"
              clearable maxlength="100" show-word-limit/>

    <div><br></div>
    <el-row class="mb-4">
      <el-button type="upload" round @click="save">提交</el-button>

    </el-row>
  </div>
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
  //to do:获取token测试
  await SYNC_POST("/grade/add", {
    token: store.getters.getToken,
    article_id: route.query.id,
    expert_id: store.getters.getUserInfo.id,
    grade_expr: value1.value,
    grade_struct: value2.value,
    grade_theme: value3.value,
    advice: input,
  }, async (response) => {
    if (response.status === 200 && response.data.statusMsg === 'success') {
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
</script>

<style scoped>
.demo-rate-block {
  padding: 30px 0;
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
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
</style>
