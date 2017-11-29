import os

import tkinter as tk

from PIL import Image,ImageDraw

import time

import base64

from io import BytesIO

import json

from urllib import request, parse



class ImageGenerator:

    def __init__(self,parent,posx,posy,*kwargs):

        self.parent = parent

        self.posx = posx

        self.posy = posy

        #taille par défaut

        self.sizex = 28

        self.sizey = 28

        self.filename =""#init filename

        self.b1 = "up"

        self.xold = None

        self.yold = None

        self.coords= [[],[]]

        self.index_coords=0



        self.drawing_area=tk.Canvas(self.parent)

        self.drawing_area.pack(fill="both",expand=1,side="top",pady=10, padx=10)

        self.drawing_area.bind("<Configure>", self.on_resize)

        self.drawing_area.bind("<Motion>", self.motion)

        self.drawing_area.bind("<ButtonPress-1>", self.b1down)

        self.drawing_area.bind("<ButtonRelease-1>", self.b1up)



        self.button_save=tk.Button(self.parent,text="Enregistrer",width=10,bg='white',command=self.save)

        self.button_save.pack(fill="x",side="right", padx=10, pady=10)



        self.button_clear=tk.Button(self.parent,text="Effacer",width=10,bg='white',command=self.clear)

        self.button_clear.pack(fill="x", side="right", padx=10, pady=10)



        self.label_champ_signification = tk.Label(root,text="Label :")

        self.label_champ_signification.pack(side="left",fill="x", padx=10, pady=10)



        self.champ_signification = tk.Entry(root)

        self.champ_signification.pack(side="bottom",fill="x", padx=10, pady=10)



        self.image = Image.new("RGB", (self.sizex, self.sizey), (255, 255, 255))



    def getname(self):#test if the user set a filename

        if not self.champ_signification.get() :

            self.label_champ_signification.configure(foreground="red")

            return False

        else:

            return True



    def getcoords(self):

        if len(self.coords)<=2 :

            self.show_pop_up("vous devez dessinez une lettre")

            return False

        else:

            return True

    def save(self):

        if(self.getname() and self.getcoords()):



            self.draw = ImageDraw.Draw(self.image)

            for i in range(0,self.index_coords,1):#make lines between each point in each columns

                self.draw.line(self.coords[i],(0,0,0),width=40)



            img28 = self.image.resize((28, 28),Image.ANTIALIAS)



            self.filename = self.champ_signification.get()#define the filename

            file = "test/"+str(int(time.time()))+".png"

            img28.save(file, "PNG")



            os.system("python3 eval_model.py "+file)



            #self.show_pop_up("Prédiction:")

            self.clear()



    def show_pop_up(self,message):

        # show the information for the user

        top = tk.Toplevel()

        top.grab_set()

        message = tk.Label(top, text= message , padx=10, pady=10)

        message.pack()

        buttonClose = tk.Button(top, text='ok', command=top.destroy)

        buttonClose.pack()

        top.grab_release()



    def clear(self):

        self.drawing_area.delete("all")

        self.image.close()

        self.label_champ_signification.configure(foreground="black")

        self.image = Image.new("RGB", (self.sizex, self.sizey), (255, 255, 255))

        self.draw = ImageDraw.Draw(self.image)

        #self.champ_signification.delete(0, tk.END)

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



    def on_resize(self, event):

        # determine the ratio of old width/height to new width/height

        wscale = float(event.width) / self.sizex

        hscale = float(event.height) / self.sizey

        self.sizex = event.width

        self.sizey = event.height

        # resize the canvas

        self.drawing_area.config(width=self.sizex, height=self.sizey-30)

        # rescale all the objects tagged with the "all" tag

        self.drawing_area.scale("all", 0, 0, wscale,hscale)

        self.image=self.image.resize((event.width,event.height),Image.ANTIALIAS)



if __name__ == "__main__":

    root = tk.Tk()

    root.title('Integration données')

    root.wm_geometry("%dx%d+%d+%d" % (500, 500, 10, 10))

    root.config(bg='white')

    #root.resizable(0, 0)

    ImageGenerator(root, 10, 10)

    root.mainloop()