<template>
  <div class="question-container">
  <el-row v-for="(item, index) in questions " :key="item.id">
    <el-col>
      <div><span class="question-head">{{ index + 1 }}. {{ item.questionHead }}</span></div>
      <el-radio-group v-model="answers[index].answer" class="answer-group">
        <div class="answer" v-for="(candidate, label) in item.candidate">
        <el-radio  :label="label" :value="label">{{ label }}.
          {{ candidate }}
        </el-radio>
        </div>
      </el-radio-group>
    </el-col>
  </el-row>
  <el-button @click="onSubmit">提交</el-button>
  </div>
</template>

<script lang="ts" setup>
import {reactive, toRef} from "vue";

const props = defineProps({
  questions: {
    type: Array<Object>,
    default: () => {
      return [{
        id: 'q1',
        questionHead: '问题1',
        candidate: {
          A: '选项1',
          B: '选项2',
          C: '选项3',
          D: '选项4'
        },
        trueAnswer: 'A'
      }, {
        id: 'q2',
        questionHead: '问题2',
        candidate: {
          A: '选项1',
          B: '选项2',
          C: '选项3',
          D: '选项4'
        },
        trueAnswer: 'A'
      }]
    }
  }
})
const answers = reactive([])
let questions = toRef(props, 'questions')
function init() {
  for (let item of props.questions) {
    answers.push({questionId: item.id, answer: ''})
  }
}
async function onSubmit() {
}
init()
</script>

<style scoped>
.question-container{
  padding: 10px;
}
.question-head {
  display: flex ;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}
.answer :deep(.el-radio) {
  display: block !important;
  line-height: 23px;
  white-space: normal;
  margin-right: 0;
}
.answer :deep(.el-radio-group) {
  display: block !important;
}
.answer{
  text-align: start;
}
.answer-group {
  display: block;
  margin-top: 10px;
}
</style>
