# -*- coding: utf-8 -*-
"""
For trimming audio data

@author: Gwangwon
"""
# Audio trim test
from pydub import AudioSegment
import math
audio = AudioSegment.from_wav("") # file path
seconds = 5 * 1000

for i in range(int(math.floor(len(audio)/seconds))):
    slice = audio[i*seconds:seconds*(i+1)]
    slice.export('Dataset/trim_test/{}'.format(i), format='wav')
    
