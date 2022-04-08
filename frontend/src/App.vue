<script setup lang="ts">
import { ref } from "vue";

const ActualPage = ref("");

if (window.location.hash.substr(1) == '/gallery'){
  ActualPage.value = "Gallery";
}

function Page(link:string){
   ActualPage.value = link;
}

</script>

<template>
  <div id="app">
    <body>
    <nav :class="`menu ${ActualPage}`">
      <ul>
        <li>
          <router-link :class="`link ${ActualPage}`" to="/" @click="Page('')">Home</router-link>
        </li>
        <li >
          <router-link :class="`link ${ActualPage}`" to="/gallery" @click="Page('Gallery')">Gallery</router-link>
        </li>
      </ul>
    </nav>
    </body>
    <div>
      <router-view />
    </div>
  </div>
</template>

<style>

@import url('https://fonts.googleapis.com/css?family=Roboto:500,900,100,300,700,400');

.menu {
  letter-spacing: 1px;
  height: 9.6vh;
  min-height: 40px;
  background: #242631;
  border-bottom: 1px solid #353948;
}

.menu.Gallery{
  background: #ffffff;
  height: 70px;
  border-bottom: none;
}

.menu.Gallery ul li{
  margin-top: 0px;
}

.menu ul {
  margin-block-start: 0;
  margin-block-end: 0;
  text-align: center; 
}

.menu ul li {
  margin-top: max(calc(-40px + 4vh),-24px);
  display: inline-block;
}

.link {
  font-size: max(2.1vh,12px);
  text-decoration: none;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 400;
  text-transform: uppercase;
  margin: 175px;
  line-height: 30px;
}

.link.Gallery{
  color: rgb(0, 0, 0);
  font-size: 16px;
  font-weight: 800;
}

.link:after,
.link:before {
  border: 0.7px solid rgba(255, 255, 255, 0.4);
  opacity:0;
  background:rgba(255, 255, 255, 0.4);
  content: " ";
  display: block;
  margin: auto;
  transition: all 500ms ease-in-out;
  width: 0;
  
}

.link.Gallery:after,
.link.Gallery:before{
  border: 0.7px solid rgb(0, 0, 0);
  background:rgb(0, 0, 0);
}

.link:hover:after,
.link:hover:before {
  opacity:1;
  width: 70%;
}

.error {
  color: red;
  font-weight: bold;
  list-style-type: none;
  margin-top: 1em;
}

body::-webkit-scrollbar {
  display: none;
}

body{
  margin:0;
  padding:0;
  font-family: "Roboto", sans-serif;
}

</style>
