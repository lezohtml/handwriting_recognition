import json
from bottle import post, request
import base64, PIL


class App():
    @post('/postimg')
    def postImg():
        monjson = request.json  #get json object
        img = monjson['img']  #read data from json relative to the image
        label = monjson['label']  #read the label
        imgdecode = base64.b64decode(img) #decode image base64 format
        imgname = 'images/' + label + ".png"  #name the picture file with the label and the .png file extension
        with open(imgname, "wb") as fh:  #write the picture data in the picture file
            fh.write(imgdecode)

        retour = {"return": "Ok"} #return json message

        return retour
