<script setup lang="ts">
import { ref } from "vue";
import { api } from "../http-api";
import { ImageType } from "../image";
import { AlgoTypes } from "../algorithms";

const CurrentId = ref(-1);
const CurrentName = ref("")
const srcImage = ref("");
const imageList = ref<ImageType[]>([]);
const selectAlgo = ref("");
const algoList = ref<AlgoTypes[]>([]);
const target = ref<HTMLInputElement>();
const listColor = ["purple","cold","hot","green","blue","yellow","red","orange"];
const sideList = ["right","left"];
const color = ref("");
const side = ref("");

getImageList();
getAlgoList();

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
  getImageList();
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

function showImage(id:number, name:string) {
  CurrentId.value = id;
  CurrentName.value = name
  api.getImage(id).then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      document.getElementById("Displayerinfo")!.hidden = true;
      document.getElementById("createImage")!.hidden = false;
      document.getElementById("buttondelete")!.hidden = false;
      document.getElementById("buttondownload")!.hidden = false;
      srcImage.value = reader.result as string
    };
  });
}

async function applyAlgo(prop: number,name: String,parameter: String): Promise<Blob> {
  return api.getAlgo(prop, name, parameter);
}

async function deleteImage(): Promise<void> {
  CurrentId.value = -1; 
  document.getElementById("createImage")!.hidden = true;
  document.getElementById("Displayerinfo")!.hidden = false;
  document.getElementById("buttondelete")!.hidden = true;
  document.getElementById("buttondownload")!.hidden = true;
  await api.deleteImage(CurrentId.value);
  getImageList()
}


async function showImageWithAlgo() {
  if (CurrentId.value == -1){
    alert("Please choose an Image");
  }
  if (selectAlgo.value == ""){
    alert("Please choose an Algorithm");
  }
  if (selectAlgo.value != "" && CurrentId.value != -1){
    const val = ref("");
    if (selectAlgo.value == "ColorFilter" || selectAlgo.value == "MeanFilter" || selectAlgo.value == "Brightness"){
      val.value = "&p1=" + (document.getElementById('range' +selectAlgo.value) as HTMLInputElement).value;
    }else if (selectAlgo.value == "SideGray"){
      val.value = "&p1=" + side.value;
    }else if (selectAlgo.value == "KeepColor"){
      val.value = "&p1=" + color.value;
    }else{
      val.value = "";
    }
    document.getElementById("wait")!.setAttribute("style","z-index:4");
    await applyAlgo(CurrentId.value, selectAlgo.value, val.value);
    srcImage.value = "images/"+CurrentId.value+"?algorithm="+selectAlgo.value+val.value;
    document.getElementById("Imginlist-"+CurrentId.value)!.setAttribute("src", "/images/" + CurrentId.value + "?algorithm=" + selectAlgo.value + val.value);
    await new Promise(r => setTimeout(r, 3000));
    document.getElementById("wait")!.setAttribute("style","z-index:-1");
  }
}


function resetAlgoPanel(){
  for (let i = 0; i < algoList.value.length; i++){
    document.getElementById("name"+algoList.value[i].name)!.style.opacity = "0.5";
    document.getElementById("showParam"+algoList.value[i].name)!.setAttribute("style","max-height:0px");
    selectAlgo.value = "";
    if (algoList.value[i].name == "Brightness" || algoList.value[i].name == "ColorFilter" || algoList.value[i].name == "MeanFilter") {
      (document.getElementById("range"+algoList.value[i].name) as HTMLInputElement).value = "0";
      if (algoList.value[i].name == "MeanFilter"){
        document.getElementById('rangeValue'+algoList.value[i].name)!.innerHTML = "1";
      }else{
        document.getElementById('rangeValue'+algoList.value[i].name)!.innerHTML = "0";
      }
    }
  }
  for(let j = 0; j < sideList.length; j++){
    if ((document.getElementById("sideParam"+sideList[j]) as HTMLInputElement).checked == true){
      (document.getElementById("sideParam"+sideList[j]) as HTMLInputElement).checked = false;
    }
  }
  for(let j = 0; j < listColor.length; j++){
    if ((document.getElementById("colorParam"+listColor[j]) as HTMLInputElement).checked == true){
      (document.getElementById("colorParam"+listColor[j]) as HTMLInputElement).checked = false;
    }
  }
}

