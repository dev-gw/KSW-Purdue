# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import audio_module

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
autel_df = audio_module.convert_data(100, 'Autel_Evo2', '0')
dji_df = audio_module.convert_data(100, 'DJI_Phantom4', '1')
df = pd.concat([autel_df,dji_df])
df.to_pickle("UAV.pkl") # save data to pickle

# processing
X = np.array(df.feature.tolist())
y = np.array(df.class_label.tolist())
y_oh = to_categorical(y)
x_train, x_test, y_train, y_test = train_test_split(X, y_oh, test_size=0.2, random_state=2023)

# cnn preprocessing
x_train_cnn = tf.reshape(x_train, [-1, 40, 216, 1]) # for neural shape
x_test_cnn = tf.reshape(x_test, [-1, 40, 216, 1]) 

# CNN model training
cnn_model = audio_module.cnn_base(40,216,1)
cnn_model.compile(optimizer=Adam(), loss='binary_crossentropy', metrics=['accuracy'])
history = cnn_model.fit(x_train_cnn, y_train, batch_size=32, epochs=15, validation_split = 0.1)

# CNN history graph
audio_module.show_history(history)

# Model evaluate
cnn_accuracy = cnn_model.evaluate(x_test_cnn, y_test)[1]









