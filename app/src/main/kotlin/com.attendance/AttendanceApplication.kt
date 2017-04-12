package com.attendance

import android.app.Application
import android.content.Context

class AttendanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
    }

}