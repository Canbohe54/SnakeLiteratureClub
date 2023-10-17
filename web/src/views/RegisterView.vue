<template>
  <logo-title />
  <div class="register">
    <h1>账号注册</h1>
    <RegisterForm />
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import LogoTitle from '../components/LogoTitle.vue'
import RegisterForm from '../components/RegisterForm.vue'
@Options({
  components: {
    RegisterForm,
    LogoTitle
  }
})
export default class RegisterView extends Vue {}
</script>
<style>
.register {
  text-align: center;
  background-color: #fafafa;
  border-radius: 20px;
  width: 600px;
  position: absolute;
  padding: 20px 30px 40px 30px;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
}
</style>
