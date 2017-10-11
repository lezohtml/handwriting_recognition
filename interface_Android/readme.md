# Un moteur de reconnaissance d’écriture manuelle

Chefs de projet : Alexis LEBLOND / Richard VACHERON 

Equipe : Léonard PRADIER / Brice HARISMENDY / Norman O'Shea / Valentin Faria Oliveira

## Sujet :

De nos jours, les écrans tactiles sont la base des tablettes et smartphones, mais sont aussi de plus en plus présents dans nos ordinateurs. Un nouveau segment a également fait son apparition : les tablettes/PC (comme la Surface de Microsoft).

Avec ces écran un nouvel usage consiste à écrire directement sur l’écran avec son doigt, un stylo passif ou un stylo actif. Pour que les mots écrits sous forme de dessin à l’écran soient convertis en texte utilisable dans un traitement de texte, une phase de reconnaissance est nécessaire. Il existe plusieurs applications et moteurs capables de réaliser cette tâche. Sur nos smartphones certains claviers virtuels reconnaissent les mots écrits, mais il existe également des applications de prise de notes qui permettent de convertir les mots écrits en texte comme Nebo/MyScript (http://www.myscript.com/nebo/).

Le but de ce projet est de fournir un moteur de reconnaissance d’écriture manuelle portable et utilisable sur le maximum de plateformes possibles. Ce moteur pourra ensuite être utilisé comme base pour la réalisation d’applications tierces comme des logiciels de prise de notes manuscrites. Le projet est vaste, c’est pourquoi dans un premier temps, il faut se focaliser sur le moteur de reconnaissance.

Il existe déjà des projets en lien avec le sujet comme le moteur HWRE (https://github.com/phatware/WritePad-Handwriting-Recognition-Engine), mais il est aussi possible de partir sur un nouveau projet, un moteur ouvert adapté au sujet. Pour reconnaître le texte manuscrit, il faut d’abord identifier les parties constituant le mot, les discrétiser. Plusieurs techniques et méthodes pourront être utilisées : reconnaissance de textes écrits et reçus sous forme d’images bitmap (couleur de l’encre) et/ou utilisation des gestes (point de départ du stylo, direction du mouvement, pression, ...) qui ont servi à écrire le mot. Il faut ensuite identifier le mot écrit, pour cela il est possible d’utiliser des techniques d’apprentissage. L’apprentissage des lettres, mots et phrases pourra se faire par utilisation d’un moteur de machine learning comme TensorFlow (https://www.tensorflow.org/install/).


### Suivi séance 1 :

  - Présentation du projet
  - Choix de la technologie --> Python + Java 
  - Une équipe (1) pour la plateforme PC avec du Python
  - Une équipe (2) pour la plateforme Android en Java
  - Système neuronal à implementer et à initialiser grâce aux deux plateformes;
  - Choix du "design" pour les différentes applications

### Suivi séance 2 :
  - Création des deux interfaces (PC-Android)
	-- Zone de dessin, sauvegarde en image, clean de la zone de dessin 
  - Mise en place d'une communication client serveur
	-- Envoie des fichier sur le serveur en SFTP
  - Installation du serveur TensorFlow 
  - Problèmes logiciels rencontrés 

### Pour la séance 3
  - Mise en place de la récupération des images sur le serveur pour les interpreter avec TensorFlow
  - Terminer les interfaces mobile et PC
  - Appréhender TensorFlow


