# Projet PDL

Ce dépôt correspond au rendu intermédiaire du projet PDL du vendredi 25 mars 2022.

## Auteurs

Duboureau Guillaume
Loustau Valentin
Coubard-Lerendu Yoann

## Prérequis et Technologies

Les prérequis devraient déjà être faits sur votre ordinateur au Crémi (vu en TP et dispo sur moodle si besoin).
Java (JDK 11) + JavaScript
Vue.js : export PATH=/opt/users/Node.js/bin:$PATH

## Dépendances

org.springframework.boot : version 2.6.0
BoofCV : version 0.39.1

## Compatibilité du serveur

Compatible : Windows 10, Windows 11, Linux, Debian Buster
Non compatible/non testé : MacOS, Ubuntu 20.04

## Compatibilité du client

Compatible : Google Chrome, Firefox
Non compatible : Safari

## Execution 

À la racine du projet faire : 
1. mvn clean install
2. mvn --projects backend spring-boot:run
3. ouvrir un navigateur internet à l'adresse http://localhost:8080

