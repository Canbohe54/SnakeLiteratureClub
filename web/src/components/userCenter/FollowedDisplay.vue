<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-card class="box-card follow-result-list-card">
          <el-empty v-if="false" description="暂无结果"/>
          <el-row>
            <el-col :span="4">
              <el-menu
                class="followed-menu el-menu-vertical-demo"
                default-active="all"
              >
                <el-menu-item index="all" class="followed-menu-item" route="/account/info">
                  <span>全部关注</span>
                </el-menu-item>
                <el-menu-item index="professor" class="followed-menu-item" route="/account/email/info">
                  <span>专家</span>
                </el-menu-item>
                <el-menu-item index="author" class="followed-menu-item" route="/account/password/verify">
                  <span>作者</span>
                </el-menu-item>
              </el-menu>
            </el-col>
            <el-col :span="20">
              <el-card class="box-card followed-single">
                <div class="followed-avtinfo">
                  <el-avatar :size="52"></el-avatar>
                  <div class="followed-info">
                    <div class="followed-info-nametag">
                      <el-text class="mx-1 followed-info-username" size="large">Canbohe54</el-text>
                      <el-tag disable-transitions>专家</el-tag>
                    </div>
                    <el-text class="mx-1" size="small">这个人很懒，什么都没留下~</el-text>
                  </div>
                </div>
                <!--                <el-button link></el-button>-->
                <el-dropdown>
                  <span class="followed-more el-dropdown-link">
                    <el-icon><MoreFilled /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item>取消关注</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-card>
            </el-col>
          </el-row>

          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageInfo.currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pageInfo.total"
            class="search-result-pageination">
          </el-pagination>
        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { MoreFilled } from '@element-plus/icons-vue'

const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}

// 监听 page size 改变的事件
function handleSizeChange (newSize: any) {
  pageInfo.pageSize = newSize
}

// 监听 页码值 改变的事件
function handleCurrentChange (newPage: any) {
  pageInfo.currentPage = newPage
}

</script>
<style scoped>
.followed-menu {
  margin-top: 10px;
  border: 0;
}
.follow-result-list-card {
  --el-card-padding: 20px 0;
}
.followed-menu-item {
  display: flex;
  flex-direction: row-reverse;
  padding-right: 30px!important;
}
.followed-menu-item.is-active {
  color: #fafafa;
  background-color: #00a1d6;
}
.followed-menu-item:hover {
  color: #00a1d6;
  background-color:var(--el-color-info-light-9);
}
.follow-result-list-card {
  border-radius: 10px;
}

.search-result-pageination {
  margin: 20px 0 10px 0;
  display: flex;
  justify-content: center;
}

.followed-single {
  border: 0;
  box-shadow: none;
  margin: 0 10px 10px;
}

.followed-single:deep(.el-card__body) {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.followed-avtinfo {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.followed-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 0 20px;
}

.followed-info-nametag {
  display: flex;
  justify-content: center;
  margin-bottom: 6px;
}

.followed-info-username {
  margin-right: 6px;
}
.followed-more {
  cursor: pointer;
  display: flex;
  align-items: center;
  margin-right: 10px;
}
.el-dropdown-link:focus {
  outline: none;
}
</style>
