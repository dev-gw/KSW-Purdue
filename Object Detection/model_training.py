# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

version 2.2

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import module
import joblib

import tensorflow as tf
from sklearn import svm
from tensorflow.keras.optimizers import Adam
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
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
#autel_df = module.convert_data(100, 'Autel_Evo2', '0')
#dji_df = module.convert_data(100, 'DJI_Phantom4', '1')
#df = pd.concat([autel_df,dji_df])
#df.to_pickle("UAV.pkl") # save data to pickle
df = pd.read_pickle("save/UAV.pkl")
accuracy = [] # save accuracy

# processing
X = np.array(df.feature.tolist())
y = np.array(df.class_label.tolist())
x_train, x_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=2023)
## for Neural network
y_oh = to_categorical(y)
x_train, x_test, y_train_oh, y_test_oh = train_test_split(X, y_oh, test_size=0.2, random_state=2023)

# NN
BATCH_SIZE = 32
EPOCHS = 15
## training
nn_model = module.neural_base(40) # Match with column
nn_model.compile(optimizer=Adam(), loss='binary_crossentropy', metrics=['accuracy'])
nn_history = nn_model.fit(x_train, y_train_oh, batch_size=BATCH_SIZE, epochs=EPOCHS, validation_split = 0.1)
nn_model.save('save/nn_model.h5') # Save model and weights(.pb)
## Model evaluate
nn_accuracy = nn_model.evaluate(x_test, y_test_oh)[1]
accuracy.append(nn_accuracy)
print("nn : ", classification_report(nn_model.predict(x_test), y_test))

# SVM
## training
svm_model = module.svm_base(C=10, kernel='linear')
svm_model.fit(x_train, y_train)
joblib.dump(svm_model, 'save/svm_model.pkl') # save model
## Model evaluate
svm_accuracy = svm_model.score(x_test, y_test)
accuracy.append(svm_accuracy)
print("svm : ", classification_report(svm_model.predict(x_test), y_test))

# KNN
## training
knn_model = module.knn_base(n_neighbors=6)
knn_model.fit(x_train, y_train)
joblib.dump(knn_model, 'save/knn_model.pkl') # save model
## Model evaluate
knn_accuracy = knn_model.score(x_test, y_test)
accuracy.append(knn_accuracy)
print("knn : ", classification_report(knn_model.predict(x_test), y_test))

# GNB
## training
gnb_model = module.gnb_base()
gnb_model.fit(x_train, y_train)
joblib.dump(gnb_model, 'save/gnb_model.pkl') # save model
## Model evaluate
gnb_accuracy = gnb_model.score(x_test, y_test)
accuracy.append(gnb_accuracy)
print("gnb : ", classification_report(svm_model.predict(x_test), y_test))

# print training result
score_table = pd.DataFrame(accuracy, index=['NN','SVM','KNN','GNB'], columns=['mfcc']) 
print(score_table)

'''
# CNN
x_train_cnn = tf.reshape(x_train,[160,40,1])
x_test_cnn = tf.reshape(x_test, [40,40,1])
cnn_model = module.cnn_base(40,1)
cnn_model.compile(optimizer=Adam(), loss='binary_crossentropy', metrics=['accuracy'])
cnn_history = cnn_model.fit(x_train_cnn, y_train_oh, batch_size=BATCH_SIZE, epochs=EPOCHS, validation_split = 0.1)
# Model evaluate
cnn_accuracy = cnn_model.evaluate(x_test_cnn, y_test_oh)[1]'''





