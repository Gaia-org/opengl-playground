package com.example.opengl.samples

import android.app.Application
import android.content.Context

class SampleApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        sInstance = this
    }

    companion object {
        private lateinit var sInstance: SampleApplication

        fun getInstance(): SampleApplication {
            return this.sInstance
        }
    }
}