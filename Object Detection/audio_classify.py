# -*- coding: utf-8 -*-
"""
@author: Gwangwon Kim

"""
import os
import glob
import numpy as np
import pandas as pd
import pickle
import audio_module

import tensorflow as tf


# Setting(For using GPU)
os.chdir('C:\광원\k-sw\활동\KSW-Purdue\Object Detection')
physical_devices = tf.config.list_physical_devices('GPU')
try:
    tf.config.experimental.set_memory_growth(physical_devices[0],True)
except:
    pass

# Data input
autel_df = audio_module.convert_data(100, 'Autel_Evo2', '0')
dji_df = audio_module.convert_data(100, 'DJI_Phantom4', '1')
df = pd.concat([autel_df,dji_df])
df.to_pickle("UAV.pkl") # save data to pickle










