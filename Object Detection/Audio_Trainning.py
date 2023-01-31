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
import librosa


# Data input
audio_path = ''
y, sr = librosa.load(audio_path) # sr = sampling rate

# Feature Extraction
def extract(y, sr):
    mfcc = np.mean(librosa.feature.mfcc(y,sr=sr,n_mfcc=40).T,axis=0)
    mel = np.mean(librosa.feature.melspectrogram(y,sr=sr).T, axis=0)
    chroma_stft = np.mean(librosa.feature.chroma_stft(y, sr).T,axis=0)
    contrast = np.mean(librosa.feature.stft(S=np.abs(librosa.stft(y)),sr=sr).T,axis=0)
    tonnetz = np.mean(librosa.feature.tonnetz(y=librosa.effects.harmonic(y),sr=sr).T,axis=0)
    return mfcc, mel, chroma_stft, contrast, tonnetz

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
def neural_base(shape):
    input_tensor = Input(shape=(shape))
    x = Dense(128)(input_tensor)
    x = Activation('relu')(x)
    x = Dropout(rate=0.1)(x)
    x = Dense(128)(x)
    x = Activation('relu')(x)
    x = Dropout(rate=0.1)(x)

    output = Dense(1, activation='sigmoid')(x)
    
    model = Model(inputs=input_tensor, outputs=output)
    model.summary()
    return model

# Model Trainning (Can edit for test)
def trainning(X,y, model_name):
    if model_name == 'neural_base':
        model = model_name(5)
        model.compile(optimizer=Adam(lr=0.001), loss='binary_crossentropy', metrics=['accuracy'])
        model.fit(X,y, epochs=30)
        model.save(model_name + '.h5')
    else:
        model = model_name(X,y)
        model.fit(X,y)
        joblib.dump(model, './' + model_name + '.pkl') # save model
        
trainning(X,y)


