<template>
  <logo-title />
  <div class="login">
    <LoginForm />
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import LogoTitle from '../components/LogoTitle.vue'
import LoginForm from '../components/user/LoginForm.vue'
@Options({
  components: {
    LoginForm,
    LogoTitle
  }
})
export default class LoginView extends Vue {}
</script>
<style>
/* .login {
  text-align: center;
  background-color: #fafafa;
  border-radius: 20px;
  width: 460px;
  position: absolute;
  padding: 20px 30px 40px 30px;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
} */
</style>
