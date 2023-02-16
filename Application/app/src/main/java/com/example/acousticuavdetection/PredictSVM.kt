package com.example.acousticuavdetection

import android.os.Environment
import umich.cse.yctung.androidlibsvm.LibSVM

class PredictSVM {
    private var svm: LibSVM? = null
    val systemPath: String = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
    val appFolderPath = systemPath + "libsvm/" // your datasets folder
    fun startPridict() {
        svm = LibSVM()
        svm!!.predict(appFolderPath + "hear_scale_predict " + appFolderPath + "model " + appFolderPath + "result")
    }
}