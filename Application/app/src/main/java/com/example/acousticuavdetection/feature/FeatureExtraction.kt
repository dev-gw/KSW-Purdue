package com.example.acousticuavdetection.feature

import android.app.Application
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.io.File
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merlyn.kotlinspeechfeatures.MathUtils
import org.merlyn.kotlinspeechfeatures.SpeechFeatures

class FeatureExtraction(application: Application) : AndroidViewModel(application) {
    private val TAG = "AcousticUAVDetection"
    private val speechFeatures = SpeechFeatures()

    fun performMfcc() {
        viewModelScope.launch(Dispatchers.IO) {
            val wav = loadWavFile(fileFromAsset("audioSample", "english.wav"))
            val result = speechFeatures.mfcc(MathUtils.normalize(wav), nFilt = 64)
            Log.d(TAG, "mfcc output:")
            result.forEach {
                Log.d(TAG, it.contentToString())
            }
        }
    }

    private fun loadWavFile(file: File): IntArray {
        val wavFile = WavFile.openWavFile(file)
        val numFrames = wavFile.numFrames.toInt()
        val channels = wavFile.numChannels
        val loopCounter: Int = numFrames * channels / 4096+1
        val intBuffer = IntArray(numFrames)
        for (i in 0 until loopCounter){
            wavFile.readFrames(intBuffer, numFrames)
        }
        return intBuffer
    }

    private fun fileFromAsset(directory: String, name: String): File { // Need to modify for collected audio data
        val context = getApplication<Application>()
        val cacheDir = context.cacheDir
        return File("$cacheDir/$name").apply { writeBytes(context.assets.open("$directory/$name").readBytes()) }
    }
}