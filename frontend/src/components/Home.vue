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
    return image.name;
}

function showImageList(element:string){
  if(element == "selectimg"){
    document.getElementById("algo").hidden = true;
    document.getElementById("showImgIfClicked").hidden = false;
  }else if(element == "algo"){
    document.getElementById("showImgIfClicked").hidden = true;
    document.getElementById("algo").hidden = false;
  }
}

</script>

<template>
  <div id="home">
    <nav class="category">
      <button class="selectimg" v-on:click="showImageList('selectimg')">
        <img src="../assets/selectimg.png"/>
      </button>

      <button class="algo">
        <img src="../assets/algo.png" v-on:click="showImageList('algo')"/>
      </button>
      <!-- pour le bouton image ou algo -->
    </nav>

    <nav class="slidebar">
      <div id="showImgIfClicked">
        <div id="chooseImage" v-for="image in imageList" :key="image.id">
          <input type="radio" class="radio" v-model="selectedId" :value="image.id" @change="showImage" :id="`input-` + image.id">
            <ul class="home-ul">
              <li class="home-li">
                <label>
                  <img id="choosenImg" :src="`/images/`+ image.id"/>
                </label>
              </li>
          </ul> 
        </div>
      </div>
      <div id="algo" hidden>
        <div id="applyAlgo">
          <p id="wait" hidden>Transformation de l'image en cours...</p>
          <div id="chooseAlgo" v-for="algo in algoList" :key="algo.name">
            <input type="radio" class="radio" v-model="selectAlgo" :value="algo.name" @change="needParam">
              <ul>
                <li class="home-li">
                  <label>
                    <a>{{algo.name}}</a>
                  </label>
                </li>
            </ul> 
          </div>
          <!--<button @click="showImageWithAlgo">apply algo</button> !-->
        </div>
      </div>
    </nav>

    <nav class="option">
    </nav>
    <!-- <button @click="deleteImage">Delete the image</button> -->
    <div id="upload">
      <!-- <input type="file" id="file" ref="file" @change="handleFileUpload" /> -->
    </div>
    <div id="submit">
      <!-- <button @click="submitFile">Submit</button> -->
    </div>
    <div id="Form"></div>
    <div>
      <img id="createImage" />
    </div>
    
  </div>
</template>

<style scoped>

#home{
  display: flex;
  background: #1C1D26;
} 

.category{
  width : 3.64vw;
  min-width: 30px;
  height : 90.1vh;
  background : #242631;
  border-right: 1px solid #353948;
}

.selectimg, .algo{
  border: none;
  background: none;
  cursor: pointer;
}

.selectimg img{
  width: 1.8vw;
  min-width: 20px;
  margin-top: 4vh;
  margin-left: max(calc(0.4vw - 4px),-1px);
  opacity: 0.5;
}

.algo img{
  width: 1.5vw;
  min-width: 18px;
  margin-top: 5vh;
  margin-left: max(calc(0.6vw - 5px),0px);
  opacity: 0.5;
}

.algo img:hover, .selectimg img:hover{
  opacity: 1;
  transition: 0.3s;
}


.slidebar{
  width : 17.6vw;
  min-width: 120px;
  height : 90.1vh;
  background : #242631;
  border-right: 1px solid #353948;
  overflow-y:auto;
}

.slidebar::-webkit-scrollbar {
  width: 5px;
  height: 5px;
}
.slidebar::-webkit-scrollbar-track {
  background-color: #11171a88;
  border-radius: 10px;
}
.slidebar::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.249);
  border-radius: 10px;
}

.home-li{
  list-style-type: none;
}

#choosenImg{
  margin: 2%;
  float: left;
  object-fit: cover;
}

.home-li img{
  width: 90px;
  height: 90px;
  border-radius: 7px;
}

.option{
  margin-top: calc(90.1vh - 60px);
  height: 59px;
  width : 100vw;
  background : #242631;
  border-top: 1px solid #353948;
}

ul.home-ul {
  margin-block-start: 0;
  margin-block-end: 0;
  padding-inline-start: 15px;
}

ul.home-ul li.home-li a:hover {
  color: #95a5a6;
}

#chooseImage label{
  cursor: pointer;
}

input[type="radio"].radio {
  display: none;
}

</style>
