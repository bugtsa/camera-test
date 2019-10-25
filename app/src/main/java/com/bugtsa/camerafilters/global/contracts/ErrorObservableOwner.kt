package com.bugtsa.camerafilters.global.contracts

import androidx.lifecycle.LiveData

interface ErrorObservableOwner {

    fun observeErrorLiveData(): LiveData<String>
}
