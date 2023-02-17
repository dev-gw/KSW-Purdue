# -*- coding: utf-8 -*-
"""
Returning classification result

@author: Gwangwon Kim
version 1.1
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

# Main function
# data_path : sound data file path (.wav)
# model_path : pretrained model file path (.pkl)
def detect_result(data_path, model_path):
    
    # Load model
    svm_model = joblib.load(model_path)

    # Load data
    signal, sr = librosa.load(data_path)
    
    # Extraction
    mfcc = np.mean(librosa.feature.mfcc(signal,sr=sr,n_mfcc=40).T, axis=0)
    X = np.array(mfcc.tolist()).reshape(1,-1)

    # prediction
    result = svm_model.predict(X)[0]
    
    return result





    