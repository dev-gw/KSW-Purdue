package com.example.acousticuavdetection

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.provider.MediaStore.Audio.Media
import androidx.core.os.EnvironmentCompat
import java.io.File
import java.io.IOException

class AudioRecorder {
    private var mRecorder: MediaRecorder? = null
    private var mOutputFile: String? = null
    private var mStartTime: Long = 0
    private var mFileNumber = 0

    init {
    }
    fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder!!.setAudioEncodingBitRate(128000);
        mRecorder!!.setAudioSamplingRate(48000);
        mRecorder!!.setOutputFile(getOutputFile())
        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace() // handle error
        }
        mRecorder!!.start()
        mStartTime = System.currentTimeMillis()
    }

    fun stopRecording() {
        mRecorder?.run {
            mRecorder!!.stop()
            mRecorder!!.release()
        }
        mRecorder = null
    }

    private fun getOutputFile(): String {
        mFileNumber++
        return "${mOutputFile ?: ""}$mFileNumber.wav"
    }

    fun splitAudioFile() {
        if (mRecorder == null) {
            return
        }
        val elapsedTime = System.currentTimeMillis() - mStartTime
        if (elapsedTime >= 1000) {
            stopRecording()
            startRecording()
        }
    }

    fun setOutputFile(outputFile: String) {
        mOutputFile = outputFile
    }
}
