<template>
    <h2 class="passwd-change-title">正在进行账户注销</h2>
    <div>
        <router-view />
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
export default class AccountCancelView extends Vue { }
</script>
<style scoped>
.passwd-change-title {
    color: #b82020;
}
</style>
