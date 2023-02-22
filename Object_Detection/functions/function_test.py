# -*- coding: utf-8 -*-
"""
Test File

@author: Gwangwon Kim


"""
import os
import librosa
import numpy as np
import pandas as pd
import tensorflow as tf
import joblib
import sklearn2pmml
from sklearn.datasets import load_iris
from sklearn.datasets import load_diabetes
from sklearn.tree import DecisionTreeRegressor
from sklearn2pmml import PMMLPipeline, sklearn2pmml
import pandas as pd
from sklearn.svm import SVC


# feature extraction test
## Verify shape of features

#df.index.name = 'Features'
model = keras.models.load_model('../save/tf_svm_model.h5', compile=True)
converter = tf.lite.TFLiteConverter.from_keras_model(model)
flat_data = converter.convert()

with open('../save/tf_svm_model_2.tflite', 'wb') as f:
    f.write(flat_data)


# Data convert test
df = pd.read_pickle("save/mfcc_3.pkl")
X = pd.DataFrame(columns=[name for name in range(1, 41)], data = df.feature.tolist())
y = pd.DataFrame(columns = ['target'], data = df.class_label.tolist())
# here you can use the key classifier, if suitable
pipeline = PMMLPipeline([ ('svm', SVC(C=10, kernel='linear'))])
#training the model
pipeline.fit(X, y)
# exporting the model
sklearn2pmml(pipeline, 'save/svm_3.pmml', with_repr = True)
 