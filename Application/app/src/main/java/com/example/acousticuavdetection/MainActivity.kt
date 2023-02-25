package com.example.acousticuavdetection

import com.example.acousticuavdetection.network.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.acousticuavdetection.databinding.ActivityMainBinding
import com.example.acousticuavdetection.feature.FeatureExtraction
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Timer
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    init{
        instance = this
    }
    private lateinit var binding_main: ActivityMainBinding
    var viewList = ArrayList<View>()
    private lateinit var mStartButton : Button
    private var mRecorder: AudioRecorder? = null
    private var mIsRecording = false
    private val viewModel: FeatureExtraction by viewModels()
    private var timer = Timer()
    private var startTime: Long? = null
    private var endTime: Long? = null
    lateinit var GClientService: ClientService;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkPermission = CheckPermission()
        checkPermission.checkNeededPermissions()

        binding_main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding_main.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide() // Hide Top Application bar with application name

        // check directory is exist
        if (!(File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio").exists())){
            File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_audio").mkdir()
        }
        if (!(File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature").exists())){
            File("${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/uav_feature").mkdir()
        }

        // Instantiate Service object. Service objects create and manage session.
        // Communication with server can be done through service objects.
        GClientService = ClientService(ServiceType.Client, NetAddress("192.168.227.141", 632), ServerSession(),1, this);
        

        // Make another thread for receiving data from the server.
        thread(start=true) {
        assert(GClientService.Start());
            while (true)
            {
                GClientService.RecvData();
            };
        }
    }


    fun fabClick(view: View) {
        if (!mIsRecording) {
            val buttonAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.blink);
            startRecording()
            mIsRecording = !mIsRecording
            runOnUiThread(
                Runnable {
                    binding_main.fab.startAnimation(buttonAnimation); // record button blink animation start
                }
            )
        } else {
            stopRecording()
            mIsRecording = !mIsRecording
            runOnUiThread(
                Runnable {
                    changePhoneToNoise()
                    binding_main.fab.clearAnimation() // record button blink animation end
                }
            )
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
    fun changePhoneToUAV(){
        runOnUiThread {
            binding_main.progressBar1.visibility = GONE
            binding_main.progressBar2.visibility = VISIBLE
            Toast.makeText(this, "changePhoneToUAV", Toast.LENGTH_SHORT).show()
        }
    }
    fun changePhoneToNoise(){
        runOnUiThread {
            binding_main.progressBar1.visibility = VISIBLE
            binding_main.progressBar2.visibility = GONE
            Toast.makeText(this, "changePhoneToNoise", Toast.LENGTH_SHORT).show()
        }
    }
    fun changePhoneInferenceTime(text: Long){
        runOnUiThread{
            binding_main.textView.text = "${text} ns"
        }
    }
    fun checkPhoneSwitch(): Boolean {
        return binding_main.switch1.isChecked
    }

    fun networkInferenceTimerStart() {
        startTime = System.nanoTime()
    }
    fun networkInferenceTimerEnd() {
        endTime = System.nanoTime() - startTime!!
        changePhoneInferenceTime(endTime!!)
        startTime = 0
        endTime = 0
    }
    fun changePhoneResultText(result: String) {
        runOnUiThread {
            binding_main.textView2.text = result
        }
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
    fun getTfliteInterpreter(modelPath: String): Interpreter? { // model call function
        try {
            return Interpreter(loadModelFile(instance, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // lode tflite model file from assets folder
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, modelPath: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.getChannel()
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun changeResult(result: Int)
    {
        networkInferenceTimerEnd()
        when (result as UInt)
        {
            DetectionResult.Noise.id -> {
                binding_main.progressBar2.visibility = GONE; binding_main.progressBar1.visibility = VISIBLE; }
            else -> { binding_main.progressBar1.visibility = GONE; binding_main.progressBar2.visibility = VISIBLE; }
        }
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MainActivity
        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
    }
}