function showcategory(element:string){
  if(element == "selectimg"){
    document.getElementById("algo")!.hidden = true;
    document.getElementById("showImgIfClicked")!.hidden = false;
    document.getElementById("slidebar")!.style.height = "calc(90.1vh - 60px)";
    document.getElementById("selectalgo")!.style.opacity = "0.5";
    document.getElementById("selectimg")!.style.opacity = "1";
    document.getElementById("uploadimg")!.hidden = false;
    document.getElementById("buttonapply")!.hidden = true;
    resetAlgoPanel();
  }else if(element == "selectalgo"){
    document.getElementById("showImgIfClicked")!.hidden = true;
    document.getElementById("algo")!.hidden = false;
    document.getElementById("slidebar")!.style.height = "90.1vh";
    document.getElementById("selectimg")!.style.opacity = "0.5";
    document.getElementById("selectalgo")!.style.opacity = "1";
    document.getElementById("uploadimg")!.hidden = true;
    document.getElementById("buttonapply")!.hidden = false;
  }
}

function selectedAlgo(name: string){
  if (selectAlgo.value == name){
    document.getElementById("name"+name)!.style.opacity = "0.5";
    document.getElementById("showParam"+name)!.setAttribute("style","max-height:0px");
    selectAlgo.value = "";
    return;
  } 
  selectAlgo.value = name;
  for (let i = 0; i < algoList.value.length; i++){
    if (name != algoList.value[i].name){
      document.getElementById("name"+algoList.value[i].name)!.style.opacity = "0.5";
      document.getElementById("showParam"+algoList.value[i].name)!.setAttribute("style","max-height:0px");
      if (algoList.value[i].name == "Brightness" || algoList.value[i].name == "ColorFilter" || algoList.value[i].name == "MeanFilter") {
        (document.getElementById("range"+algoList.value[i].name) as HTMLInputElement).value = "0";
        if (algoList.value[i].name == "MeanFilter"){
          document.getElementById('rangeValue'+algoList.value[i].name)!.innerHTML = "1";
        }else{
          document.getElementById('rangeValue'+algoList.value[i].name)!.innerHTML = "0";
        }
      }
      if (algoList.value[i].name == "SideGray"){
      for(let j = 0; j < sideList.length; j++){
        if ((document.getElementById("sideParam"+sideList[j]) as HTMLInputElement).checked == true){
          (document.getElementById("sideParam"+sideList[j]) as HTMLInputElement).checked = false;
          }
        }
      }
      if (algoList.value[i].name == "KeepColor"){
        for(let j = 0; j < listColor.length; j++){
          if ((document.getElementById("colorParam"+listColor[j]) as HTMLInputElement).checked == true){
            (document.getElementById("colorParam"+listColor[j]) as HTMLInputElement).checked = false;
          }
        }
      }
    }
  }
  document.getElementById("name"+name)!.style.opacity = "1";
  var slider = document.getElementById("range"+name) as HTMLInputElement;
  if (name == "ColorFilter"){
    slider.setAttribute("min","0");
    slider.setAttribute("max","359");
    slider.defaultValue = "0";
    document.getElementById('rangeValue'+name)!.innerHTML = "0";
    document.getElementById("showParam"+name)!.setAttribute("style","max-height:300px")
  }else if(name == "MeanFilter"){
    slider.setAttribute("min","1");
    slider.setAttribute("max","9");
    slider.defaultValue = "1";
    document.getElementById('rangeValue'+name)!.innerHTML = "0";
    document.getElementById("showParam"+name)!.setAttribute("style","max-height:300px");
    slider.step = "2";
  }else if (name == "Brightness"){
    slider.defaultValue = "0";
    document.getElementById('rangeValue'+name)!.innerHTML = "0";
    document.getElementById("showParam"+name)!.setAttribute("style","max-height:300px");
  }else if (name == "SideGray" || name == "KeepColor"){
    document.getElementById("showParam"+name)!.setAttribute("style","max-height:300px");
  }
}

function rangeSlide(name: string) {
  const val = (document.getElementById('range' +name) as HTMLInputElement).value;
  document.getElementById('rangeValue'+name)!.innerHTML = val;
} 

function setColor(selectedColor: string){
  color.value = selectedColor;
}

