# -*- coding: utf-8 -*-
"""
Returning classification result

@author: Gwangwon Kim
version 2.1
"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import librosa
import joblib
import sys

import tensorflow as tf
from sklearn import svm
import warnings
warnings.filterwarnings(action='ignore')


# Main function
# data : list of mfcc values.
# model_path : pretrained model file path (.pkl)
def detect_result(data, model_path):
    
    # Load model
    svm_model = joblib.load(model_path)
    
    # Extraction
    #mfcc = np.mean(librosa.feature.mfcc(signal,sr=sr,n_mfcc=40).T, axis=0)
    X = np.array(data.tolist()).reshape(1,-1)

    # prediction
    result = svm_model.predict(X)[0]
    
    return result




    