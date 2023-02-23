package com.example.acousticuavdetection

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CheckPermission {
    fun checkNeededPermissions() {
        println("Requesting permission")
        if (ContextCompat.checkSelfPermission(MainActivity.ApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.ApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.ApplicationContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.ApplicationContext(), Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.ApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            println("Requesting permission")
            ActivityCompat.requestPermissions(MainActivity.instance,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE), 0)
        }
    }
}