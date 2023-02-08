# -*- coding: utf-8 -*-
"""
For export models

@author: Gwangwon
"""
import tensorflow as tf
from tensorflow import keras

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

