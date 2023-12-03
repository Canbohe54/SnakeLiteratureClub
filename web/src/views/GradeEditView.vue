<template>
  <div class="gradeEdit">
    <NavBar class="navbar" /><!--导航栏-->
    <GradeEditor class="graedit"/>

  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import GradeEditor from "@/components/grade/GradeEditor.vue";
@Options({
  components: {
    NavBar,
    GradeEditor
  }
})
export default class GradeEditView extends Vue { }
</script>
<style>
.artedit {
  margin: 20px 50px;
}

.navbar {
  margin: 0 0;
}
</style>
