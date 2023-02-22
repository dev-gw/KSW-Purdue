package com.example.acousticuavdetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.example.acousticuavdetection.databinding.ActivityMainBinding
import com.example.acousticuavdetection.databinding.FragmentPhoneBinding
import com.example.acousticuavdetection.databinding.FragmentServerBinding
import com.example.acousticuavdetection.feature.FeatureExtraction
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    init{
        instance = this
    }
    private lateinit var binding_main: ActivityMainBinding
    private lateinit var binding_phone: FragmentPhoneBinding
    private lateinit var binding_server: FragmentServerBinding
    var viewList = ArrayList<View>()
    private lateinit var mStartButton : Button
    private var mRecorder: AudioRecorder? = null
    private var mIsRecording = false
    private val viewModel: FeatureExtraction by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNeededPermissions()

        binding_main = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding_main.root)
        binding_phone = FragmentPhoneBinding.inflate(layoutInflater)
        setContentView(binding_phone.root)
        binding_server = FragmentServerBinding.inflate(layoutInflater)
        val tab1 = binding_main.tabLayout.getTabAt(0)
        tab1?.setCustomView(R.layout.fragment_phone)
        val tab1Binding = tab1?.customView?.let {
            FragmentPhoneBinding.bind(it)
        }

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide() // Hide Top Application bar with application name

        viewList.add(layoutInflater.inflate(R.layout.fragment_phone, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_server, null))

        binding_main.viewPager.adapter = pagerAdapter()

        binding_main.tabLayout.setupWithViewPager(binding_main.viewPager) // tab과 viewPager 연결
                binding_main.tabLayout.getTabAt(0)?.setText("phone")
                binding_main.tabLayout.getTabAt(1)?.setText("server")
                binding_main.tabLayout.getTabAt(0)?.setIcon(R.drawable.baseline_speaker_phone_24)
                binding_main.tabLayout.getTabAt(1)?.setIcon(R.drawable.baseline_device_hub_24)
        if (!(File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio").exists())){
            File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio").mkdir()
        }
        if (!(File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature").exists())){
            File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature").mkdir()
        }
    }


    fun fabClick(view: View) {
        if (!mIsRecording) {
            startRecording()
            mIsRecording = !mIsRecording
            runOnUiThread(
                Runnable {

                }
            )
        } else {
            stopRecording()
            mIsRecording = !mIsRecording
            runOnUiThread(
                Runnable {

                }
            )
        }
    }

    fun fab2Click (view: View) {
        Toast.makeText(this, "MFCC called. Check logcat.", Toast.LENGTH_LONG).show()
    }
    private fun checkNeededPermissions() {
        println("Requesting permission")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            println("Requesting permission")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun startRecording() {
        mRecorder = AudioRecorder(context = this)
        mRecorder?.startRecording()
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
    }
    private fun stopRecording() {
        mRecorder?.stopTimer()
        mRecorder?.stopRecording()
        mRecorder = null
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
    }
    inner class pagerAdapter() : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any) = view == `object` // 뷰랑 오브젝트가 같냐

        override fun getCount() = viewList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var curView = viewList[position]
            binding_main.viewPager.addView(curView)
            return curView
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            binding_main.viewPager.removeView(`object` as View)
        }

    }
    fun changePhoneToUAV(){
        runOnUiThread {
            binding_phone.progressBar1.visibility = GONE
            binding_phone.progressBar2.visibility = VISIBLE
            Toast.makeText(this, "changePhoneToUAV", Toast.LENGTH_SHORT).show()
        }
    }
    fun changePhoneToNoise(){
        runOnUiThread {
            binding_phone.progressBar1.visibility = VISIBLE
            binding_phone.progressBar2.visibility = GONE
            Toast.makeText(this, "changePhoneToNoise", Toast.LENGTH_SHORT).show()
        }
    }
    fun changePhoneInferenceTime(text: Long){
        runOnUiThread{
            binding_phone.textView.text = "${text}ms"
        }
    }
    inner class classificationListner() {

    }
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
    fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(instance, modelPath)!!)
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
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MainActivity
        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
    }
}