function setSide(selectSide: string){
  side.value = selectSide;
}

</script>

<template>

  <body id="home">

    <nav class="category">
      <button class="selectimg" v-on:click="showcategory('selectimg')">
        <img id = "selectimg" src="../assets/selectimg.png"/>
      </button>

      <button class="selectalgo" v-on:click="showcategory('selectalgo')">
        <img id="selectalgo" src="../assets/selectalgo.png"/>
      </button>
    </nav>


    <nav class="slidebar" id="slidebar">

      <div class="showImgIfClicked" id="showImgIfClicked">
        <div class="listImg" v-for="image in imageList" :key="image.id">
          <img class="Imginlist" :id="`Imginlist-` + image.id" :src="`/images/`+ image.id" v-on:click="showImage(image.id, image.name)"/>
        </div>
      </div>

      <div id="algo" hidden>
        <div id="applyAlgo">
          <div id="chooseAlgo" v-for="algo in algoList" :key="algo.name">
            <div class="c">
              <input type="checkbox" class="algorithmsname" :id="`algorithmsname`+algo.name" v-on:click="selectedAlgo(algo.name)">
              <h1 :id="`name`+algo.name">
                <label class="inputlabel" :for="`algorithmsname`+algo.name">
                  {{algo.name}}
                </label>
              </h1>
              <div class="showParam" :id="`showParam`+algo.name">
                <div v-if="algo.name == `Brightness` || algo.name == `ColorFilter` || algo.name == `MeanFilter`">
                  <span class="rangeValue" :id="`rangeValue`+algo.name"></span>
                  <input class="rangeParamValue" :id="`range` + algo.name" type="range" min="-110" max="110" v-on:Change="rangeSlide(algo.name)"> 
                </div>
                <div class="radio-toolbar">
                <div v-if="algo.name == `KeepColor`">
                  <div v-for="colors in listColor" :key="colors">
                    <input type="radio" name="colorParam" :id="`colorParam` + colors" @click="setColor(colors)">
                    <label class="color" :for="`colorParam` + colors">
                      {{colors}}
                    </label>
                  </div>
                </div>
                <div class="divSides" v-if="algo.name == `SideGray`">
                  <div v-for="sides in sideList" :key="sides">
                    <input type="radio" name="sideParameters" :id="`sideParam` + sides" @click="setSide(sides)">
                    <label class="sideParam" :for="`sideParam` + sides">
                      {{sides}}
                    </label>
                  </div>
                </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

     <nav class="uploadimg" id="uploadimg">
      <label class="buttonupload">
        <input type="file" id="file" ref="file" @change="handleFileUpload"/>
        Add Image
      </label>
    </nav>

    <nav class="waitingscreen" id="wait">
      <div class="messagewaitscreen">Image Processing</div>
      <div class="loader"></div>
    </nav>

    <nav class="Imgdisplayer">
      <label class="Displayerinfo" id="Displayerinfo">Please select an Image</label>
      <img id="createImage" :src="srcImage"/>
    </nav>

    <nav class="option">
      <div class ="apply">
        <button class="buttonapply" id="buttonapply" @click="showImageWithAlgo" hidden>Apply</button>
      </div>
      <div class="download">
        <a :href="`/images/` + CurrentId" :download="CurrentName">
          <button class="buttondownload" id="buttondownload" hidden>
            <img src="../assets/download.png"/>
            Download
          </button>
        </a>
      </div>
      <div class="delete">
        <button class="buttondelete" id="buttondelete" @click="deleteImage" hidden>
        <img src="../assets/delete.png"/>
          Delete
        </button>
      </div>
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
  opacity: 1;
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

div.c{
  margin-left: max(calc(5vw - 22px), 18px);
}
.algorithmsname{
  opacity:0;
}

.inputlabel{
  cursor: pointer;
  font-size: max(1vw, 12px);
}

h1{
  background-color: #242631;
  color: rgb(243, 243, 243);
  width: 100%;
  text-align: left;
  font-size: 15px;
  opacity: 0.5;
}

.showParam{
  max-height:0px;
  overflow: hidden;
  transition:max-height 0.5s;
  background-color: #242631;
}

.rangeValue {
  position: relative;
  display: block;
  margin-left: max(4vw,35px);
  font-size: max(calc(2.8vw - 30px),11px);
  color: #999;
  font-weight: 400;
}

