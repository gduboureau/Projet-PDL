<script setup lang="ts">
import { defineProps, ref } from "vue";
import { AlgoTypes } from "../algorithms";
import { api } from "../http-api";
import { ImageType } from "../image";
import router from "../router";
import Image from './Image.vue';

const param = ref("");
const props = defineProps<{ id: number }>();
const selectAlgo = ref("");
const algoList = ref<AlgoTypes[]>([]);

getAlgoList();

function getAlgoList() {
  api.getAlgoList().then((data) => {
    algoList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

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
  if (algo.options[algo.selectedIndex] === undefined) return;
  var select = algo.options[algo.selectedIndex].text;
  if (select == "Brightness" || select == "ColorFilter" || select == "meanFilter"){
    param.value = "&p1=" + input.value;
    if (input.value == ""){
      alert("Vous devez rentrer une valeur.")
      document.location.reload();
      return;
    }else if (select == "ColorFilter" && (parseInt(input.value) > 360 || parseInt(input.value) < 0)){
      alert("La valeur de teinte doit être comprise entre 0 et 360.")
      document.location.reload();
      return;
    }else if (select == "meanFilter" && parseInt(input.value) < 0){
      alert("La valeur de flou doit être supérieure à 0.")
      document.location.reload();
      return;
    }
    document.getElementById("myForm")!.remove();
  }
  document!.getElementById("wait")!.hidden = false;
  await applyAlgo(props.id, selectAlgo.value, param.value);
  document!.getElementById("wait")!.hidden = true;
  router.push({ name: 'gallery' });
}

function needParam() {
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  for (let i = 0; i < algoList.value.length; i++){
    if (select == algoList.value[i].name && algoList.value[i].hasParameters == true && document.getElementById("myForm") == null){
      const galleryElt = document.getElementById("Form");
      var x = document.createElement("input");
      if (galleryElt !== null) {
        x.setAttribute("id", "myForm");
        x.setAttribute("type", "number");
        galleryElt.appendChild(x);
        return;
      }
    }else{
      if (document.getElementById("myForm") != null && algoList.value[i].hasParameters == false && select == algoList.value[i].name){
        document.getElementById("myForm")!.remove();
      }
    }
  }
}

</script>

<template>
<div>
  <Image class="imgalg" :id="props.id"/>
  <div>
    <p id="wait" hidden>Transformation de l'image en cours...</p>
    <select id="algolist" v-model="selectAlgo" @change="needParam">
      <option v-for="algo in algoList" :value="algo.name" :key="algo.name">{{ algo.name }}</option>
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