# Rapport de projet *Handwriting Recognition*

M1 :
--
  - Norman O'SHEA
  - Brice HARISMENDY
  - Valentin FARIA OLIVEIRA
  - Léonard PRADIER

M2 :
--
  - Alexis LEBLOND
  - Richard VACHERON

## Sommaire :
 - Contexte
 - Fonctionnement
 - Protocole
 - Apprentissage
 - Applications
 - Organisation
 - Conclusion

<!-- CONTEXTE -->
## Contexte :

### Sujet :
De nos jours, les écrans tactiles sont la base des tablettes et smartphones, mais sont aussi de plus en plus présents dans nos ordinateurs. Un nouveau segment a également fait son apparition : les tablettes/PC (comme la Surface de Microsoft).

Avec ces écran un nouvel usage consiste à écrire directement sur l’écran avec son doigt, un stylo passif ou un stylo actif. Pour que les mots écrits sous forme de dessin à l’écran soient convertis en texte utilisable dans un traitement de texte, une phase de reconnaissance est nécessaire. Il existe plusieurs applications et moteurs capables de réaliser cette tâche. Sur nos smartphones certains claviers virtuels reconnaissent les mots écrits, mais il existe également des applications de prise de notes qui permettent de convertir les mots écrits en texte comme Nebo/[MyScript](http://www.myscript.com/nebo/).

Le but de ce projet est de fournir un moteur de reconnaissance d’écriture manuelle portable et utilisable sur le maximum de plateformes possibles. Ce moteur pourra ensuite être utilisé comme base pour la réalisation d’applications tierces comme des logiciels de prise de notes manuscrites. Le projet est vaste, c’est pourquoi dans un premier temps, il faut se focaliser sur le moteur de reconnaissance.

Il existe déjà des projets en lien avec le sujet comme le moteur [HWRE](https://github.com/phatware/WritePad-Handwriting-Recognition-Engine), mais il est aussi possible de partir sur un nouveau projet, un moteur ouvert adapté au sujet. Pour reconnaître le texte manuscrit, il faut d’abord identifier les parties constituant le mot, les discrétiser. Plusieurs techniques et méthodes pourront être utilisées : reconnaissance de textes écrits et reçus sous forme d’images bitmap (couleur de l’encre) et/ou utilisation des gestes (point de départ du stylo, direction du mouvement, pression, ...) qui ont servi à écrire le mot. Il faut ensuite identifier le mot écrit, pour cela il est possible d’utiliser des techniques d’apprentissage. L’apprentissage des lettres, mots et phrases pourra se faire par utilisation d’un moteur de machine learning comme [TensorFlow](https://www.tensorflow.org/install/).

### But :
L'essentiel du projet est basé sur le deeplearning. Nous devons apprendre à TensorFlow à reconnaître des mots écrits à la main pour ensuite les communiquer au client. L'apprentissage étant une phase difficile nous approchons la reconnaissance de lettres et de chiffres. Nous coupons l'image en petites parties dans lesquelles nous allons chercher à reconnaître des lettres pour les mettre bouts à bout et retrouver le mot originel.

<!-- Fonctionnement -->
## Fonctionnement
Le projet est découpé en 3 grandes parties :

  - **L'application client:**  
Elle fait l'interface entre l'utilisateur et le serveur. Il permet à l'utilisateur d'écrire son mot avant de l'envoyer à l'intelligence artificielle pour recevoir la réponse.

  - **L'API REST:**  
Cette API permet de recevoir les données venant des applications client pour les traiter. Elle reçoit l'image émise par l'application avant de la couper afin qu'elle ait une forme carrée. Elle est ensuite envoyée à TensorFlow.

  - **TensorFlow :**  
Il permet de reconnaître la lettre, le mot, le chiffre envoyée par l'API sous forme d'image.  

**Diagramme de cas d'utilisation application client :**  
![Diagramme de cas d'utilisation app client](rapport/cu.png "Diagramme de cas d'utilisation")

**Diagramme de cas d'utilisation API REST :**  

![Diagramme de cas d'utilisation API REST](rapport/cu_api.png "Diagramme de cas d'utilisation")

### Fonctionnement de base :
L'utilisateur écrit sur une tablette, un smartphone ou un ordinateur des mots. L'image est ensuite envoyée sur le serveur TensorFlow pour être traitée. L'image peut aussi être traitée en locale si l'intelligence artificielle est chargée directement sur l'appareil. Tensorflow traite l'image en l'envoyant dans un réseau de neurones qui permet selon .. de trouver le mot, la lettre ou le chiffre qui correspond. Le serveur renvoi ensuite le résultat pour l'afficher sur l'appareil de l'utilisateur.

![Diagramme de séquence](rapport/SD.png "Diagramme de séquence")

### Réseau de neurones :
ETRE BREF ! (on peut copier WIKI mais citer les sources)
Un réseau de neurones est inspiré de la biologie humaine et des neurones du cerveau. Il est utilisé dans de nombreux domaine notamment en statistique ou encore en recherche. Il est le coeur des méthodes d'apprentissage profont (deeplearning en anglais). Ces méthode ont pour but de permettre la reconnaissance de signaux visuel ou sonore à un système informatisé, en l'occurence pour notre projet des images de mots.

Pour en savoir plus sur les réseaux de neurones et l'apprentissage profond:
 - [Article de Wikipedia sur l'apprentissage profond](https://fr.wikipedia.org/wiki/Apprentissage_profond)
 - [Article de Wikipedia sur les réseau de neurones](https://fr.wikipedia.org/wiki/R%C3%A9seau_de_neurones_artificiels)

### TensorFlow :
TensorFlow est un système d'apprentissage automatique développé en Python par Google sous licence Apache. Il supporte le calcul sur CPU et GPU. Il est disponible pour différentes plateformes et dispose d'API dans plusieurs langages de programmation (Python, C++, Java, Go, ...). Son principe d'apprentissage est de recevoir un ensemble d'images auquelles son rattaché des labels. Pour chaque label, un neurone est crée et se voit attribuer toutes les images correspondant à ce label.

Le premier apprentissage n'est jamais suffisant pout que le réseau de neurones reconnaîssent correctement une image. En effet, lorsqu'il sera demandé à TensorFlow de reconnaître une image, ses premières réponse seront souvent erronées. C'est pourquoi plusieurs apprentissages sont souvent nécessaire. Au-fur et à mesure de ceux-ci, TensorFlow recconnaître avec plus de facile de nouvelles images.

Toutefois il est important de faire attention au sur-apprentissage ! Si trop d'apprentissage sont fait, un réseau de neurones va se spécialiser sur les exemples données avec une trop grande précision et sera incapable de reconnaître des images n'ayant pourtant que quelques petites différence.

<!-- PROTOCOLE -->
## Protocole :
### API REST :
L'API REST est écrite en Python. Elle utilise la bibliothèque [bottle](https://bottlepy.org/) pour les fonctionnalités réseau.
Elle reste toujours en écoute, les informations peuvent arriver à tout moment.
La réception d'une image se fait sur le port 8000 de notre serveur et à l'adresse */postimg*. Les informations arrivent sous forme de JSON afin de récupérer facilement les données. Une fois l'image récupérée, elle est coupée en carré puis envoyée à TensorFlow. Ce dernier lui envoie alors une réponse : un mot. Ce mot est passé dans le dictionnaire afin de savoir s'il a du sens et si il est correct (orthographiquement) avant de le renvoyer au client.
--> HTTP

<!-- APPRENTISSAGE -->
## Apprentissage :
La phase d'apprentissage est une des plus grosse partie de notre projet. Effectivement, la reconnaissance ne peut se faire sans une base de connaissance importante. Nous avons dû trouver un base imposante de données écrites à la main afin de permettre à TensorFlow d'avoir de quoi reconnaître différents types d'écritures.


<!-- APP CLIENTES -->
## Applications clientes :

Les applications clientes permettent le lien entre l'utilisateur et le serveur de reconnaissance. Elle affiche une zone de dessin qui permet à l'utilisateur d'écrire le mot. Elle envoie ensuite ce dessin sous forme d'image et attend la réponse du serveur.

### Android :
L'applcation possède 2 mode différents; un mode "Expert" et un mode "Démonstration". Pour toute les explications à suivre le mode Démonstration sera celui décrit sauf indication contraire.
#### Interface
L'application est composée d'une seule activité divisé en 2 zones principales:
 - Une zone de dessin, en haut, prenant 70% de l'écran.
 - Une zone d'interaction prenant le reste l'espace, en bas. Elle est composée de boutton et labels pour permettre à l'utilisateur de faire des choix, ainsi que de l'informer des changement d'état suite à ses actions et les retour du serveur.
#### Fonctionnement
Dès le demarrage de l'application, la vue principale s'affiche comportant une zone de dessin, un texte indiquant ce que l'utilisateur doit faire ainsi que 2 boutons lui permettant d'effacer son dessin ou alors de l'envoyer au serveur.
 1. L'utilisateur à le choix, après avoir écrit son mot, d'effacer ce qu'il vient de faire ou alors de l'envoyer au serveur.
 2. S'il décide de l'envoyer, l'image est encodé en base64 puis insérer dans un objet JSON ayant pour attribut "img". Cet objet est envoyé au serveur via l'url "http://tf.boblecodeur.fr:8000/postimg". Le serveur répond et la zone de du bas affiche alors le résultat avec la probabilité de celui-ci.
    - En mode Expert, lors de la réponse du serveur, l'interface du bas change pour afficher les 3 meilleurs résultat trouvé et l'utilisateur peut alors choisir le mot qu'il a écrit. Ceci pourrait être éventuellement utile pour un renforcement positif du réseau de neurone.
  3. Après un envoi, la zone de dessin est effacé et laisse la possiblité à l'utilisateur d'écrire un nouveau mot.

#### Évolution
### PC :

<!-- ORGANISATION -->
## Organisation

## Répartition des tâches :
Brice HARISMENDY | Norman O'SHEA | Valentin FARIA OLIVEIRA | Léonard PRADIER
:-: | :-: | :-: | :-:
Interface PC | Application Android | Application Android | Interface PC
Mise en place serveur TensorFlow | Analyse TensorFlow  | Apprentissage modèle | API REST
Interaction avec le serveur TensorFlow | | Modèle / Resolveur | Interaction API REST / interface TensorFlow
Apprentissage modèle| | |

## Outils
 - [Android Studio](https://developer.android.com/studio/index.html) pour le développement de l'application Android
 - [GitHub](http://github.com) pour l'hébergement du projet
 - [Slack](www.slack.com) pour la communication au sein de l'équipe
 - [TensorFlow](https://www.tensorflow.org/) pour le moteur de deeplearning


<!-- CONCLUSION -->
## Conclusion :
Ce projet à été très enrichissant pour nous qui n'avions que de vague connaissances théoriques concernant le l'apprentissage profond. Il nous a permis d'appronfondir ces connaissances et de nous faire une idée sur sa mise en pratique, notamment avec TensorFlow.

Cependant, notre modèle d'apprentissge pour TensorFlow reste imparfait et nous ne somme pas encore en mesure de reconnaitre des mots.

Ceci pourra être le sujet d'une évolution de ce projet.
