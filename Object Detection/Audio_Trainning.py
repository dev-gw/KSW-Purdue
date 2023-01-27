# -*- coding: utf-8 -*-
"""
Version 1.2
@author: Gwangwon Kim
"""
import numpy as np
from sklearn import svm, naive_bayes, neighbors
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense, Dropout, Flatten, Activation, MaxPooling2D , GlobalAveragePooling2D
import joblib

# Data input

# Data preprocessing

# Feature Extraction
## MFCC

## mel

## contrast

## chroma

## tonnetz

# Modeling
## SVM(Support Vector Machine)
def svm_base(X,y,C,kernel='linear'):
    svm_model = svm.SVC(C, kernel=kernel)
    return svm_model

## GNB (Gaussian Naive Bayes)
def gnb_base(X,y):
    gnb_model = naive_bayes.GaussianNB()
    return gnb_model

## KNN (K-Nearest-Neighbor)
def knn_base(X,y, n_neighbors=6):
    knn_model = neighbors.KNeighborsClassifier(n_neighbors=n_neighbors)
    return knn_model

## NN(Nueral Network)
def neural_base(INPUT_SIZE):
    input_tensor = Input(shape=(INPUT_SIZE))
    x = Dense(128, activation = 'relu')(input_tensor)
    x = Dropout(rate=0.1)(x)
    x = Dense(128, activation='relu')(x)
    x = Dropout(rate=0.1)(x)

    output = Dense(2, activation='sigmoid')(x)
    
    model = Model(inputs=input_tensor, outputs=output)
    model.summary()
    return model

# Model Trainning (Can edit for test)
def trainning(X,y, model_name):
    if model_name == 'neural_base':
        model = model_name(7)
        model.compile(optimizer=Adam(lr=0.001), loss='binary_crossentropy', metrics=['accuracy'])
        model.fit(X,y, epochs=30)
        model.save(model_name + '.h5')
    else:
        model = model_name(X,y)
        model.fit(X,y)
        joblib.dump(model, './' + model_name + '.pkl') # save model



