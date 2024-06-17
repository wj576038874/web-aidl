package com.example.example


import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyJavaScripInterface(private val context: AppCompatActivity) {


    @JavascriptInterface
    fun showToast(message: String) {
        context.runOnUiThread {
            val processName = Utils.getProcessName(context)
            Toast.makeText(context, processName + message, Toast.LENGTH_SHORT).show()
        }
    }

}