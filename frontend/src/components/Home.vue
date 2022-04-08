<script setup lang="ts">
import { ref } from "vue";
import { api } from "../http-api";
import { ImageType } from "../image";
import { AlgoTypes } from "../algorithms";

const CurrentId = ref(-1);
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

async function handleFileUpload(event: Event) {
  target.value = (event.target as HTMLInputElement);
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    await api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.value.value = '';
    }).catch(e => {
      console.log(e.message);
      alert("Error: 415 Unsupported Media Type");
    });
  }
  location.reload();
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

function showImage(id:number) {
  CurrentId.value = id;
  document.getElementById("createImage")!.setAttribute("src", "images/" + id);
}

async function applyAlgo(prop: number,name: String,parameter: String): Promise<Blob> {
  return api.getAlgo(prop, name, parameter);
}

async function deleteImage(): Promise<void> {
  await api.deleteImage(CurrentId.value);
  location.reload();
}

/*async function showImageWithAlgo() {
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
  await applyAlgo(CurrentId.value, selectAlgo.value, param.value);
  document!.getElementById("wait")!.hidden = true;
  document.getElementById("createImage")!.setAttribute("src","images/"+CurrentId.value+"?algorithm="+selectAlgo.value+param.value);
}*/

/*function needParam() {
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
}*/

async function showImageWithAlgo() {
  if (selectAlgo.value != "" && CurrentId.value != -1){
    const val = ref("");
    if (selectAlgo.value == "ColorFilter" || selectAlgo.value == "meanFilter" || selectAlgo.value == "Brightness"){
      val.value = "&p1=" + (document.getElementById('range' +selectAlgo.value) as HTMLInputElement).value;
    }else{
      val.value = "";
    }
    console.log(val.value);
    document!.getElementById("wait")!.hidden = false;
    await applyAlgo(CurrentId.value, selectAlgo.value, val.value);
    document!.getElementById("wait")!.hidden = true;
    document.getElementById("createImage")!.setAttribute("src","images/"+CurrentId.value+"?algorithm="+selectAlgo.value+val.value);
  }
}

function showcategory(element:string){
  if(element == "selectimg"){
    document.getElementById("algo")!.hidden = true;
    document.getElementById("showImgIfClicked")!.hidden = false;
    document.getElementById("uploadimg")!.hidden = false;
    document.getElementById("slidebar")!.style.height = "calc(90.1vh - 60px)";
  }else if(element == "selectalgo"){
    document.getElementById("showImgIfClicked")!.hidden = true;
    document.getElementById("algo")!.hidden = false;
    document.getElementById("slidebar")!.style.height = "90.1vh";
    document.getElementById("uploadimg")!.hidden = true;
  }
}

function selectedAlgo(name: string){
  if (selectAlgo.value == name){
    document.getElementById("name"+name)!.style.opacity = "0.5";
    selectAlgo.value = "";
    return;
  } 
  selectAlgo.value = name;
  for (let i = 0; i < algoList.value.length; i++){
    if (name != algoList.value[i].name){
      document.getElementById("name"+algoList.value[i].name)!.style.opacity = "0.5";
    }
  }
  document.getElementById("name"+name)!.style.opacity = "1";
  var slider = document.getElementById("range"+name) as HTMLInputElement;
  if (name == "ColorFilter"){
    slider.setAttribute("min","0");
    slider.setAttribute("max","360");
    slider.defaultValue = "0";
  }else if(name == "meanFilter"){
    slider.setAttribute("min","1");
    slider.setAttribute("max","20");
    slider.defaultValue = "1";
  }else if (name == "Brightness"){
    slider.defaultValue = "0";
  }
}

function rangeSlide(name: string) {
  const val = (document.getElementById('range' +name) as HTMLInputElement).value;
  document.getElementById('rangeValue'+name)!.innerHTML = val;
}

</script>

