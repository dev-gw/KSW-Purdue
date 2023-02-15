package com.example.acousticuavdetection

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_phone.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var viewList = ArrayList<View>()
    var timer : Timer? = null
    var deltaTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        viewList.add(layoutInflater.inflate(R.layout.fragment_phone, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_server, null))

        //startBtn.setOnClickListener{ Timerfun() }
        //stopBtn.setOnClickListener{ timer?.cancel() }

        viewPager.adapter = pagerAdapter()

        tabLayout.setupWithViewPager(viewPager) // tab과 viewPager 연결
        tabLayout.getTabAt(0)?.setText("phone")
        tabLayout.getTabAt(1)?.setText("server")
        tabLayout.getTabAt(0)?.setIcon(R.drawable.baseline_speaker_phone_24)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.baseline_device_hub_24)
        /*

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        // Set onClickListener for start button
        startButton.setOnClickListener {
            startRecording()
        }

        // Set onClickListener for stop button
        stopButton.setOnClickListener {
            stopRecording()
        }
        */
    }

    fun Timerfun(){
        timer = timer(period = 100){
            if(deltaTime > 100) cancel()
            progressBarPhone.setProgress(++deltaTime)
            println(progressBarPhone.progress)
        }
    }

    inner class pagerAdapter() : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any) = view == `object` // 뷰랑 오브젝트가 같냐

        override fun getCount() = viewList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var curView = viewList[position]
            viewPager.addView(curView)
            return curView
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            viewPager.removeView(`object` as View)
        }

    }
}