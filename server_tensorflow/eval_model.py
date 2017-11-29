import sys
import tensorflow as tf
from PIL import Image,ImageFilter
from skimage import transform, data
from skimage.color import rgb2gray
import skimage
import os
import numpy as np

def predictLetter(imvalue):
    x = tf.placeholder(tf.float32, [None, 784])
    W = tf.Variable(tf.zeros([784, 10]))
    b = tf.Variable(tf.zeros([10]))
    y = tf.matmul(x, W) + b

    y_ = tf.placeholder(tf.float32, [None, 10])


    saver = tf.train.Saver()

    sess = tf.InteractiveSession()
    tf.global_variables_initializer().run()

    with tf.Session() as sess:
        saver.restore(sess, "/home/etudiant/PycharmProjects/tf_apprentissage/model.ckpt")
   
        prediction=tf.argmax(y,1)
        return prediction.eval(feed_dict={x: imvalue}, session=sess)


def imageprepare(argv):
    image = data.imread(argv)
    ##images28 = transform.resize(image, (28,28))
    images28 = np.array(image)
    images28 = rgb2gray(images28)
    images28 = np.reshape(images28,(-1,784))
    
    return images28
    

def main(argv):
    imvalue = imageprepare(argv)
    predictedLetter = predictLetter(imvalue)
    print("==PREDICTION== : ")
    print(predictedLetter[0])
    
if __name__ == "__main__":
    main(sys.argv[1])