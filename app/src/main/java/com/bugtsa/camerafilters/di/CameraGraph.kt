package com.bugtsa.camerafilters.di

import android.app.Application
import com.bugtsa.camerafilters.di.data.RepositoryModule
import com.bugtsa.camerafilters.di.domain.InteractorModule
import com.bugtsa.camerafilters.di.presentation.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object CameraGraph {

    private lateinit var application: Application

    fun initialize(application: Application) {
        this.application = application

        assembleGraph()
    }

    private fun assembleGraph(): KoinApplication {
        val platformGraph = listOf(
            RepositoryModule.module,
            InteractorModule.module,
            ViewModelModule.module
        )

        return startKoin {
            androidLogger()
            androidContext(application)
            androidFileProperties()
            modules(platformGraph)
        }
    }
}