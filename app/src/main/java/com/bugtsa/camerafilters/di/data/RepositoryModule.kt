package com.bugtsa.camerafilters.di.data

import com.bugtsa.camerafilters.data.file.FileManagerRepository
import com.bugtsa.camerafilters.data.file.FileManagerRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

object RepositoryModule {

    val module by lazy {
        module {
            single { FileManagerRepositoryImpl(get()) } bind FileManagerRepository::class
        }
    }
}