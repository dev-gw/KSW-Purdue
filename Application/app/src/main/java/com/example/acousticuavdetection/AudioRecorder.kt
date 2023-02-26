package com.example.acousticuavdetection

import android.app.Application
import android.content.Context
import android.os.Environment
import com.example.acousticuavdetection.feature.FeatureExtraction
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

class AudioRecorder(context: Context) {
    private var mIsRecording: Boolean? = false
    private var mContext: Context = context
    var filePath:String = "${Environment.getExternalStorageDirectory()}/uav_audio/audio"
    val waveRecorder = WaveRecorder(filePath)
    var divideTimer: Timer? = null
    private var mfcc: FeatureExtraction? = null
    init {
    }
    fun startRecording() {
        mfcc = FeatureExtraction(mContext.applicationContext as Application)
        var fileIndex = 0 //index of file
        val basePath = "${mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio/audio"

        deleteLegacy(basePath, ".wav") //Look for old audio files and delete them
        deleteLegacy("${mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature/audio_feature", "")
        // Look for old extracted feature files and delete them

        mIsRecording = true
        if (mIsRecording as Boolean) {
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
                    mfcc!!.performMfcc(fileIndex) // send .wav audio file's path to MFCC
                    fileIndex++ //increment file name
                    waveRecorder.changeFilePath(
                        basePath + String.format(
                            fileIndex.toString(),
                            "%02d"
                        ) + ".wav"
                    )
                    waveRecorder.startRecording()
                }, 5000, 5200) // record audio data each 5 seconds
        }
    }


    fun stopRecording() {
        mIsRecording = false
        stopTimer()
        waveRecorder.stopRecording()
    }
    fun stopTimer() {
        divideTimer?.cancel() // stop thread
    }

    fun deleteLegacy(filePath: String, fileFormat: String){
        var fileIndex = 0
        while (File(filePath + String.format(fileIndex.toString(), "%02d") + fileFormat).exists()) {
            File(filePath + String.format(fileIndex.toString(), "%02d") + fileFormat).delete()
            fileIndex++
        }
    }
    companion object {
        private const val TAG = "AudioRecorder"
    }
}

