package com.example.acousticuavdetection.feature

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.io.File
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import org.merlyn.kotlinspeechfeatures.MathUtils
import org.merlyn.kotlinspeechfeatures.SpeechFeatures
import java.io.FileOutputStream
import java.security.AccessController.getContext
import kotlin.coroutines.CoroutineContext

class FeatureExtraction(application: Application) : AndroidViewModel(application) {
    private val TAG = "AcousticUAVDetection"
    private val speechFeatures = SpeechFeatures()
    var audio_num = 0
    var outputFile: File? = null
    var fileOutputStream: FileOutputStream? = null
    var fileBody = "audio_feature"
    var filePath:String = "${application.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio/"
    val data = arrayOf(arrayOf(1.0f, 2.0f), arrayOf(3.0f, 4.0f))
    fun performMfcc(fileIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //val wav = loadWavFile(fileFromAsset(filePath, "audio${String.format(fileIndex.toString(),"%02d")}.wav"))
            val wav = loadWavFile(File(filePath + "audio${String.format(fileIndex.toString(),"%02d")}.wav"))
            val result = speechFeatures.mfcc(MathUtils.normalize(wav), numCep = 40, nFilt = 26)
            outputFile = File(filePath, "audio_feature${String.format(fileIndex.toString(),"%02d")}")
            fileOutputStream = FileOutputStream(outputFile)
            fileOutputStream!!.write(result.contentToString().toByteArray())
            fileOutputStream!!.close()
            Log.d(TAG, "${result.javaClass}"+"mfcc output:")
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