
# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import module

import tensorflow as tf
from sklearn import svm
from tensorflow.keras.optimizers import Adam
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from tensorflow.keras.models import load_model

# processing

# Model load
nn_model = load_model('save/nn_model.h5')