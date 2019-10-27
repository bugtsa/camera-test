package com.bugtsa.camerafilters.presentation

import com.bugtsa.camerafilters.di.ScopeHost
import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowDataHolder

class FilterPhotoViewModel(override val provider: ScopedInstanceProvider<TakePhotoFlowDataHolder>) :
    RxViewModel(),
    ScopeHost<TakePhotoFlowDataHolder> by ScopeHost.Delegate(provider) {

    override fun onCleared() {
        super.onCleared()
        scopeClosed()
    }

}
