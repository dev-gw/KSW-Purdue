# -*- coding: utf-8 -*-
"""
Returning classification result

@author: Gwangwon Kim
version 3.0
"""
import os
import glob
import numpy as np
import pickle
import joblib
import sys
import warnings
from tensorflow import keras
warnings.filterwarnings(action='ignore')

# Main function
# data : 1D array of mfcc values.
# model_path : pretrained model file path (tf_svm_model.h5)
def detect_result(data, model_path):
    
    # Load model
    svm_model = keras.models.load_model(model_path, compile=True)
    
    # Processing
    # X = np.mean(data.T, axis=0)
    # X = np.array(data.tolist()).reshape(1,-1)

    # prediction
    result = np.argmax(svm_model.predict(data))
    
    return result


    