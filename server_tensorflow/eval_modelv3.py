# Modify 'test1.jpg' and 'test2.jpg' to the images you want to predict on

from keras.models import load_model
from keras.preprocessing import image
import numpy as np
import sys

# dimensions of our images
img_width, img_height = 28, 28

# load the model we saved
model = load_model('model.h5')
model.compile(loss='categorical_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])

# predicting images
img = image.load_img(sys.argv[1], target_size=(img_width, img_height))
x = image.img_to_array(img)
x = np.reshape(x, (-1, 1,28, 28))

images = np.vstack([x])
classes = model.predict_classes(images, batch_size=1,verbose=0)
prob = model.predict_proba(images,batch_size=1,verbose=0)
print (classes)
print(prob)