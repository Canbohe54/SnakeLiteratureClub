<template>
    <h2 class="passwd-change-title">正在进行密码修改</h2>
    <div>
        <router-view />
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
export default class PasswdChangeView extends Vue { }
</script>
<style scoped>
.passwd-change-title {
    color: #b82020;
}
</style>
