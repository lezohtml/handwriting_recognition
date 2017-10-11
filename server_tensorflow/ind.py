import json
from bottle import post, request


class App() :

    @post('/postimg')
    def postImg():
        #monjson = json.load(jsonn)
        img = request.forms.get('img')
        label = request.forms.get('label')
        #img = monjson["img"]
        #label = monjson["label"]
        #envoi des infos à tensorflow
        if img and label :
            retour = "IMG : OK \n LABEL : OK"
        elif img :
            retour = "IMG : OK"
        elif label :
            retour = label + " OK"
        else :
            retour = "ERROR"

         #info retournées par tensorflow
        f = open('log.txt', 'a')
        f.write(str(label) + '\n')  # python will convert \n to os.linesep
        f.close()
        return retour



