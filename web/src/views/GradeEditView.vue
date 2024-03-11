<template>
  <div class="gradeEdit">
    <NavBar class="navbar" /><!--导航栏-->
    <GradeEditor class="graedit"/>
    <GradeDisplay class="graDis"/>
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBarOld.vue'
import GradeEditor from "@/components/grade/GradeEditor.vue";
import GradeDisplay from "@/components/grade/GradeDisplay.vue";
@Options({
  components: {
    NavBar,
    GradeEditor,
    GradeDisplay
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
