<template>
    <div class="acc-manage-nav-bar">
        <NavBar />
    </div>
    <div class="acc-manage">
        <el-row class="acc-manage-row">
            <el-col :span="3">
                <AccManageBar />
            </el-col>
            <el-col :span="21" class="acc-manage-center"> <!--非常完美，不要改了-->
                <router-view />
            </el-col>
        </el-row>
    </div>
</template>
<style>
.acc-manage-nav-bar {
    margin: 0 50px;
}
.acc-manage {
    margin: 0 50px 0 0;
}
.acc-manage-row {
    flex-wrap: nowrap;
}
.acc-manage-center { /* 主界面 */
    margin: 0 50px 50px 50px;
}
.navbar {
    margin: 0 0;
}

</style>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '@/components/NavBar.vue'
import AccManageBar from '@/components/accountManage/AccManageBar.vue'
@Options({
  components: {
    NavBar,
    AccManageBar
  }
})
export default class UserCenterView extends Vue { }
</script>
