#-*- coding: utf-8 -*-
import tkinter as tk
from tkinter import Entry, Label,END
from PIL import Image,ImageDraw
import time
import base64
from io import BytesIO
from urllib import request, parse

class ImageGenerator:
    def __init__(self,parent,posx,posy,*kwargs):
        self.parent = parent
        self.posx = posx
        self.posy = posy
        self.sizex = 780
        self.sizey = 350
        self.filename =""#init filename
        self.b1 = "up"
        self.xold = None
        self.yold = None
        self.coords= [[],[]]
        self.index_coords=0
        self.drawing_area=tk.Canvas(self.parent,width=self.sizex,height=self.sizey)
        self.drawing_area.pack(fill="x",side="top",pady=10, padx=10)
        self.drawing_area.bind("<Motion>", self.motion)
        self.drawing_area.bind("<ButtonPress-1>", self.b1down)
        self.drawing_area.bind("<ButtonRelease-1>", self.b1up)
        self.button_save=tk.Button(self.parent,text="Enregistrer",width=10,bg='white',command=self.save)
        self.button_save.pack(fill="x",side="right", padx=10)
        self.button_clear=tk.Button(self.parent,text="Effacer",width=10,bg='white',command=self.clear)
        self.button_clear.pack(fill="x", side="right", padx=10)
        self.label_champ_signification = Label(root,text="Entrer la signification du dessin :")
        self.label_champ_signification.pack(side="left",fill="x", padx=10)
        self.champ_signification = Entry(root)
        self.champ_signification.pack(side="left",fill="x", padx=10)
        self.image=Image.new("RGB",(self.sizex,self.sizey),(255,255,255))
        self.draw=ImageDraw.Draw(self.image)

    def getname(self):
        if not self.champ_signification.get():
            return False
        else:
            return True

    def save(self):
        if(self.getname()):
            for i in range(0,self.index_coords,1):
                self.draw.line(self.coords[i],(0,0,0),width=3)
            self.filename = str(int(time.time()))+"_"+self.champ_signification.get()+".png"
            imageFile = BytesIO()
            #with self.image.save(fp=output,format="PNG") as imageFile:
       #     with open("images/" + self.filename, "rb") as imageFile:
            self.image.save(fp=imageFile, format="PNG")
            img = base64.b64encode(imageFile.getvalue())
            content = {"img": img, "label": self.filename}
            data = parse.urlencode(content).encode()
            req = request.Request('http://tf.boblecodeur.fr:8000/postimg', data=data)  # send data to the server
            # get and read the answer
            resp = request.urlopen(req)
            result = resp.read()
            print (result)

            self.clear()





    def clear(self):
        self.drawing_area.delete("all")
        self.image.close()
        self.image = Image.new("RGB", (self.sizex, self.sizey), (255, 255, 255))
        self.draw = ImageDraw.Draw(self.image)
        self.champ_signification.delete(0, END)
        self.coords=[[],[]]
        self.index_coords = 0


    def b1down(self, event):
        self.b1 = "down"

    def b1up(self, event):
        self.b1 = "up"
        self.xold = None
        self.yold = None
        self.index_coords+=1
        self.coords.append(list())

    def motion(self, event):
        if self.b1 == "down":
            if self.xold is not None and self.yold is not None:
                event.widget.create_line(self.xold, self.yold, event.x, event.y, smooth='true', width=5,fill='black')
                self.coords[self.index_coords].append((self.xold, self.yold))
        self.xold = event.x
        self.yold = event.y


if __name__ == "__main__":
    root = tk.Tk()
    root.title('Integration données')
    root.wm_geometry("%dx%d+%d+%d" % (800, 450, 10, 10))
    root.config(bg='white')
    root.resizable(0, 0)
    ImageGenerator(root, 10, 10)
    root.mainloop()