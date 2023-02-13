# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import librosa
import module
import joblib
import sys

import tensorflow as tf
from sklearn import svm
from tensorflow.keras.optimizers import Adam
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from tensorflow.keras.models import load_model

''' Receive argument 
    1 : data path
    2 : model file path '''

def detect_result():
    argument = sys.argv
    del argument[0]
    
    data_path = sys.argv[1]
    model_path = sys.argv[2]
    
    data_path = 'Dataset/trim_test/3.wav'
    model_path = 'save/svm_model.pkl'
    # Test data for server

    # Model load
    #nn_model = load_model('save/nn_model.h5')
    svm_model = joblib.load(model_path)

    # processing
    signal, sr = librosa.load(data_path) 
    mfcc = np.mean(librosa.feature.mfcc(signal,sr=sr,n_mfcc=40).T, axis=0)

    X = np.array(mfcc.tolist()).reshape(1,-1)

    # prediction
    result = svm_model.predict(X)[0]
    print(result)
    return result





    