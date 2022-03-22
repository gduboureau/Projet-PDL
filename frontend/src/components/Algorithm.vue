<script setup lang="ts">
import { defineProps, ref } from "vue";
import { api } from "../http-api";
import router from "../router";

const param = ref("");
const props = defineProps<{ id: number }>();
const selectAlgo = ref("");

api
  .getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      const algoElt = document.getElementById("imgAlgo");
      if (algoElt !== null) {
        const imgElt = document.createElement("img");
        algoElt.appendChild(imgElt);
        if (imgElt !== null && (reader.result as string)) {
          imgElt.setAttribute("src", reader.result as string);
        }
      }
    };
  })
  .catch((e) => {
    console.log(e.message);
  });

function applyAlgo(prop: number, name: String, parameter: String) {
  api.getAlgo(prop, name, parameter);
}

function showImageWithAlgo() {
  var input = document.getElementById("myForm") as HTMLInputElement;
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  if (
    select == "Brightness" ||
    select == "ColorFilter" ||
    select == "meanFilter"
  ){
    param.value = "&p1=" + input.value;
    if (input.value == ""){
      alert("please enter a value");
      return;
    }
    applyAlgo(props.id, selectAlgo.value, param.value);
    router.push({ name: 'image', params: { id: props.id } });
  }else{
    applyAlgo(props.id, selectAlgo.value, param.value);
    router.push({ name: 'image', params: { id: props.id } });
  }
}

function needParam() {
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  if (
    (select == "Brightness" ||
      select == "ColorFilter" ||
      select == "meanFilter") &&
    document.getElementById("myForm") == null
  ) {
    const galleryElt = document.getElementById("Form");
    var x = document.createElement("input");
    if (galleryElt !== null) {
      x.setAttribute("id", "myForm");
      x.setAttribute("type", "number");
      x.setAttribute("value", "0");
      galleryElt.appendChild(x);
    }
  } else {
    if (
      document.getElementById("myForm") != null &&
      select != "Brightness" &&
      select != "ColorFilter" &&
      select != "meanFilter"
    )
      document.getElementById("myForm")!.remove();
  }
}

</script>

<template>
  <div id="imgAlgo">
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
    </div>
    <div id="Form"></div>
  </div>
</template>

<style>

img {
  height: 80vh;
}

</style>
