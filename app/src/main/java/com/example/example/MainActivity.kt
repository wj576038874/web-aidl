package com.example.example

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var targetId = 0

    private lateinit var activityManager: ActivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MyInstance.getInstance().setNum(100)

        findViewById<Button>(R.id.btn).setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", "https://m.jd.com")
            startActivity(intent)
        }

        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        activityManager.getRunningTasks(Int.MAX_VALUE).forEach {
//            if(it.baseActivity?.packageName == "com.example.example"){
//                Log.e("asd" , it.baseActivity?.packageName.toString())
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    targetId = it.taskId
//                }
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
//        activityManager.moveTaskToFront(targetId , 0)
    }
}