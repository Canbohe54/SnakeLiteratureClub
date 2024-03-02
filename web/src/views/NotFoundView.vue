<template>
    <logo-title />
    <div class="container">
        <h1>404 Not Found</h1>
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import LogoTitle from '@/components/LogoTitle.vue'
@Options({
  components: {
    LogoTitle
  }
})
export default class NotFoundView extends Vue {}
</script>