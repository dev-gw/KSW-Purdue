# -*- coding: utf-8 -*-
"""
Returning classification result

@author: Gwangwon Kim
version 2.1
"""
import os
import glob
import numpy as np
import pickle
import joblib
import sys
import warnings
warnings.filterwarnings(action='ignore')

# Main function
# data : 2D array of mfcc values.
# model_path : pretrained model file path (.pkl)
def detect_result(data, model_path):
    
    # Load model
    svm_model = joblib.load(model_path)
    
    # Processing
    mfcc = np.mean(data.T, axis=0)
    X = np.array(mfcc.tolist()).reshape(1,-1)

    # prediction
    result = svm_model.predict(X)[0]
    
    return result




    