# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import module

import tensorflow as tf
from sklearn import svm
from tensorflow.keras.optimizers import Adam
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical

from tensorflow.keras.models import Sequential, Model
from tensorflow.keras.layers import Input, Dense , Conv2D , Dropout , Flatten , Activation, MaxPooling2D , GlobalAveragePooling2D
from tensorflow.keras.optimizers import Adam , RMSprop 
from tensorflow.keras.layers import BatchNormalization
from tensorflow.keras.callbacks import ReduceLROnPlateau , EarlyStopping , ModelCheckpoint , LearningRateScheduler

# Setting(For using GPU)
os.chdir('C:\광원\k-sw\활동\KSW-Purdue\Object Detection')
physical_devices = tf.config.list_physical_devices('GPU')
try:
    tf.config.experimental.set_memory_growth(physical_devices[0],True)
except:
    pass

# Data input
autel_df = module.convert_data(100, 'Autel_Evo2', '0')
dji_df = module.convert_data(100, 'DJI_Phantom4', '1')
df = pd.concat([autel_df,dji_df])
df.to_pickle("UAV.pkl") # save data to pickle

# processing
X = np.array(df.feature.tolist())
y = np.array(df.class_label.tolist())
y = to_categorical(y)
x_train, x_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=2023)

# CNN
## preprocessing
x_train_cnn = tf.reshape(x_train, [-1, 216, 40, 1]) # for neural shape
x_test_cnn = tf.reshape(x_test, [-1, 216, 40, 1]) 
# training
cnn_model = module.cnn_base(216, 40 ,1)
cnn_model.compile(optimizer=Adam(), loss='binary_crossentropy', metrics=['accuracy'])
cnn_history = cnn_model.fit(x_train_cnn, y_train, batch_size=32, epochs=15, validation_split = 0.1)
# Model evaluate
cnn_accuracy = cnn_model.evaluate(x_test_cnn, y_test)[1]

# SVM
## training
svm_model = module.svm_base(C=10, kernel='linear')
svm_model.fit(x_train, y_train)
## Model evaluate
svm_accuracy = svm_model.score(x_test, y_test)

# KNN
## training
knn_model = module.knn_base(n_neighbors=6)
knn_model.fit(x_train, y_train)
## Model evaluate
knn_accuracy = knn_model.score(x_test, y_test)

# GNB
## training
gnb_model = module.gnb_base()
gnb_model.fit(x_train, y_train)
## Model evaluate
gnb_accuracy = gnb_model.score(x_test, y_test)

# NN
## training
nn_model = module.neural_base(40)
nn_model.compile(optimizer=Adam(), loss='binary_crossentropy', metrics=['accuracy'])
nn_history = nn_model.fit(x_train, y_train, batch_size=32, epochs=15, validation_split = 0.1)
## Model evaluate
nn_accuracy = nn_model.evaluate(x_test, y_test)

# Score table








