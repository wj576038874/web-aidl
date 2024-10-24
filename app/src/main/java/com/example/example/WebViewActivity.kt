package com.example.example

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    private var binder: IMyAidlInterface? = null

    private lateinit var countDownLatch: CountDownLatch

    private lateinit var progressBar: WebViewProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.web)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        webView = findViewById(R.id.web)
        progressBar = findViewById(R.id.progress)

        Toast.makeText(this, MyInstance.getInstance().getNum().toString(), Toast.LENGTH_SHORT)
            .show()

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavaScripInterface(this), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.setProgress(newProgress)
            }
        }

        val url = intent.getStringExtra("url")


        countDownLatch = CountDownLatch(1)

        thread {
            val intent = Intent(this, MyService::class.java)
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            Log.e("asd", "bindService" + Thread.currentThread().name)
            try {
                countDownLatch.await()//链接binder之后，此线程等待，直到binder链接成功之后会调用countDownLatch.countDown() 继续执行当前线程后的代码
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //等待子线程链接binder 获取到binder之后 就可以调用binder的方法 拿到主进程的数据了
            // 拿到binder成功之后调用countDownLatch.countDown() 此线程继续往下执行，然后调用binder拿到url
            // 切换到主线程加载webview

            Log.e("asd", "bindService" + Thread.currentThread().name + binder?.numString)

            runOnUiThread {
                webView.loadUrl(binder?.url!!)
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = IMyAidlInterface.Stub.asInterface(service)
            countDownLatch.countDown()//binder链接成功之后 通知线程继续执行
            Log.e(
                "asd",
                "onServiceConnected" + Thread.currentThread().name + binder?.numString
            )
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }
}