.rangeParamValue{
  width: 8vw;
  min-width: 75px;
  height: 10px;
  -webkit-appearance: none;
  background: rgba(17, 17, 17, 0.501);
  outline: none;
  border-radius: 15px;
  overflow: hidden;
}

.rangeParamValue::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #010101;
  cursor: pointer;
  border: 2px solid rgba(52, 52, 52, 0.496);
  box-shadow: -407px 0 0 400px #90b5db65;
}

.radio-toolbar {
  margin: 10px;
}

.radio-toolbar input[type="radio"] {
  opacity: 0;
  position: fixed;
  width: 0;
}

.radio-toolbar label {
  margin-top: 5px;
  letter-spacing: 1px;
  font-size: min(1.9vw,12px);
  display: inline-block;
  background-color: #353948;
  padding: 5px 7px;
  border: 2px solid #353948;
  border-radius: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.564);
}

.radio-toolbar input[type="radio"]:checked + label {
  background-color:#1c1d268f;
  color: rgb(255, 255, 255);
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

.showImgIfClicked{
  text-align: center;
}

.listImg{
  display: inline-block;
}

.Imginlist{
  width: 6vw;
  min-width: 80px;
  height: 6vw;
  min-height: 80px;
  margin: 3px;
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
  text-align: center;
  font-size: 1.7vw;
}

input[type="file"] {
  display: none;
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

.waitingscreen {
 position: fixed;
 display: flex;
 justify-content: center;
 align-items: center;
 top: 0;
 left: 0;
 right: 0;
 bottom: 0;
 background-color: rgba(255, 255, 255, 0.33);
 z-index: -1;
}

.messagewaitscreen{
  margin-top: min(-12vh,-30px);
  font-size: min(2vw,25px);
}

.loader {
	position: absolute;
	display: flex;
	
}

.loader::before,
.loader::after {
	content: '';
	box-sizing: border-box;
}

.loader::before {
	height: 2px;
	width: 30vw;
	background-color: black;
	animation: loader 1.5s cubic-bezier(0, 0, 0.5, 0.7) infinite;
}

@keyframes loader {
	0%, 44%, 88.1%, 100% {
		transform-origin: left;
	}
	
	0%, 100%, 88% {
		transform: scaleX(0);
	}
	
	44.1%, 88% {
		transform-origin: right;
	}
	
	33%, 44% {
		transform: scaleX(1);
	}
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

.Displayerinfo{
  background: rgba(255, 255, 255, 0.025);
  color: rgb(114, 114, 114);
  border-radius: 30px;
  padding: max(1vw,10px);
  font-size: max(1.5vw,15px);
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

.apply{
  position: absolute;
  margin-left: max(calc(6vw - 30px),10px);
}

.buttonapply {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.678);
  background: #1C1D26;
  border: 2px solid #353948;
  padding: 10px 10px;
  font-size: 13px;
  letter-spacing: 1.5px;
  cursor: pointer;
  box-shadow: inset 0 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.buttonapply:hover {
  box-shadow: inset 100px 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.download{
  margin-top: 10px;
  margin-left: calc(75vw - 250px);
}

.buttondownload{
  color: rgba(255, 255, 255, 0.678);
  background: #1C1D26;
  border: 2px solid #353948;
  padding: 10px 10px;
  font-size: 13px;
  letter-spacing: 1.5px;
  cursor: pointer;
  box-shadow: inset 0 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.buttondownload:hover {
  box-shadow: inset 110px 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.buttondownload img{
  width: 12px;
}

.delete{
  margin-top: -39.5px;
  margin-left: calc(80vw - 150px);
}

.buttondelete{
  color: rgba(255, 255, 255, 0.678);
  background: #1C1D26;
  border: 2px solid #353948;
  padding: 10px 10px;
  font-size: 13px;
  letter-spacing: 1.5px;
  cursor: pointer;
  box-shadow: inset 0 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.buttondelete:hover {
  box-shadow: inset 100px 0 0 0 #353948;
  -webkit-transition: ease-out 0.3s;
  -moz-transition: ease-out 0.3s;
  transition: ease-out 0.3s;
}

.buttondelete img{
  width: 10.5px;
}

</style>
