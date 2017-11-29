from skimage import transform, data
from skimage.color import rgb2gray
from numpy import array
import skimage
import tensorflow as tf
import os
import numpy as np


def load_data(data_directory):
	directories = [d for d in os.listdir(data_directory)
					if os.path.isdir(os.path.join(data_directory, d))]
	labels = []
	images = []
	for d in directories:
		label_directory = os.path.join(data_directory, d)
		file_names = [os.path.join(label_directory, f)
						for f in os.listdir(label_directory)
						if f.endswith(".png")]
		for f in file_names:
			images.append(data.imread(f))
			labels.append(int(d))
	return images, labels

ROOT_PATH = "/Users/alexis/Desktop/tf/"
train_data_directory = os.path.join(ROOT_PATH, "own")

images, labels = load_data(train_data_directory)

##images28 = [transform.resize(image, (28,28)) for image in images]
images28 = np.array(images)
images28 = rgb2gray(images28)
images28 = np.reshape(images28,(-1,784))

original_indices = tf.constant(labels)
depth = tf.constant(10)
labels = tf.one_hot(indices=original_indices, depth=depth)

x = tf.placeholder(tf.float32, [None, 784])
W = tf.Variable(tf.zeros([784, 10]))
b = tf.Variable(tf.zeros([10]))
y = tf.matmul(x, W) + b

y_ = tf.placeholder(tf.float32, [None, 10])

cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y_, logits=y))
train_step = tf.train.GradientDescentOptimizer(0.5).minimize(cross_entropy)

saver = tf.train.Saver()

sess = tf.InteractiveSession()
tf.global_variables_initializer().run()

path_to_dataset = "Hnd/Img/"
with tf.Session() as session:
	session.run(tf.global_variables_initializer())
	for dir in os.listdir(path_to_dataset):
		for file in os.listdir(path_to_dataset + dir):
			for i in range(2000):
				sess.run(train_step, feed_dict={x: images28, y_: labels.eval()})


save_path = saver.save(sess, "./tf_apprentissage/model.ckpt")
