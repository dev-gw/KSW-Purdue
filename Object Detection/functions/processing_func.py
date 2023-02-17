# -*- coding: utf-8 -*-
"""
Data processing functions

@author: Gwangwon

"""
import tensorflow as tf
from tensorflow import keras
import os
from pydub import AudioSegment
import math

# Export models to other formats
def convert_func():
    # Convert .h5 model to .pb
    model = keras.models.load_model('save/nn_model.h5', compile=False)

    export_path = 'save/pb'
    model.save(export_path, save_format='tf')

    # Convert Tensorflow Lite model for app
    saved_model_dir = 'save/pb'
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS,
                                       tf.lite.OpsSet.SELECT_TF_OPS]
    tflite_model = converter.convert()
    open('save/test1.tflite', 'wb').write(tflite_model)


# Change name of audio file
def name_change():
    file_path = 'Dataset/noise'
    file_names = os.listdir(file_path)
    i = 1
    for name in file_names:
        src = os.path.join(file_path, name)
        dst = str(i)+'.wav'
        dst = os.path.join(file_path, dst)
        os.rename(src,dst)
        i+=1
 

# Trim audio files for 5 seconds
def trim_func():
    # Audio file path
    audio = AudioSegment.from_wav("test-1.wav")
    seconds = 5 * 1000 # set 5 seconds

    for i in range(int(math.floor(len(audio)/seconds))):
        slice = audio[i*seconds:seconds*(i+1)]
        slice.export('Dataset/trim_test/{}.wav'.format(i), format='wav')

    




