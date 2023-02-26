package com.example.acousticuavdetection

import android.util.Log
import org.tensorflow.lite.Interpreter


class AudioClassificationHelper(
    var mfccFeature: FloatArray,
) {
    val tflite: Interpreter? = MainActivity.instance.getTfliteInterpreter("tf_svm_model2.tflite") // call model file

    init {
        initClassifier()
    }
    fun initClassifier() {
        try {
            startAudioClassification()
        } catch (e: IllegalStateException) {
            Log.e("AudioClassification", "TFLite failed to load with error: " + e.message)
        }
    }
    fun startAudioClassification() {
        classifyAudio()
    }
    private fun classifyAudio() {
        var inferenceTime = System.nanoTime() // to measure time
        var outputs = Array(1) { FloatArray(3) } // output of model result's size is [1][3]
        tflite?.run(mfccFeature, outputs) // run model, input/output data
        inferenceTime = System.nanoTime() - inferenceTime // time measurement end
        MainActivity.instance.changePhoneInferenceTime(inferenceTime) // display measurement time
        if (outputs[0][2] > outputs[0][1] && outputs[0][2] > outputs[0][0]){
            MainActivity.instance.changePhoneToNoise() // If [0][2] data is the largest, change radar color green
        }
        else {
            MainActivity.instance.changePhoneToUAV() //or, change radar color Red, Drone detected
        }
        MainActivity.instance.changePhoneResultText(outputs[0][0].toString() + "$\n" + outputs[0][1].toString() + "$\n" + outputs[0][2].toString() )
//        Log.d(TAG, outputs[0][0].toString()) // Autel Evo
//        Log.d(TAG, outputs[0][1].toString()) // EJI Phantom 4
//        Log.d(TAG, outputs[0][2].toString()) // Noise
//        Log.d(TAG, "$inferenceTime")
    }
    companion object {
        private const val TAG = "AudioClassificationHelper"
    }
}
