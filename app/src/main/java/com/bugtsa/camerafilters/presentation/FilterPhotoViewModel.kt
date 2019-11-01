package com.bugtsa.camerafilters.presentation

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.di.ScopeHost
import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowDataHolder
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class FilterPhotoViewModel(
    application: Application,
    override val provider: ScopedInstanceProvider<TakePhotoFlowDataHolder>
) :
    RxAndroidViewModel(application),
    ScopeHost<TakePhotoFlowDataHolder> by ScopeHost.Delegate(provider) {

    private val showPhotoLiveData = MutableLiveData<ShowPhotoState>()
    fun observeShowPhotoStates(): LiveData<ShowPhotoState> = showPhotoLiveData

    override fun onCleared() {
        super.onCleared()
        scopeClosed()
    }

    fun getBitmap() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        provider.provide().sourceUri?.also { uri ->
            showPhotoLiveData.value = ShowPhotoState.SourceImagePhotoState(uri)
        }
    }

    fun processFiltersList(filter: Filter, sourceBitmap: Bitmap) {
        val brightnessFinal = 0
        val saturationFinal = 1.0f
        val contrastFinal = 1.0f
        filter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        filter.addSubFilter(ContrastSubFilter(contrastFinal))
        filter.addSubFilter(SaturationSubfilter(saturationFinal))

        viewModelScope.launch(Dispatchers.Main) {
            processFilteredBitmap(filter, sourceBitmap)
        }
    }

    private suspend fun processFilteredBitmap(filter: Filter, sourceBitmap: Bitmap) {
        val flowFilter: Flow<Bitmap> = flow {
            val filteredImage = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            emit(filter.processFilter(filteredImage))
        }
        val bitmap = flowFilter
            .flowOn(Dispatchers.Main)
            .single()
        showPhotoLiveData.postValue(ShowPhotoState.FilteredImagePhotoState(bitmap))
    }

    suspend fun prepareThumbnail(bitmap: Bitmap) {
        val flowThumbManager: Flow<MutableList<ThumbnailItem>> = flow {

            val thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false)

            ThumbnailsManager.clearThumbs()
            showPhotoLiveData.postValue(ShowPhotoState.ClearFiltersListPhotoState)

            // add normal bitmap first
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName =
                getApplication<Application>().baseContext.getString(R.string.filter_normal)
            ThumbnailsManager.addThumb(thumbnailItem)

            val filters = FilterPack.getFilterPack(getApplication<Application>().baseContext)

            for (filter in filters) {
                val tI = ThumbnailItem()
                tI.image = thumbImage
                tI.filter = filter
                tI.filterName = filter.name
                ThumbnailsManager.addThumb(tI)
            }
            val thumbs = ThumbnailsManager.processThumbs(getApplication<Application>().baseContext)
            emit(thumbs)
        }
        val result = flowThumbManager
            .flowOn(Dispatchers.Main)
            .single()

        showPhotoLiveData.postValue(ShowPhotoState.FiltersListPhotoState(result))
    }
}

sealed class ShowPhotoState {
    class SourceImagePhotoState(val uri: Uri) : ShowPhotoState()
    class FilteredImagePhotoState(val bitmap: Bitmap) : ShowPhotoState()
    object ClearFiltersListPhotoState : ShowPhotoState()
    class FiltersListPhotoState(val filtersList: MutableList<ThumbnailItem>) : ShowPhotoState()
}
