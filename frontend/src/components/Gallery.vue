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
const CurrentId = ref(0);
const CurrentName = ref("");

function ActiveImg(id: number, name:string) {
  CurrentId.value = id;
  CurrentName.value = name;
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
  if (CurrentId.value + index < imageList.value[0].id) {
    ActiveImg(imageList.value[imageList.value.length - 1].id, imageList.value[imageList.value.length - 1].name);
  } else if (CurrentId.value + index > imageList.value[imageList.value.length - 1].id) {
    ActiveImg(imageList.value[0].id, imageList.value[0].name);
  } else {
    for (let i=0; i<imageList.value.length ; i++){
      if (CurrentId.value == imageList.value[i].id){
        ActiveImg(imageList.value[i+index].id, imageList.value[i+index].name)
        break;
      }
    }
  }
}

</script>

<template>
  <div class="body">
    <div :class="`popup-img ${isActive}`">
      <a :href="`/images/${CurrentId}`" :download="CurrentName">
        <button class="download">
          <img src="../assets/download.png" alt="Download" />
        </button>
      </a> 
      <button class="close" v-on:click="Close()">
        <img src="../assets/close.png" alt="Close" />
      </button>
      <button class="arrow left" v-on:click="Slide(-1)">
        <img src="../assets/previous.png" alt="ArrowLeft" />
      </button>
      <button class="arrow right" v-on:click="Slide(+1)">
        <img src="../assets/next.png" alt="ArrowRight" />
      </button>
      <div class="large-img">
        <img :src="`${popupImg}`" />
      </div>
    </div>
    <div>
      <div class="gallery-display">
        <Image
          v-for="image in imageList"
          :id="image.id"
          :key="image.name"
          v-on:click="ActiveImg(image.id, image.name)"
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
  margin-top:75px;
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
  width: 100.5%;
  height: 100.5%;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1;
  
}

.download, .close, .arrow{
  position:absolute;
  cursor: pointer;
  border: none;
  background: none;
}

.download img, .close img, .arrow img{
  opacity: 0.7;
  transition: 0.3s;
}

.download img:hover, .close img:hover, .arrow img:hover{
  transform: scale(1.25);
  opacity: 1;
  transition: 0.3s;
}

.download {
  top: 5vh;
  right: 6.5vw;
}
.download img {
  width: 1.7vw;
  height: 1.7vw;
}

.close {
  top: 5vh;
  right: 3vw;
}
.close img {
  width: 1.5vw;
  height: 1.5vw;
}

.arrow {
  top: 50vh;
  right: 90vw;
  
}

.arrow img {
  width: 2vw;
  height: 2vw;
  opacity: 1;
}

.arrow img:hover {
  transform: scale(1.3);
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
