package com.bugtsa.camerafilters.di.presentation

import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.di.TakePhotoScopeWrapper
import com.bugtsa.camerafilters.presentation.FilterPhotoViewModel
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowDataHolder
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowScopeType
import com.bugtsa.camerafilters.presentation.media.TakePhotoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ViewModelModule {
    val module by lazy {
        module {
            viewModel {
                val scopeWrapper = get<TakePhotoScopeWrapper>()
                val takePhotoProvider = object : ScopedInstanceProvider<TakePhotoFlowDataHolder> {
                    override fun provide(vararg arguments: Any?): TakePhotoFlowDataHolder {
                        return scopeWrapper.getOrCreate().get { parametersOf(*arguments) }
                    }
                }
                TakePhotoViewModel(get(), get(), takePhotoProvider)
                    .apply {
                        subscribeOnCleared {
                            scopeWrapper.close()
                        }
                    }
            }

            viewModel {
                val takePhotoScopeWrapper = get<TakePhotoScopeWrapper>()
                val takePhotoProvider = object : ScopedInstanceProvider<TakePhotoFlowDataHolder> {
                    override fun provide(vararg arguments: Any?): TakePhotoFlowDataHolder {
                        return takePhotoScopeWrapper.getOrCreate().get { parametersOf(*arguments) }
                    }
                }
                FilterPhotoViewModel(takePhotoProvider)
            }

            scope(named<TakePhotoFlowScopeType>()) {
                scoped { TakePhotoFlowDataHolder() }
            }
            single {
                TakePhotoScopeWrapper()
            }
        }
    }
}