<template>
    <div class="search">
        <NavBar class="navbar" /><!--导航栏-->
        <SearchBar />
    </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import NavBar from '../components/NavBar.vue'
import SearchBar from '../components/SearchBar.vue'
@Options({
  components: {
    NavBar,
    SearchBar
  }
})
export default class SearchView extends Vue { }
</script>
<style>
.search {
    margin: 0 50px;
}

.navbar {
    margin: 0 0;
}
</style>
