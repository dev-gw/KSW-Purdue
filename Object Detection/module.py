# -*- coding: utf-8 -*-
"""
Version 1.2
@author: Gwangwon Kim
"""
import os
import numpy as np
import pandas as pd
from sklearn import svm, naive_bayes, neighbors
import matplotlib.pyplot as plt
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.layers import Input, Dense, Dropout, Conv1D, Conv2D, Flatten, Activation, MaxPooling2D , GlobalAveragePooling2D
import joblib
import librosa
from tensorflow.keras.optimizers import Adam , RMSprop 
from tensorflow.keras.layers import BatchNormalization
from tensorflow.keras.callbacks import ReduceLROnPlateau , EarlyStopping , ModelCheckpoint , LearningRateScheduler

# Change name of audio file
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
        ## Except CNN, np.mean method is needed.
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

# Convert data to dataframe
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
def svm_base(C, kernel):
    svm_model = svm.SVC(C=C, kernel=kernel)
    return svm_model
## libSVM for implementation

## GNB (Gaussian Naive Bayes)
def gnb_base():
    gnb_model = naive_bayes.GaussianNB()
    return gnb_model

## KNN (K-Nearest-Neighbor)
def knn_base(n_neighbors=6):
    knn_model = neighbors.KNeighborsClassifier(n_neighbors=n_neighbors)
    return knn_model

## NN(Nueral Network)
def neural_base(column):
    input_tensor = Input(shape=(column))
    x = Dense(128)(input_tensor)
    x = Activation('relu')(x)
    x = Dropout(rate=0.1)(x)
    x = Dense(128)(x)
    x = Activation('relu')(x)
    x = Dropout(rate=0.1)(x)

    output = Dense(2, activation='sigmoid')(x)
    
    model = Model(inputs=input_tensor, outputs=output)
    model.summary()
    return model

## CNN(Convolutional Nueral Network) - for only test
def cnn_base(column, channel):
    input_tensor = Input(shape=(column, channel)) # 배치제외 3차원
    
    x = Conv1D(16,16, activation='relu')(input_tensor)
    x = Conv1D(16,16, activation='relu')(x)
    
    x = Flatten()(x)
    
    x = Dense(32, activation = 'relu')(x)
    x = Dropout(rate=0.2)(x)
    
    output = Dense(2, activation='softmax', name='output')(x)
    
    model = Model(inputs=input_tensor, outputs=output)
    model.summary()
    return model

def show_history(history):
    plt.figure(figsize=(6,6))
    plt.yticks(np.arange(0,1,0.05))
    plt.plot(history.history['accuracy'], label='train')
    plt.plot(history.history['val_accuracy'], label='valid')
    plt.legend()
    

