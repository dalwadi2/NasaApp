package dev.harshdalwadi.nasaapp

import android.app.Activity
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    private val TAG = MyApp::class.java.simpleName

    operator fun get(activity: Activity): MyApp {
        return activity.application as MyApp
    }

    companion object {
        var mInstance: MyApp? = null

        @Synchronized
        fun getInstance(): MyApp? {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}