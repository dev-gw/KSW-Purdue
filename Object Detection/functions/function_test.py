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
df = pd.DataFrame(index = ['MFCC','mel','chroma_stft','contrast','tonnetz']
                  ,columns = ['Shape'])
#df.index.name = 'Features'

## sample data
y, sr = librosa.load('Dataset/trim_test/1.wav')

## MFCC
mfcc = librosa.feature.mfcc(y, sr=sr, n_mfcc=40)
mfcc_mean = np.mean(mfcc.T,axis=0)
print('mfcc: ', mfcc.shape, 'mfcc_mean:', mfcc_mean.shape)
# print(mfcc)
df.iloc[0] = mfcc.shape[0]

## mel
mel = librosa.feature.melspectrogram(y,sr=sr)
mel_mean = np.mean(mel.T, axis=0)
print('mel: ', mel.shape, 'mel_mean:', mel_mean.shape)
df.iloc[1] = mel.shape[0]

## chroma_stft
chroma_stft = librosa.feature.chroma_stft(y,sr)
chroma_stft_mean = np.mean(chroma_stft.T, axis=0)
print('stft: ', chroma_stft.shape, 'stft_mean:', chroma_stft_mean.shape)
df.iloc[2] = chroma_stft.shape[0]

## contrast
stft = np.abs(librosa.stft(y))
contrast = librosa.feature.spectral_contrast(S=stft,sr=sr)
contrast_mean = np.mean(contrast.T, axis=0)
print('contrast: ', contrast.shape, 'contrast_mean:', contrast_mean.shape)
df.iloc[3] = contrast.shape[0]

## tonnetz
tonnetz = librosa.feature.tonnetz(y=librosa.effects.harmonic(y),sr=sr)
tonnetz_mean = np.mean(tonnetz.T, axis=0)
print('tonntez: ', tonnetz.shape, 'tonnetz_mean:', tonnetz_mean.shape)
df.iloc[4] = tonnetz.shape[0]


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
 