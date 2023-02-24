# -*- coding: utf-8 -*-
"""
Returning classification result

@author: Gwangwon Kim
version 3.2
"""
import os
import glob
import numpy as np
import pickle
import joblib
import sys
import warnings
import scikitlearn

warnings.filterwarnings(action='ignore')

# Main function
# data : 1D array of mfcc values.
# model_path : pretrained model file path ('svm_model.pkl')

def detect_result(data, model_path):
    # Setting(For using GPU)
    
    # Load model
    # Load model
    svm_model = joblib.load(model_path)

    # Processing
    #X = np.array(data.tolist()).reshape(1,-1)

    # prediction
    result = svm_model.predict(data)[0]
    
    return result


    