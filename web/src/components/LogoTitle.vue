<template>
    <div class="logoTitle">
        <span class="logo"></span>
    <router-link to="/" class="titleLink">蛇拾文学社</router-link>
    </div>
</template>
<style>
.logoTitle {
    margin: 10px 50px 0 50px;
}
.logo {
  display: flex;
  float: left;
  height: 32px;
  width: 32px;
  margin: 0 10px 0 16px;
  background: url("../assets/logo.png") no-repeat center center;
  background-size: contain;
}
.titleLink {
    display: flex;
    float: left;
    text-decoration: none;
    color: #606266;
    font: 14px "Microsoft YaHei";
    margin-top: 8px;
}
</style>
