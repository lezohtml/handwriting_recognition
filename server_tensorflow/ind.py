import json, os, base64, PIL
from bottle import post, request
from eval_model import *
from resizeimage import resizeimage


class App():
    @post('/postimg')
    def postImg():
        monjson = request.json  # get json object
        img = monjson['img']  # read data from json relative to the image
        label = monjson['label']  # read the label
        imgdecode = base64.b64decode(img)  # decode image base64 format
        imgname = 'images/' + label + ".png"  # name the picture file with the label and the .png file extension
        with open(imgname, "wb") as fh:  # write the picture data in the picture file
            fh.write(imgdecode)
        img = Image.open(imgname)
        img = resizeimage.resize_cover(img, [28, 28])
        img.save('images/crop/' + label, img.format)
        newimage = 'images/crop/' + label + ".png"
        retour = os.system("python3 eval_model.py " + newimage)
        data = {retour}
        data['key'] = 'mot'
        json_data = json.dumps(data)
        return json_data

    @post('/postimg2')
    def postImg2():
        monjson = request.json  # get json object
        img = monjson['img']  # read data from json relative to the image
        label = monjson['label']  # read the label
        imgdecode = base64.b64decode(img)  # decode image base64 format
        imgname = 'images/' + label + ".png"
        data = {"accuracy" : "97.25", "word" : "hello"}
        json_data = json.dumps(data)
        return json_data