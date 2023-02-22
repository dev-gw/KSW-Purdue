package com.example.acousticuavdetection.feature

import android.app.Application
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.io.File
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.acousticuavdetection.AudioClassificationHelper
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import com.jlibrosa.audio.JLibrosa

class FeatureExtraction(application: Application) : AndroidViewModel(application) {
    private val TAG = "AcousticUAVDetection"
    private val jLibrosa = JLibrosa()

    var outputFile: File? = null
    var fileOutputStream: FileOutputStream? = null
    var filePath:String = "${application.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}"
    fun performMfcc(fileIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val audioFeatureValues = jLibrosa.loadAndRead(filePath + "/uav_audio/audio${String.format(fileIndex.toString(),"%02d")}.wav", 22050, -1)
            val result = jLibrosa.generateMFCCFeatures(audioFeatureValues, 22050, 40, 2048, 128,512)
            val process_result = to2DFloatArray(result)
            val audioHelper = AudioClassificationHelper(context = getApplication(), mfccFeature = process_result)
            outputFile = File("${filePath}/uav_feature/", "audio_feature${String.format(fileIndex.toString(),"%02d")}")
            fileOutputStream = FileOutputStream(outputFile)
            fileOutputStream!!.write(process_result.contentToString().toByteArray())
            fileOutputStream!!.close()
//            result.forEach {
//                Log.d(TAG, it.contentToString())
//            }
        }
    }

    private fun to2DFloatArray(array: Array<FloatArray>): FloatArray {
        val rows = array.size
        val cols = array[0].size
        var sum = 0F
        val result = FloatArray(40)
        for (i in 0 until rows) {
            sum = 0F
            for (j in 0 until cols) {
                sum += array[i][j]
            }
            result[i] = sum / cols
        }
        return result
    }

}