//package com.example.acousticuavdetection
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.os.Environment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.androidbuffer.kotlinfilepicker.KotConstants
//import com.androidbuffer.kotlinfilepicker.KotRequest
//import com.androidbuffer.kotlinfilepicker.KotResult
//import com.example.acousticuavdetection.databinding.FragmentServerBinding
//import com.test.libsvmandroidexample.PredictFragment.AsyncPredictTask
//import com.test.libsvmandroidexample.Utility.isEmptyOrWhitespace
//import umich.cse.yctung.androidlibsvm.LibSVM
//import java.io.File
//import java.io.FileInputStream
//
//
//class PredictSVM : Fragment() {
//    private var svm: LibSVM? = null
//    val systemPath: String = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
//    //val appFolderPath = systemPath + "libsvm/" // your datasets folder
//    val appFolderPath = "${Environment.getExternalStorageDirectory()}/uav_audio"
//    var inputFile: File? = null
//    var fileInputStream: FileInputStream? = null
//    var fileBody = "audio_feature"
//    var filePath: String? = "${Environment.getExternalStorageDirectory()}/uav_audio"
//    private lateinit var binding_server: FragmentServerBinding
//    var commands: MutableList<String> = ArrayList()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding_server = FragmentServerBinding.inflate(layoutInflater)
//
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        KotRequest.File(this.requireActivity(), requestCode = REQUEST_FILE).isMultiple(false).setMimeType(KotConstants.FILE_TYPE_FILE_ALL).pick()
//
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    fun startPredict() {
//        svm = LibSVM()
//        svm!!.predict(appFolderPath + "audio_feature " + appFolderPath + "model " + appFolderPath + "result")
//        Toast.makeText(
//            getContext(),
//            "SVM Predict has executed successfully!",
//            Toast.LENGTH_LONG
//        ).show()
//    }
//
//    fun Predict() {
//
//        /*
//        if (probabilityCheckbox.isChecked()) {
//            if (!isEmptyOrWhitespace(probabilityInput.getText().toString())) {
//                commands.add("-b")
//                commands.add(probabilityInput.getText().toString())
//            }
//        }
//         */
//        commands.add(testFilePicker.getText().toString())
//        commands.add(modelFilePicker.getText().toString())
//        if (isEmptyOrWhitespace(outputFileNameInput.getText().toString())) {
//            Toast.makeText(context, "Output file name is required!", Toast.LENGTH_SHORT).show()
//            return
//        }
//        commands.add(ContainerActivity.appFolderPath + outputFileNameInput.getText().toString())
//        AsyncPredictTask().execute(*commands.toTypedArray())
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (REQUEST_FILE == requestCode && resultCode == Activity.RESULT_OK) {
//            val result = data?.getParcelableArrayListExtra<KotResult>(KotConstants.EXTRA_FILE_RESULTS)
//            commands.add(result!!.get(0).toString())
//        }
//    }
//    /*
//    private abstract inner class AsyncPredictTast {
//        val tv = findViewById(R.id.TV_RESULT) as TextView
//        object : ThreadTask<Int?, Int?>() {
//            protected fun onPreExecute() {}
//            protected fun doInBackground(arg: Int): Int? {
//                var result = 0
//                var index = 0
//                while (index++ < arg) {
//                    result += 1 shl index
//                }
//                return result
//            }
//
//            protected fun onPostExecute(result: Int) {
//                tv.text = result.toString()
//            }
//        }.execute(4)
//    }
//     */
//    companion object {
//        val REQUEST_FILE = 3000
//        var PICK_MODELFILE = 3001
//    }
//
//}