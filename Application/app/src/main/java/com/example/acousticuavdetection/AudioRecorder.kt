package com.example.acousticuavdetection

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.example.acousticuavdetection.feature.FeatureExtraction
import com.github.squti.androidwaverecorder.WaveRecorder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

class AudioRecorder(context: Context) {
    private var mIsRecording: Boolean? = false
    private var mContext: Context = context
    private var mStartTime: Long = 0
    var filePath:String = "${Environment.getExternalStorageDirectory()}/uav_audio/audio"
    val waveRecorder = WaveRecorder(filePath)
    var divideTimer: Timer? = null
    private var mfcc: FeatureExtraction? = null
    init {
    }
    fun startRecording() {
        mfcc = FeatureExtraction(mContext.applicationContext as Application)
        var fileIndex = 0 //index of file
        //var basePath = "${Environment.getExternalStorageDirectory()}/uav_audio/audio" //Base path of audio files
        val basePath = "${mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio/audio"
        //Look for old audio files and delete them
        deleteLegacy(basePath, ".wav")
        deleteLegacy("${mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature/audio_feature", "")

        mIsRecording = true
        if (mIsRecording as Boolean) {
            //Run on new thread since it'll be in the background
            waveRecorder.changeFilePath(
                basePath + String.format(
                    fileIndex.toString(),
                    "%02d"
                ) + ".wav"
            )
            waveRecorder.waveConfig.sampleRate = 22050
            waveRecorder.startRecording()
            divideTimer = Timer()
            divideTimer!!.scheduleAtFixedRate(
                timerTask {
                    waveRecorder.stopRecording()
                    mfcc!!.performMfcc(fileIndex)
                    fileIndex++ //increment file name
                    waveRecorder.changeFilePath(
                        basePath + String.format(
                            fileIndex.toString(),
                            "%02d"
                        ) + ".wav"
                    )
                    waveRecorder.startRecording()
                }, 5000, 5200)
        }
    }


    fun stopRecording() {
        mIsRecording = false
        stopTimer()
        waveRecorder.stopRecording()
    }
    fun stopTimer() {
        divideTimer?.cancel()
    }

    fun deleteLegacy(filePath: String, fileFormat: String){
        var fileIndex = 0
        while (File(filePath + String.format(fileIndex.toString(), "%02d") + "$fileFormat").exists()) {
            File(filePath + String.format(fileIndex.toString(), "%02d") + "$fileFormat").delete()
            fileIndex++
        }
    }
    companion object {
        private const val TAG = "AudioRecorder"
    }
}

