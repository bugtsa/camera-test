package com.bugtsa.camerafilters

import androidx.multidex.MultiDexApplication
import com.bugtsa.camerafilters.di.CameraFiltersGraph

class CameraApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        CameraFiltersGraph.initialize(this)
    }
}