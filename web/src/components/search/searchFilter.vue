<template>
    <el-row>
      <el-col :span="18" :offset="2">

        <div class="filterBox">
          <el-row class="filterGroup" v-for="(group, groupKey) in filterTagGroups">
            <el-col :span="3">
              <el-text class="checkboxGroupName">{{groupKey}}</el-text>
            </el-col>
            <el-col :span="21">
              <el-checkbox-group class="checkboxGroup" tag="span" v-model="filterSelection[groupKey]" @change="onChange()" :label="groupKey">
                <el-checkbox-button class="checkboxButton" v-for="key in group" :label="key">{{key}}</el-checkbox-button>
              </el-checkbox-group>
            </el-col>
          </el-row>
        </div>

      </el-col>
    </el-row>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { AttributeAddableObject } from '@/scripts/ArticleTagFilter'

let filterTagGroups = {'group-A': ['A-filter', 'B-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil', 'C-fil'], 'group-B': ['D-fil', 'E-fil']}

const filterSelection: AttributeAddableObject = ref({});

// filterSelect初始化: 初始化结果为{'groupName_1': [], 'groupName_2': [], ...}
// => 为了保证filterSelect中一定包含所有筛选组的键值(即filters对象中的所有键)
// => 也可以不用, 此时必须点过筛选组group中某个tag才会在filterSelect中包含组键
(() => {
  for (const filter in filterTagGroups) {
    filterSelection.value[filter] = []
  }
})();

function onChange() {
  console.log(filterSelection.value)
}
</script>

<style scoped>
.filterBox {
  border-radius: 20px;
  border: 2px solid royalblue;
  padding-top: 5px;
  padding-bottom: 5px;
}
.filterGroup {
  margin-top: 5px;
  margin-bottom: 5px;
}
.checkboxGroupName {
  text-align: center;
  line-height: 30px;
  float: right;
  padding-right: 25%;
}
.checkboxGroup {
  border-left: 3px solid blueviolet;
  padding-left: 3%;
  float: left;
}
.checkboxGroup :deep(.is-checked) {
  background-color: rgba(66, 185, 131, 0.5);
}
.checkboxButton :deep(.el-checkbox-button__inner) {
  border: none !important;
  background-color: rgba(0, 0, 0, 0) !important;
  padding: 8px 13px !important;
  box-shadow: none !important;
}
.checkboxButton {
  --el-checkbox-button-checked-text-color: #b82020;
  margin-bottom: 10px;
  margin-left: 0;
  margin-right: 25px;
  text-align: left;
  border: 1px solid rgba(0, 0, 0, 0.5);
  border-radius: 5px !important;
}
</style>
