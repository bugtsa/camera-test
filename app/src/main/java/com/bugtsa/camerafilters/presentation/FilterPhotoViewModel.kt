package com.bugtsa.camerafilters.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bugtsa.camerafilters.di.ScopeHost
import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowDataHolder

class FilterPhotoViewModel(override val provider: ScopedInstanceProvider<TakePhotoFlowDataHolder>) :
    RxViewModel(),
    ScopeHost<TakePhotoFlowDataHolder> by ScopeHost.Delegate(provider) {

    private val sourceUri by lazy { provider.provide().sourceUri }
    private val destinationUri by lazy { provider.provide().destinationUri }

    private val showBitmapLiveData = MutableLiveData<Uri>()
    fun observeShowBitmap(): LiveData<Uri> = showBitmapLiveData

    override fun onCleared() {
        super.onCleared()
        scopeClosed()
    }

    fun getBitmap() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val source = sourceUri
        val destination = destinationUri
//        val bitmap = BitmapFactory.decodeFile(source.toString(), options)
        showBitmapLiveData.postValue(source)
    }

}
