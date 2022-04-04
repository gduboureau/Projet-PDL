<script setup lang="ts">
</script>

<template>
  <div id="app">
    <body>
    <nav class="menu">
      <ul>
        <li>
          <router-link class="link" to="/">Home</router-link>
        </li>
        <li >
          <router-link class="link" to="/gallery">Gallery</router-link>
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
  margin-top: -15px;
  margin-bottom: -5px;
}

.menu ul {
  text-align: center; 
}

.menu ul li {
  display: inline-block;
}

.link {
  text-decoration: none;
  color: rgb(0, 0, 0);
  font-weight: 800;
  text-transform: uppercase;
  margin: 0 175px;
  line-height: 37px;

}

.link:after,
.link:before {
  border: 0.7px solid #000;
  opacity:0;
  background:rgb(0, 0, 0);
  content: " ";
  display: block;
  margin: auto;
  transition: all 500ms ease-in-out;
  width: 0;
  
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

body{
  margin:0;
  padding:0;
  font-family: "Roboto", sans-serif;

}

</style>
