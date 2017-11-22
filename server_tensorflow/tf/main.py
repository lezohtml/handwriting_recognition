# -*- coding: utf-8 -*-
import tensorflow as tf
from PIL import Image
import numpy as np
import os

"""
# Model parameters
W = tf.Variable([.3], dtype=tf.float32)
b = tf.Variable([-.3], dtype=tf.float32)
# Model input and output
x = tf.placeholder(tf.float32)
linear_model = W * x + b
y = tf.placeholder(tf.float32)

# loss
loss = tf.reduce_sum(tf.square(linear_model - y)) # sum of the squares
# optimizer
optimizer = tf.train.GradientDescentOptimizer(0.01)
train = optimizer.minimize(loss)

# training data
x_train = [1, 2, 3, 4]
y_train = [0, -1, -2, -3]
# training loop
init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init) # reset values to wrong
for i in range(1000):
  sess.run(train, {x: x_train, y: y_train})

# evaluate training accuracy
curr_W, curr_b, curr_loss = sess.run([W, b, loss], {x: x_train, y: y_train})
print("W: %s b: %s loss: %s"%(curr_W, curr_b, curr_loss))
"""
def conv(entree, noyaux):
    n = noyaux[0]*noyaux[1]*noyaux[2]
    W = tf.Variable(tf.truncated_normal(noyaux, stddev=(2/n)**.5))  # poid aléatoire (distribution gaussienne)
    b = tf.Variable(tf.zeros(noyaux[-1:]))  # biais
    conv = tf.nn.conv2d(entree, W, [1, 1, 1, 1], 'SAME')+b
    # entree = notre image ,
    # W = poid (noyaux de notre convolution,
    # [1,1,1,1] = pas de notre convolution,
    # 'SAME' va rajouter des zeros autour de notre matrice permet une matrice de même taille
    # donc en sortie pour que le noyaux ne perde pas d'information

    return tf.nn.relu(conv)  # on applique la non linéarité


def pool(entree):
    return tf.nn.max_pool(
        entree,
        [1, 2, 2, 1],
        [1, 2, 2, 1],
        'SAME'
    )


def dense(entree, forme):
    n = forme[0]
    w = tf.Variable(tf.truncated_normal(forme, stddev=(2/n)**.5))  # poid
    b = tf.Variable(tf.zeros(forme[0]))

    matmul = tf.matmul(entree, w)+b

    return tf.nn.relu(matmul)


def model(entree):
    c1 = conv(entree, (3, 3, 3, 16))
    c2 = conv(c1, (3, 3, 16, 16))
    c3 = pool(c2)

    c4 = conv(c3, (3, 3, 16, 32))

    # explication du deuxième paramètre
    # 3 -> Longueur
    # 3 -> Largeur
    # 16->profondeur de la matrice 3D précédente
    # 32->profondeur de la prochaine matrice 3D

    c5 = conv(c4, (3, 3, 32, 32))
    c6 = pool(c5)

    c7 = tf.reshape(c6, (-1, 16**2 * 32))  # on ecrase notre matrice en 3d dans un vecteur
    # c6 = la matrice 3D
    # arg2 = la forme :
    #         -1 : qu'une solution
    #          16**2 * 32 = nombre d'element dans le vecteur
    c8 = dense(c7, (16**2 * 32, 100))  # multiplication matricielle
    c9 = dense(c8, (100, 2))
    return c9

def resize_img(img):
    image_temp = Image.open(img)
    image_temp = image_temp.resize((64, 64), Image.ANTIALIAS)
    image_temp = np.asarray((image_temp).convert('L'))
    image_temp = np.reshape(image_temp, 64, 64)
    return image_temp


if __name__ == "__main__":
    x = tf.placeholder(tf.float32, [None, 64, 64, 3])  # contiendra nos images par la suite
    # tf.float32 -> type de la donnée qui serra stocké
    # argument 2 vas être la forme de x
    # None -> signifie qu'on traite plusieurs image en même temps
    # 64, 64, 3 -> résolution de l'image : 3 = profondeur

    path_to_dataset = "Hnd/Img/"
    f_de_x = model(x)  # permet de déterminer ce qu'est l'image (y = f(x) )

    y = tf.placeholder(tf.float32, [None, 2])  # les labels (ce que signifie en réalité l'image
    # fonction de cout (calcul de la distance entre ce que l'on veut et ce qu'on a
    cout = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=f_de_x, labels=y))
    step = tf.train.AdamOptimizer(0.0001).minimize(cout)  # on minimise notre cout (0.0001 est arbitraire)

    with tf.Session() as session:
       session.run(tf.global_variables_initializer())
       for dir in os.listdir(path_to_dataset):
           for file in os.listdir(path_to_dataset + dir):
               image = resize_img(path_to_dataset+dir+"/"+file)
               session.run(step, feed_dict={x: image, y: dir})