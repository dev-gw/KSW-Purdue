package com.example.acousticuavdetection.feature

import android.app.Application
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.io.File
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.acousticuavdetection.AudioClassificationHelper
import com.example.acousticuavdetection.MainActivity
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import com.jlibrosa.audio.JLibrosa
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

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
            val process_result = dataStandardization(to2DFloatArray(result))


            /* --------------------
                Send data to Server
               -------------------- */
            CoroutineScope(Dispatchers.IO).launch { MainActivity.instance.GClientService.SendAudioData(process_result); }



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
        var sum = 0F
        val result = FloatArray(41)
        for (i in array.indices) {
            sum = 0F
            for (j in array[i].indices) {
                sum += array[i][j]
            }
            result[i] = sum / array[i].size
        }
        return result
    }

    private fun dataStandardization(numArray: FloatArray): FloatArray {
        var avg: Double
        var sum = 0.0
        var variance = 0.0
        var standardDeviation: Double

        val result = FloatArray(41)
        for (num in numArray) {
            sum += num
        }

        avg = sum / numArray.size

        for (num in numArray) {
            variance += (num - avg).pow(2.0)
        }
        standardDeviation = sqrt(variance / numArray.size)

        for (i in numArray.indices) {
            result[i] = ((numArray[i]-avg) / standardDeviation).toFloat()
        }
        return result
    }

}