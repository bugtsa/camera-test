package com.bugtsa.camerafilters.di.presentation

import com.bugtsa.camerafilters.presentation.media.TakePhotoViewModel
import com.bugtsa.camerafilters.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val module by lazy {
        module {
            viewModel {
                TakePhotoViewModel(get(), get())
            }

            viewModel {
                MainViewModel()
            }
        }
    }
}