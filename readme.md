# Handwriting recognition

Chefs de projet : Alexis LEBLOND / Richard VACHERON

Equipe : Léonard PRADIER / Brice HARISMENDY / Norman O'Shea / Valentin Faria Oliveira

## Utilisation :

  - Application PC :

  Commande : *python3 interface_PC/app_v2.py*

  - API REST :

  Commande : *pyhton3 server_tensorflow/main.py*

  Pour information, l'adresse utilisée par les différents applications est tf.boblecodeur.fr:8000. L'API n'est donc utilisable qu'avec des tests type :
  *curl -H "Content-Type: application/json" -X POST -d '{"img":"hdifuhqdohfihdoishqfiosdhfio","label":"un"}' http://tf.boblecodeur.fr:8000/postimg2*

  - Application Android :

  - Parser image :
