<template>
  <el-row>
    <el-col :span="18" :offset="3">
      <div>
        <el-card class="box-card follow-result-list-card">
          <el-row>
            <el-col :span="4">
              <el-menu class="followed-menu el-menu-vertical-demo" default-active="all" v-model="activeIndex"
                @select="getUserFollowedList">
                <el-menu-item index="all" class="followed-menu-item">
                  <span>全部关注</span>
                </el-menu-item>
                <el-menu-item index="专家" class="followed-menu-item">
                  <span>专家</span>
                </el-menu-item>
                <el-menu-item index="学生" class="followed-menu-item">
                  <span>学生作者</span>
                </el-menu-item>
              </el-menu>
            </el-col>
            <el-col :span="20">
              <el-empty v-if="followInfo.length === 0" description="暂无结果" />
              <el-card class="box-card followed-single" v-for="(followUser, index) in followInfo" :key="index"
                :span="12" @click.native="gotoDetail(followUser.id)">
                <div class="followed-avtinfo">
                  <el-avatar :size="52" :src="followUser.avatar"></el-avatar>
                  <div class="followed-info">
                    <div class="followed-info-nametag">
                      <el-text class="mx-1 followed-info-username" size="large">{{ followUser.name }}</el-text>
                      <el-tag disable-transitions :type="followUser.identity==='专家'?'warning':'success'">{{ followUser.identity }}</el-tag>
                    </div>
                    <el-text class="mx-1" size="small">{{ followUser.description }}</el-text>
                  </div>
                </div>
                <!--                <el-button link></el-button>-->
                <el-dropdown @command="handleCommand">
                  <span class="followed-more el-dropdown-link">
                    <el-icon>
                      <MoreFilled />
                    </el-icon>
                  </span>
                  <template #dropdown v-if="route.params.id === store.getters.getUserInfo.id">
                    <el-dropdown-menu>
                      <el-dropdown-item :command="followUser.id">取消关注</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-card>
            </el-col>
          </el-row>

          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="pageInfo.currentPage" :page-sizes="[10, 20, 30, 40]" :page-size="pageInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total" class="search-result-pageination">
          </el-pagination>
        </el-card>
      </div>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { SYNC_GET, SYNC_POST } from '@/scripts/Axios';
import { MoreFilled } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus'


const route = useRoute()
const router = useRouter()
const store = useStore()

const activeIndex = ref('all')

const followInfo = ref(
[
    ]
)

const followId = ref([])

const pageInfo = {
  currentPage: 1,
  pageSize: 10,
  total: 10
}

// 监听 page size 改变的事件
function handleSizeChange(newSize: any) {
  pageInfo.pageSize = newSize
}

// 监听 页码值 改变的事件
function handleCurrentChange(newPage: any) {
  pageInfo.currentPage = newPage
}

async function gotoDetail(user_id: any) {
  if (user_id !== '' && user_id !== undefined) {
    //await router.push({name: 'user-info-article', params: {id: user_id}})
    location.href = '/#/user/' + user_id
  } else {
    router.push({ path: '/404' })
  }
}

async function getUserBasicInfo() {
  let newFollowInfo: any[] = []
  if (followId.value.length === 0) {
    followInfo.value = []
    return
  }
  for (let i = 0; i < followId.value.length; i++) {
    await SYNC_GET('/usr/getUserBasicInfo', {
      user_id: followId.value[i]
    }, response => {
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        let newInfo = {
          id: response.data.user_info.id,
          name: response.data.user_info.name,
          avatar: response.data.user_info.pictureUrl,
          identity: response.data.user_info.group,
          description: response.data.user_info.introduction
        }
        newFollowInfo.push(newInfo)
      } else {
        console.log(response)
      }
    })
  }
  followInfo.value = newFollowInfo
  console.log(newFollowInfo)
}

async function getUserFollowedList(key: string, keyPath: string[]) {
  console.log(key)
  let target_iden = key === 'all' ? ["专家", "学生"] : [key]
  await SYNC_GET('/usr/getAllFollowed', {
    user_id: route.params.id,
    target_identity: target_iden,
    page_num: pageInfo.currentPage,
    page_size: pageInfo.pageSize
  }, response => {
    if (response.status === 200 && response.data.statusMsg === 'Success.') {
      //console.log(response.data)
      followId.value = response.data.all_followed.list
      console.log(followId.value)
      getUserBasicInfo()
    } else {
      console.log(response)
    }
  })
}

getUserFollowedList('all', ['all'])

const handleCommand = (command: string | number | object) => {
    SYNC_POST('/usr/unfollowByUID', {
      token: store.getters.getToken,
      user_id: command
    }, response => {
      if (response.status === 200 && response.data.statusMsg === 'Success.') {
        ElMessage.info('取消关注成功')
        getUserFollowedList('all', ['all'])
        location.reload()
      } else {
        console.log(response)
      }
    })
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
  padding-right: 30px !important;
}

.followed-menu-item.is-active {
  color: #fafafa;
  background-color: #00a1d6;
}

.followed-menu-item:hover {
  color: #00a1d6;
  background-color: var(--el-color-info-light-9);
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

.followed-single:hover {
  cursor: pointer;
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
