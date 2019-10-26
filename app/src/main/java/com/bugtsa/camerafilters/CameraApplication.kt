package com.bugtsa.camerafilters

import androidx.multidex.MultiDexApplication
import com.bugtsa.camerafilters.di.CameraGraph

class CameraApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        CameraGraph.initialize(this)
    }
}