package com.bugtsa.camerafilters.di.domain

import com.bugtsa.camerafilters.domain.file.FileManagerInteractor
import com.bugtsa.camerafilters.domain.file.FileManagerInteractorImpl
import org.koin.dsl.bind
import org.koin.dsl.module

object InteractorModule {

    val module by lazy {
        module {
            single { FileManagerInteractorImpl(get()) } bind FileManagerInteractor::class
        }
    }
}