package com.misoca.radioplayer

import android.app.Application
import android.content.Intent

class RadioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent(applicationContext, RadioService::class.java))
    }
}