<template>
  <div class="container">
    <logo-title />
    <RegForm />
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import LogoTitle from '../components/LogoTitle.vue'
import RegForm from '../components/user/RegForm.vue'
@Options({
  components: {
    RegForm,
    LogoTitle
  }
})
export default class RegisterView extends Vue {}
</script>
<style>
</style>
