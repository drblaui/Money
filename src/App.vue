<template>
  <v-app>
    <v-main>
      <Header/>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import Header from './components/Header.vue'

export default defineComponent({
  name: 'App',

  components: {
    Header
},
})
</script>
