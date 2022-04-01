<script setup lang="ts">
</script>

<template>
  <div id="app">
    <body>
    <nav class="menu">
      <ul>
        <li>
          <router-link to="/">Home</router-link>
        </li>
        <li >
          <router-link to="/gallery">Gallery</router-link>
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
nav {
  width: auto;
  margin: 0 auto;
  background: rgb(255, 255, 255);
  padding: 10px;
}

nav ul {
  list-style: none;
  text-align: center;
}
nav ul li {
  display: inline-block;
}
nav ul li a {
  display: block;
  padding: 10px 45px;
  text-decoration: none;
  color: rgb(0, 0, 0);
  font-weight: 800;
  text-transform: uppercase;
  margin: 0 10px;
}


nav.menu ul li a {
  position: relative;
  overflow: hidden;
  z-index: 1;
  border-radius: 10px 10px;
}
nav.menu ul li a:after {
  display: block;
  position: absolute;
  margin: 0;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  content: '.';
  color: transparent;
  width: 1px;
  height: 1px;
  border-radius: 50%;
  background: transparent;
}

nav.menu ul li a:hover:after {
  animation: circle 2s ease-in;
}

@keyframes circle {
  0% {
    margin: auto;
    z-index: -1;
    background: rgba(226, 226, 226, 0.534);
  }
  100% {
    background: rgba(226, 226, 226, 0.534);
    height: 5000%;
    width: 5000%;
    z-index: -1;
    margin: auto;
  }
}

.error {
  color: red;
  font-weight: bold;
  list-style-type: none;
  margin-top: 1em;
}
body{
  margin:0;
  padding:0;
  font-family: "Roboto", sans-serif;

}


</style>
