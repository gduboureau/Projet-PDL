<script setup lang="ts">
import { defineProps } from "vue";
import { api } from "../http-api";

const props = defineProps<{ id: number }>();

api
  .getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery-" + props.id);
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        galleryElt.appendChild(imgElt);
        if (imgElt !== null && (reader.result as string)) {
          imgElt.setAttribute("src", reader.result as string);
        }
      }
    };
  })
  .catch((e) => {
    console.log(e.message);
  });
</script>

<template>
  <figure :id="`gallery-` + id"></figure>
</template>

<style>

.gallery-display img {
  outline: 1.8vh solid rgba(0, 0, 0, 0.2);
  outline-offset: -1.8vh;
  min-height: 50vh;
  max-height: 50vh;
  transition: all 0.2s;
  cursor: pointer;
  margin-left:-75px;
  margin-top:-31px;
}

.gallery-display img:hover {
  outline: 2.3vh solid rgba(0, 0, 0, 0.3);
  outline-offset: -2.3vh;
}

img {
  height: 80vh;
}

</style>
