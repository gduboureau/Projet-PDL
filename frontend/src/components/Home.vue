<script setup lang="ts">
import { ref } from "vue";
import { api } from "../http-api";
import { ImageType } from "../image";
import { AlgoTypes } from "../algorithms";

const param = ref("");
const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
const selectAlgo = ref("");
const algoList = ref<AlgoTypes[]>([]);
const target = ref<HTMLInputElement>();

getImageList();
getAlgoList();

function submitFile() {
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.value.value = '';
    }).catch(e => {
      console.log(e.message);
      alert("Error: 415 Unsupported Media Type");
    });
  }
  location.reload();
}

function handleFileUpload(event: Event) {
  target.value = (event.target as HTMLInputElement);
}

function getAlgoList() {
  api.getAlgoList().then((data) => {
      algoList.value = data;
  })
  .catch((e) => {
    console.log(e.message);
  });
}
function getImageList() {
  api.getImageList().then((data) => {
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

async function applyAlgo(prop: number,name: String,parameter: String): Promise<Blob> {
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
  if (select == "Brightness" || select == "ColorFilter" || select == "meanFilter") {
    param.value = "&p1=" + input.value;
    if (input.value == "") {
      alert("Vous devez rentrer une valeur.");
      document.location.reload();
      return;
    } else if (select == "ColorFilter" && (parseInt(input.value) > 360 || parseInt(input.value) < 0)) {
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
  document.getElementById("createImage").setAttribute("src","images/"+selectedId.value+"?algorithm="+selectAlgo.value+param.value);
}

function needParam() {
  const algo = document.getElementById("algolist") as HTMLSelectElement;
  var select = algo.options[algo.selectedIndex].text;
  for (let i = 0; i < algoList.value.length; i++) {
    if (select == algoList.value[i].name && algoList.value[i].hasParameters == true && document.getElementById("myForm") == null) {
      const galleryElt = document.getElementById("Form");
      var x = document.createElement("input");
      if (galleryElt !== null) {
        x.setAttribute("id", "myForm");
        x.setAttribute("type", "number");
        galleryElt.appendChild(x);
        return;
      }
    } else {
      if (document.getElementById("myForm") != null && algoList.value[i].hasParameters == false && select == algoList.value[i].name) {
        document.getElementById("myForm")!.remove();
      }
    }
  }
}

function getImageName(image: ImageType){
  if(image.name.length > 17){let newName = image.name.substring(0, image.name.length - (image.name.length-17));
    newName = newName.concat("...");
    return newName;
  }else{
    return image.name;
  } 
}
</script>

<template>
  <div id="home">
    <nav class="list-image">
      <div id="chooseImage" v-for="image in imageList" :key="image.id">
        <input type="radio" class="demo1" v-model="selectedId" :value="image.id" @change="showImage" :id="`input-` + image.id">
        <ul class="home-ul">
          <li class="home-li">
            <label :for="`input-` + image.id" >
              <a>{{ getImageName(image) }}</a>
            </label>
          </li>
        </ul>
      </div>
    </nav>
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
    <div>
      <input type="file" id="file" ref="file" @change="handleFileUpload" />
    </div>
    <div>
      <button @click="submitFile">Submit</button>
    </div>
    <div id="Form"></div>
    <div>
      <img id="createImage" />
    </div>
  </div>
</template>

<style scoped>

#home{
  border-left: 200px solid black;
  display: inline-block;
}

#createImage{
  height: 80vh;
  float:right;
}

body {
  height: 100vh;
}

nav.list-image {
  float: left;
}

ul.home-ul {
  display: flex;
  align-items: start;
  list-style-type: none;

}
ul.home-ul li.home-li {
  padding: 6px 0;
  margin-left: -200px;
}
ul.home-ul li.home-li a {
  position: relative;
  display: block;
  padding: 4px 0;
  color: #ecf0f1;
  transition: 0.5s;
}
ul.home-ul li.home-li a::after {
  position: absolute;
  content: "";
  top: 100%;
  left: 0;
  width: 100%;
  height: 3px;
  background: #ffffff;
  transform: scaleX(0);
  transform-origin: right;
  transition: transform 0.5s;
}
ul.home-ul li.home-li a:hover {
  color: #95a5a6;
}
ul.home-ul li.home-li a:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}

#chooseImage label{
  cursor: pointer;
}

input[type="radio"].demo1 {
  display: none;
}


</style>
