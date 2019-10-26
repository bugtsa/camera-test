package com.bugtsa.camerafilters.di.data

import com.bugtsa.camerafilters.data.file.FileManagerUtil
import com.bugtsa.camerafilters.data.file.FileManagerUtilImpl
import org.koin.dsl.bind
import org.koin.dsl.module

object RepositoryModule {

    val module by lazy {
        module {
            single { FileManagerUtilImpl(get())} bind FileManagerUtil::class
        }
    }
}