<template>
  <div class="common-layout">
    <el-container>
      <el-main>
        <el-card class="box-card">
          <el-row><el-text>1</el-text></el-row>
          <el-row><el-text>2</el-text></el-row>
          <el-text>3</el-text>
        </el-card>
      </el-main>

      <el-footer>
        <el-card>
          <el-row>
            <el-avatar> user </el-avatar>
            <div class="blank"></div>
            <el-text>username </el-text>
            <div class="e"></div>
            <el-text>comments</el-text>
          </el-row>
          <el-divider />
          <el-row>
            <el-avatar> user </el-avatar>
            <div class="blank"></div>
            <el-text>username </el-text>
            <div class="e"></div>
            <el-text>comments</el-text>
          </el-row>
        </el-card>
      </el-footer>
    </el-container>
  </div>
</template>
<script lang="ts" setup>

</script>
<style>
.box-card{
  display: flex;
  justify-content: center;
}

.e{
  border-right: 1px solid var(--el-border-color);
  margin-right: 10px;
  margin-left: 10px;
}

.blank{
  margin-right: 10px;
}

</style>
