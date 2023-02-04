# -*- coding: utf-8 -*-
"""
Version 1.2
@author: Gwangwon Kim
"""
import os
import numpy as np
import pandas as pd
from sklearn import svm, naive_bayes, neighbors
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.layers import Input, Dense, Dropout, Flatten, Activation, MaxPooling2D , GlobalAveragePooling2D
import joblib
import librosa

def name_change():
    file_path = 'Dataset/DJI_Phantom4'
    file_names = os.listdir(file_path)
    i = 1
    for name in file_names:
        src = os.path.join(file_path, name)
        dst = str(i)+'.wav'
        dst = os.path.join(file_path, dst)
        os.rename(src,dst)
        i+=1

# Feature Extraction
def extract_feature(signal, sr):
    try:
        # select feature
        mfcc = librosa.feature.mfcc(signal,sr=sr,n_mfcc=40)
        #mel = librosa.feature.melspectrogram(signal,sr=sr).T
        #chroma_stft = librosa.feature.chroma_stft(signal, sr).T
        #contrast = librosa.feature.stft(S=np.abs(librosa.stft(signal)),sr=sr).T
        #tonnetz = librosa.feature.tonnetz(y=librosa.effects.harmonic(signal),sr=sr).T
        
    except Exception as e:
        print("Error when extract feature")
        print(e)
        return None

    return mfcc

def convert_data(length,company,label):
    features = []     
    for index in range(1,length+1):
        file_name = 'Dataset/'+ company + '/' + str(index) + '.wav'
        # mfcc extract
        signal, sr = librosa.load(file_name, sr=22050)
        data = extract_feature(signal, sr)
        features.append([data,label]) # drone class
        featuredf = pd.DataFrame(features, columns=['feature', 'class_label'])
    return featuredf

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
        

