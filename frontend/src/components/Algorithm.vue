<script setup lang="ts">
import { defineProps, ref } from "vue";
import { api } from "../http-api";
import router from "../router";
import Image from './Image.vue';

const param = ref("");
const props = defineProps<{ id: number }>();
const selectAlgo = ref("");


async function applyAlgo(prop: number, name: String, parameter: String) :Promise<Blob> {
  return api.getAlgo(prop, name, parameter);
}

async function deleteImage() :Promise<void>{
  await api.deleteImage(props.id);
  router.push({ name: 'home' });
}


async function showImageWithAlgo(){
  var input = document.getElementById("myForm") as HTMLInputElement;
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  if (select == "Brightness" || select == "ColorFilter" || select == "meanFilter"){
    param.value = "&p1=" + input.value;
    if (input.value != ""){
      await applyAlgo(props.id, selectAlgo.value, param.value);
      router.push({ name: 'gallery'});
    }else{
      alert("Entrer une valeur valide ")
      document.location.reload();
    }
  }else{
    await applyAlgo(props.id, selectAlgo.value, param.value);
    router.push({ name: 'gallery' });
  }
}

function needParam() {
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  if ((select == "Brightness" || select == "ColorFilter" || select == "meanFilter") && document.getElementById("myForm") == null){
    const galleryElt = document.getElementById("Form");
    var x = document.createElement("input");
    if (galleryElt !== null) {
      x.setAttribute("id", "myForm");
      x.setAttribute("type", "number");
      galleryElt.appendChild(x);
    }
  } else {
    if (document.getElementById("myForm") != null && select != "Brightness" && select != "ColorFilter" && select != "meanFilter")
      document.getElementById("myForm")!.remove();
  }
}

</script>

<template>
<div>
  <Image class="imgalg" :id="props.id"/>
  <div>
    <select id="algolist" v-model="selectAlgo" @change="needParam">
      <option id="Brightness">Brightness</option>
      <option id="Histogram">Histogram</option>
      <option id="meanFilter">meanFilter</option>
      <option id="GrayOutAColorImage">GrayOutAColorImage</option>
      <option id="ColorFilter">ColorFilter</option>
      <option id="gradientSobel">gradientSobel</option>
    </select>
  </div>
  <div>
    <button @click="showImageWithAlgo">apply algo</button>
    <button @click="deleteImage">Delete the image</button>
  </div>
  <div id="Form"></div>
</div>
</template>

<style>
.imgalg img{
  height: 80vh;
}
</style>