<template>

  <body id="home">

    <nav class="category">
      <button class="selectimg" v-on:click="showcategory('selectimg')">
        <img src="../assets/selectimg.png"/>
      </button>

      <button class="selectalgo" v-on:click="showcategory('selectalgo')">
        <img src="../assets/selectalgo.png"/>
      </button>
    </nav>


    <nav class="slidebar" id="slidebar">

      <div id="showImgIfClicked">
        <div class="listImg" v-for="image in imageList" :key="image.id">
          <img class="Imginlist" :src="`/images/`+ image.id" v-on:click="showImage(image.id)"/>
        </div>
      </div>

      <div id="algo" hidden>
        <div id="applyAlgo">
          <p id="wait" hidden>Transformation de l'image en cours...</p>
          <div id="chooseAlgo" v-for="algo in algoList" :key="algo.name">
            <div class="c">
              <input type="checkbox" class="checkbox" :id="`faq-1`+algo.name" v-on:click="selectedAlgo(algo.name)">
              <h1 :id="`name`+algo.name">
                <label class="inputlabel" :for="`faq-1`+algo.name">
                  {{algo.name}}
                </label>
              </h1>
              <div class="p" :id="`p`+algo.name">
                <div v-if="algo.name == `Brightness` || algo.name == `ColorFilter` || algo.name == `meanFilter`">
                  <span :id="`rangeValue`+algo.name"></span>
                  <input :id="`range` + algo.name" type="range" min="-255" max="255" v-on:Change="rangeSlide(algo.name)"> 
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <nav class="uploadimg" id="uploadimg">
      <div id="upload">
        <label class="custom-file-upload">
          <input type="file" id="file" ref="file" @change="handleFileUpload" />
          <i class="fa fa-cloud-upload"></i> Ajouter une image
          <img src="../assets/addimg.png" />
        </label>
      </div>
    </nav>

     <nav class="uploadimg" id="uploadimg">
      <label class="buttonupload">
        <input type="file" id="file" ref="file" @change="handleFileUpload"/>
        Add Image
      </label>
    </nav>

    <nav class="Imgdisplayer">
      <img id="createImage" />
    </nav>

    <nav class="option">
      <button @click="showImageWithAlgo">apply algo</button>
    </nav>
    
  </body>
  
</template>

<style scoped>

body{
  display: flex;
} 

.category{
  z-index:2;
  width : 3.64vw;
  min-width: 30px;
  height : 90.1vh;
  background : #242631;
  border-right: 1px solid #353948;
}

.selectimg, .selectalgo{
  border: none;
  background: none;
}

.selectimg img{
  width: 1.8vw;
  min-width: 20px;
  margin-top: 4vh;
  margin-left: max(calc(0.4vw - 4px),-1px);
  opacity: 0.5;
  transition: 0.3s;
  cursor: pointer;
}

.selectalgo img{
  width: 1.5vw;
  min-width: 18px;
  margin-top: 5vh;
  margin-left: max(calc(0.6vw - 5px),0px);
  opacity: 0.5;
  transition: 0.3s;
  cursor: pointer;
}

.selectalgo img:hover, .selectimg img:hover{
  opacity: 1;
  transition: 0.3s;
}

div.c{
  position: relative;
  margin-left:2em;
}
.checkbox{
  left: 0;
  top: 0;
  height: 100%;
  width: 100%;
  opacity:0;
  visibility: 0;
}

.inputlabel{
  cursor: pointer;
  position: relative;
}

h1{
  background-color: #242631;
  color: rgb(243, 243, 243);
  width: 100%;
  text-align: left;
  font-size: 15px;
  opacity: 0.5;
}

div.p{
  max-height:0px;
  overflow: hidden;
  transition:max-height 0.5s;
  background-color: #242631;
}

.checkbox:checked ~ h1 ~ div.p{
  max-height:100px;
}


.slidebar{
  z-index:1;
  width : 17.6vw;
  min-width: 120px;
  height : calc(90.1vh - 60px);
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

.listImg{
  margin-block-start: 0;
  margin-block-end: 0;
  padding-inline-start: 15px;
}

.Imginlist{
  width: 90px;
  height: 90px;
  margin: 2%;
  float: left;
  object-fit: cover;
  cursor: pointer;
  border-radius: 7px;
}

.uploadimg{
  z-index:1;
  background: #242631;
  width : 17.75vw;
  min-width: 120px;
  height: 59px;
  border-top: 1px solid #353948;
  margin-top: calc(90.1vh - 60px);
  margin-left: min(-14.7vw,-121px);
  border-right: 1px solid #353948;
}

input[type="file"] {
  display: none;
}

.uploadimg{
  text-align: center;
  font-size: 1.7vw;
}

.buttonupload {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.678);
  background: #353948;
  border: 2px solid #353948;
  border-radius: 20px;
  padding: 10px 10px;
  display: inline-block;
  font-size: 12px;
  letter-spacing: 1px;
  cursor: pointer;
  box-shadow: inset 0 0 0 0 #1C1D26;
  -webkit-transition: ease-out 0.5s;
  -moz-transition: ease-out 0.5s;
  transition: ease-out 0.5s;
}

.buttonupload:hover {
  box-shadow: inset 0 -100px 0 0 #1C1D26;
}

.Imgdisplayer{
  position:absolute;
  background : #1C1D26;
  height : calc(90.1vh - 60px);
  width: calc(100vw - 150px);
  max-width: 82.6vw;
  margin-left: max(17.4vw, 150px);
  display: flex;
  justify-content: center;
  align-items: center;
}

.Imgdisplayer img{
  max-height: calc(75vh - 50px);
  min-height: 20px;
  max-width: 65vw;
}

.option{
  z-index: 1;
  margin-top: calc(90.1vh - 60px);
  height: 59px;
  width : 100vw;
  background : #242631;
  border-top: 1px solid #353948;
}

</style>
