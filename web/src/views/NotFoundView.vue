<template>
    <div class="notfound">
        <h1>404 Not Found</h1>
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
@Options({})
export default class NotFoundView extends Vue {}
</script>
<style>
    .notfound {
        margin: 0 50px;
    }
</style>
