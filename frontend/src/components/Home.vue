<script setup lang="ts">
import { ref } from "vue";
import router from "../router";
import { api } from "../http-api";
import { ImageType } from "../image";
import Image from "./Image.vue";
import { AlgoTypes } from "../algorithms";

const param = ref("");
const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
getImageList();
const selectAlgo = ref("");
const algoList = ref<AlgoTypes[]>([]);

getAlgoList();

function getAlgoList() {
  api
    .getAlgoList()
    .then((data) => {
      algoList.value = data;
    })
    .catch((e) => {
      console.log(e.message);
    });
}
function getImageList() {
  api
    .getImageList()
    .then((data) => {
      imageList.value = data;
    })
    .catch((e) => {
      console.log(e.message);
    });
}

function showImage() {
  var id = document.getElementById("createImage");
  id.setAttribute("src", "images/" + selectedId.value);
}

async function applyAlgo(
  prop: number,
  name: String,
  parameter: String
): Promise<Blob> {
  return api.getAlgo(prop, name, parameter);
}

async function deleteImage(): Promise<void> {
  await api.deleteImage(selectedId.value);
  location.reload();
}

async function showImageWithAlgo() {
  var input = document.getElementById("myForm") as HTMLInputElement;
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  if (algo.options[algo.selectedIndex] === undefined) return;
  var select = algo.options[algo.selectedIndex].text;
  if (
    select == "Brightness" ||
    select == "ColorFilter" ||
    select == "meanFilter"
  ) {
    param.value = "&p1=" + input.value;
    if (input.value == "") {
      alert("Vous devez rentrer une valeur.");
      document.location.reload();
      return;
    } else if (
      select == "ColorFilter" &&
      (parseInt(input.value) > 360 || parseInt(input.value) < 0)
    ) {
      alert("La valeur de teinte doit être comprise entre 0 et 360.");
      document.location.reload();
      return;
    } else if (select == "meanFilter" && parseInt(input.value) < 0) {
      alert("La valeur de flou doit être supérieure à 0.");
      document.location.reload();
      return;
    }
    document.getElementById("myForm")!.remove();
  }
  document!.getElementById("wait")!.hidden = false;
  await applyAlgo(selectedId.value, selectAlgo.value, param.value);
  document!.getElementById("wait")!.hidden = true;
}

function needParam() {
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  for (let i = 0; i < algoList.value.length; i++) {
    if (
      select == algoList.value[i].name &&
      algoList.value[i].hasParameters == true &&
      document.getElementById("myForm") == null
    ) {
      const galleryElt = document.getElementById("Form");
      var x = document.createElement("input");
      if (galleryElt !== null) {
        x.setAttribute("id", "myForm");
        x.setAttribute("type", "number");
        galleryElt.appendChild(x);
        return;
      }
    } else {
      if (
        document.getElementById("myForm") != null &&
        algoList.value[i].hasParameters == false &&
        select == algoList.value[i].name
      ) {
        document.getElementById("myForm")!.remove();
      }
    }
  }
}
</script>

<template>
  <div id="home">
    <h3>Choose an image</h3>
    <div id="chooseImage" v-for="image in imageList" :key="image.id">
      <input
        type="radio"
        v-model="selectedId"
        :value="image.id"
        @change="showImage"
        :id="`input-` + image.id"
      />
      {{ image.name }}
      <img id="createImage" />
    </div>
    <div id="applyAlgo">
      <p id="wait" hidden>Transformation de l'image en cours...</p>
      <select id="algolist" v-model="selectAlgo" @change="needParam">
        <option v-for="algo in algoList" :value="algo.name" :key="algo.name">
          {{ algo.name }}
        </option>
      </select>
      <button @click="showImageWithAlgo">apply algo</button>
      <button @click="deleteImage">Delete the image</button>
    </div>
    <div id="Form"></div>
  </div>
</template>

<style scoped>
#createImage {
  float: right;
  height: 80vh;
}

#home {
  text-align: left;
}


</style>
