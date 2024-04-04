<template>
    <!-- 画大饼，此页面包含：稿件列表（草稿箱、审核结果、已公开作品、已刊登作品） -->
    <!-- 个人信息 -->
    <el-backtop target=".el-scrollbar__wrap" :bottom="100"></el-backtop>
    <el-row class="user-center">
        <el-col :lg="6" :md="7" :sm="24">
            <el-card class="visiting-card">
                <UserVisitingCard :userId="userId" />
            </el-card>
        </el-col>
        <el-col :lg="18" :md="17" :sm="24">
            <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <UserStatistics />
            </el-card>
            <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <ContributorList />
            </el-card>
            <el-card class="user-center-main" v-if="userIdentity === 'HUNTER'">
                <HunterList />
            </el-card>
<!--            <el-card class="user-center-main">-->
<!--                <el-text class="my-private-list">暂时充当private list</el-text>-->
<!--            </el-card>-->
            <div v-if="store.getters.getUserInfo.id === route.path.split('/')[2]">
              <el-card class="user-center-main" v-if="userIdentity === 'AUDITOR'">
                <AuditList/>
              </el-card>
                <el-card class="user-center-main" v-if="userIdentity === 'EXPERT' || userIdentity === 'HUNTER'">
                <ReceivingList />
                </el-card>
              <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <RoughList />
              </el-card>
              <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <BeingAudited />
              </el-card>
              <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <FailAudited />
              </el-card>
              <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <FailedReview />
              </el-card>
              <el-card class="user-center-main" v-if="userIdentity === 'CONTRIBUTOR' || userIdentity === 'TEACHER'">
                <PostRecord />
              </el-card>
            </div>
            <el-divider border-style="dashed" style="margin: 24px 20px;width: auto;"><el-text
                    type="info">我也是有底线的~</el-text></el-divider>
        </el-col>
    </el-row>
</template>

<script lang="ts" setup>
import UserVisitingCard from './UserVisitingCard.vue'
import UserStatistics from './UserStatistics.vue'
import ContributorList from './CenterArticleList/ContributorList.vue'
import {onMounted, ref} from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import AuditList from '@/components/user/CenterArticleList/AuditList.vue'
import {SYNC_GET} from '@/scripts/Axios'
import {errorCallback} from '@/scripts/ErrorCallBack'
import ReceivingList from "@/components/user/CenterArticleList/ReceivingList.vue";
import BeingAudited from "@/components/user/CenterArticleList/BeingAudited.vue";
import RoughList from "@/components/user/CenterArticleList/RoughList.vue";
import PostRecord from "@/components/user/CenterArticleList/PostRecord.vue";
import FailAudited from "@/components/user/CenterArticleList/FailAudited.vue";
import FailedReview from "@/components/user/CenterArticleList/FailedReview.vue";
import HunterList from "@/components/user/CenterArticleList/HunterList.vue";

const route = useRoute();
const store = useStore();
const userIdentity = ref('')
const userId = ref(route.params.id?route.params.id:store.getters.getUserInfo.id);

async function getUserIdentity() {
  await SYNC_GET('/usr/getUserIdentity', {
    user_id: route.params.id ? route.params.id : store.getters.getUserInfo.id
  }, async (response) => {
    if (response.status === 200 && response.data.code === 2001) {
      userIdentity.value = response.data.data
    }
    else {
      errorCallback(response)
    }
  })
}

onMounted(() => {
  getUserIdentity()
})

</script>

<style scoped>
.affix-to-top {
    position: fixed;
    right: 20px;
    bottom: 20px;
    z-index: 999;
}

.user-center {
    margin-top: 20px;
    display: flex;
    flex-direction: row-reverse;
}

.visiting-card {
    margin-right: 20px;
    border-radius: 10px;
}

@media screen and (max-width: 991px) {
    .visiting-card {
        margin: 0 20px 20px;
    }

}

.user-center-main {
    margin: 0 20px 20px;
    border-radius: 10px;
}
</style>
