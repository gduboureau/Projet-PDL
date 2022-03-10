<script setup lang="ts">
import { ref } from 'vue'
import { api } from '../http-api';
import { ImageType } from '../image'

const imageList = ref<ImageType[]>([]);

api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });
</script>

<template>
  <div id="gallery">
    <h3>Gallery</h3>
    <Image v-for="image in imageList" :key="image" v-bind:src="'images/' + image.id" />
  </div>
</template>

<style scoped>

</style>
