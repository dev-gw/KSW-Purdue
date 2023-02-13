# -*- coding: utf-8 -*-
"""
For trimming audio data

@author: Gwangwon
"""
import os
# Audio trim test
os.chdir('C:\광원\k-sw\활동\KSW-Purdue\Object Detection')

from pydub import AudioSegment
import math
audio = AudioSegment.from_wav("test-1.wav") # file path
seconds = 5 * 1000

for i in range(int(math.floor(len(audio)/seconds))):
    slice = audio[i*seconds:seconds*(i+1)]
    slice.export('Dataset/trim_test/{}.wav'.format(i), format='wav')
    
