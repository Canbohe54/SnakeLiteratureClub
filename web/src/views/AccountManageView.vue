<template>
    <el-scrollbar height="100vh">
        <div class="container">
            <div>
                <NavBar />
            </div>
            <div class="acc-manage">
                <el-row class="acc-manage-row">
                    <el-col :span="3" class="acc-manage-bar hidden-md-and-down">
                        <AccManageBar />
                    </el-col>
                    <el-col :span="21" class="acc-manage-center">
                        <router-view />
                    </el-col>
                </el-row>
            </div>
            <div>
                <NewAccountManage />
            </div>
        </div>
    </el-scrollbar>
</template>
<style>

.acc-manage {
    margin: 0 50px 0 0;
}

.acc-manage-row {
    flex-wrap: nowrap;
}

.acc-manage-center {
    /* 主界面 */
    margin: 0 0 50px 0;
}

.navbar {
    margin: 0 0;
}
</style>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from "@/components/NavBarResponsive.vue";
import NewAccountManage from "@/components/accountManage/NewAccountManage.vue";
@Options({
    components: {
        NavBar,
        NewAccountManage,
    }
})
export default class UserCenterView extends Vue { }
</script>
