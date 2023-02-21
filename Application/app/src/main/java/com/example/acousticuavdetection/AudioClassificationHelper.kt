/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.acousticuavdetection

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import com.example.acousticuavdetection.databinding.FragmentPhoneBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.core.BaseOptions
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.ScheduledThreadPoolExecutor


class AudioClassificationHelper(
    val context: Context,
    var mfccFeature: FloatArray,
    var currentModel: String = UAV_MODEL,
) {
    private lateinit var binding_phone: FragmentPhoneBinding
    val tflite: Interpreter? = getTfliteInterpreter("tf_svm_model.tflite")

    private val classifyRunnable = Runnable {
        classifyAudio()
    }
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
        var inferenceTime = SystemClock.uptimeMillis()
        val outputs = Array(1) { FloatArray(3) }
        tflite?.run(mfccFeature, outputs)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        MainActivity.instance.changePhoneInferenceTime(inferenceTime)
        if (outputs[0][2] > outputs[0][1] && outputs[0][2] > outputs[0][1]){
            MainActivity.instance.changePhoneToNoise()
        }
        else
            MainActivity.instance.changePhoneToUAV()
        Log.d(TAG, outputs[0][0].toString()) // Autel Evo
        Log.d(TAG, outputs[0][1].toString()) // EJI Phantom 4
        Log.d(TAG, outputs[0][2].toString()) // Noise
        Log.d(TAG, "$inferenceTime")
    }
    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(MainActivity.instance, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, modelPath: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.getChannel()
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    companion object {
        private const val TAG = "AudioClassificationHelper"
        const val UAV_MODEL = "tf_svm_model.tflite"
    }
}
