<script setup lang="ts"> 
import { ref } from "vue";
import { api } from "../http-api";
import { ImageType } from "../image";
import Image from "./Image.vue";

const imageList = ref<ImageType[]>([]);

api
  .getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch((e) => {
    console.log(e.message);
  });

const isActive = ref("");
const popupImg = ref("");
let CurrentId: number;

function ActiveImg(id: number) {
  CurrentId = id;
  api.getImage(id).then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      popupImg.value = reader.result as string;
    };
  });
  document.body.style.overflow = 'hidden';
  isActive.value = "active";
}

function Close() {
  document.body.style.overflow = 'auto';
  isActive.value = "";
  popupImg.value = "";
}

function Slide(index: number) {
  if (CurrentId + index < 0) {
    CurrentId = imageList.value.length - 1;
    ActiveImg(CurrentId);
  } else if (CurrentId + index > imageList.value.length - 1) {
    CurrentId = 0;
    ActiveImg(CurrentId);
  } else {
    CurrentId += index;
    ActiveImg(CurrentId);
  }
}
</script>

<template>
  <div class="body">
    <div :class="`popup-img ${isActive}`">
      <button class="close" v-on:click="Close()">
        <img src="../assets/close.png" alt="" />
      </button>
      <button class="arrow left" v-on:click="Slide(-1)">
        <img src="../assets/previous.png" alt="" />
      </button>
      <button class="arrow right" v-on:click="Slide(+1)">
        <img src="../assets/next.png" alt="" />
      </button>
      <div class="large-img">
        <img :src="`${popupImg}`" />
      </div>
    </div>
    <div>
      <h3>Gallery</h3>
      <div class="gallery-display">
        <Image
          v-for="image in imageList"
          :id="image.id"
          :key="image.name"
          v-on:click="ActiveImg(image.id)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>

img {
  height: 85vh;
}

.gallery-display {
  display: flex;
  margin-left:75px;
  margin-top:35px;
  flex-wrap: wrap;
  justify-content: center;
}

.popup-img {
  opacity: 0;
  position: fixed;
}

.popup-img.active {
  opacity: 1;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100.1%;
  height: 100.1%;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1;
  
}

.popup-img.active .close,
.popup-img.active .large-image,
.popup-img.active .arrow {
  opacity: 1;
  transition-delay: 1s;
}

.close {
  position:absolute;
  top: 5vh;
  right: 3vw;
  cursor: pointer;
  border: none;
  background: none;
}
.close img {
  width: 1.5vw;
  height: 1.5vw;
  opacity: 70;
  transition: 0.3s;
}

.close img:hover {
  transform: scale(1.25);
  transition: 0.3s;
}

.arrow {
  position: absolute;
  top: 50vh;
  right: 90vw;
  cursor: pointer;
  border: none;
  background: none;
}

.arrow img {
  width: 2vw;
  height: 2vw;
  opacity: 70;
  transition: 0.3s;
}

.arrow img:hover {
  transform: scale(1.3);
  transition: 0.3s;
}

.right {
  right: 4.5vw;
}

.left {
  left: 1.8vw;
}

.large-img {
  margin-top: 1%;
  overflow: auto;
}

</style>
