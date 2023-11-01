<template>
    <div class="email-change-view">
        <router-view />
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
export default class EmailChangeView extends Vue { }
</script>
<style scoped>
.email-change-view {
    margin: 71px 0 0 0;
}
</style>
