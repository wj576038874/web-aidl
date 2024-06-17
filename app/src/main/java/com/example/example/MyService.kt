package com.example.example

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private val binder = object : IMyAidlInterface.Stub() {

        override fun getUrl(): String {
            return "https://www.baidu.com"
        }

        override fun getNumString(): Int {
            return MyInstance.getInstance().getNum()
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {

        }
    }
}