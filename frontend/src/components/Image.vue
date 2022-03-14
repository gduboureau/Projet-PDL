<script setup lang="ts">
import { defineProps } from 'vue';
import { api } from '../http-api';

const props = defineProps<{ id: number }>()

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery");
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        galleryElt.appendChild(imgElt);
        if (imgElt !== null && reader.result as string) {
          imgElt.setAttribute("src", (reader.result as string));
        }
      }
    };
  })
  .catch(e => {
    console.log(e.message);
  });
</script>

<template>
  <figure id="gallery">
  </figure>  
</template>

<style>

img{
  width: 35vw;
  height: 35vw;;
}

.gallery-display img{
  margin: 2%;
  width: 18vw;
  height: 18vw;
  object-fit: cover;
}

</style>