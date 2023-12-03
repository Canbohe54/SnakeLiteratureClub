<template>
  <div class="filterBox filterBox-shadow">
    <el-row class="filterGroup" v-for="(group, groupKey) in filterTagGroups">
      <el-col :span="3">
        <el-text class="checkboxGroupName">{{ groupKey }}</el-text>
      </el-col>
      <el-col :span="21">
        <el-checkbox-group class="checkboxGroup" tag="span" v-model="filterSelection[groupKey]" @change="filterOnChange()"
                           :label="groupKey">
          <el-checkbox-button class="checkboxButton" v-for="key in group" :label="key">{{ key }}</el-checkbox-button>
        </el-checkbox-group>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { AttributeAddableObject } from '@/scripts/ArticleTagFilter'

const filterTagGroups = { 'group-A': ['A-filter', 'B-fil', 'C-fil', 'D-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil'], 'group-B': ['D-fil', 'E-fil'] }

const filterSelection: AttributeAddableObject = ref({});

// filterSelect初始化: 初始化结果为{'groupName_1': [], 'groupName_2': [], ...}
// => 为了保证filterSelect中一定包含所有筛选组的键值(即filters对象中的所有键)
// => 也可以不用, 此时必须点过筛选组group中某个tag才会在filterSelect中包含组键
(() => {
  for (const filter in filterTagGroups) {
    filterSelection.value[filter] = []
  }
})()

function loadSelection (attr: AttributeAddableObject<string[]>) {
  const select = attr.tags
  for (const group in select) {
    for (const tag of select[group]) {
      filterSelection.value[group].push(tag)
    }
  }
}

defineExpose({ filterSelection, loadSelection })
defineProps({
  filterOnChange: {
    type: Function,
    required: false,
    default: () => {}
  }
})
</script>

<style scoped>
.filterBox {
  border: 1px solid var(--el-card-border-color);
  border-radius: 10px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin-bottom: 10px;
}
.filterBox-shadow {
  box-shadow: var(--el-box-shadow-light);
}
.filterGroup {
  margin-top: 5px;
  margin-bottom: 5px;
}
.checkboxGroupName {
  text-align: center;
  line-height: 40px;
  float: right;
  padding-right: 25%;
}
.checkboxGroup {
  text-align: left;
  border-left: 3px solid #76b7c2;
  padding-left: 3%;
  float: left;
}
.checkboxGroup :deep(.is-checked) {
  border-color: rgba(185, 161, 203, 0.7);
  background-color: rgba(66, 185, 131, 0.3);
}
.checkboxButton :deep(.el-checkbox-button__inner) {
  border: none !important;
  background-color: rgba(0, 0, 0, 0) !important;
  padding: 8px 13px !important;
  box-shadow: none !important;
}
.checkboxButton {
  --el-checkbox-button-checked-text-color: #4e73a2;
  margin: 5px 25px 5px 0;
  text-align: left;
  border: 2px solid rgba(0, 0, 0, 0.6);
  border-radius: 5px !important;
}
</style>
